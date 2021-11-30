package tech.poder.overlay.api

import jdk.incubator.foreign.*
import tech.poder.overlay.data.ExternalStorage
import tech.poder.overlay.data.NativeBuffer
import tech.poder.overlay.data.Process
import tech.poder.overlay.data.RectReader
import tech.poder.overlay.handles.WinAPIHandles
import tech.poder.overlay.utils.NativeUtils
import tech.poder.overlay.values.WinAPIValues
import tech.poder.overlay.window.WindowManager
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.random.Random
import kotlin.system.exitProcess

object WinAPI {

	private val registry = ConcurrentSkipListMap<Long, Any>()

	private var isCoInitialized = false

	internal var isFirstDraw = false

	internal val repaintLock = ReentrantReadWriteLock()

	val forEachWindowUpcall = NativeUtils.lookupStaticMethodUpcall(
		this::class.java,
		"forEachWindow",
		Boolean::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java
		)
	)


	var currentImageList = MemoryAddress.NULL
	var images = 0

	var lastWindow = WindowManager(MemoryAddress.NULL)

	fun loadImage(pathString: ExternalStorage): MemoryAddress {

		val res = WinAPIHandles.loadImageW(MemoryAddress.NULL, pathString.segment.address(), 0, 0, 0, 0x00000010) as MemoryAddress

		check(res != MemoryAddress.NULL) {
			"LoadImage failed: ${WinAPIHandles.getLastError()}"
		}

		return res
	}

	private fun repaint(hwnd: MemoryAddress) {

		lastWindow.startPaint()

		if (images > 0) {

			isFirstDraw = true

			val dc = WinAPIHandles.getDC(hwnd) as MemoryAddress

			repaintLock.read {
				repeat(images) {
					check(WinAPIHandles.imageListDraw(currentImageList, it, dc, 0, 0, 0) != 0) {
						"ImageList_Draw failed: ${WinAPIHandles.getLastError()}"
					}
				}
			}

			WinAPIHandles.releaseDC(hwnd, dc)
		}

		lastWindow.endPaint()
	}

	@JvmStatic
	@Suppress("unused")
	fun hookProc(hwnd: MemoryAddress, uMsg: Int, wParam: MemoryAddress, lParam: MemoryAddress): MemoryAddress {

		if (lastWindow.window == MemoryAddress.NULL) {
			lastWindow = WindowManager(hwnd)
		}

		return when (uMsg) {
			0x000f -> {
				repaint(hwnd)
				MemoryAddress.NULL
			}
			0x0002, 0x0010 -> {
				exitProcess(0)
			}
			else -> {
				println("Unhandled Called: $uMsg")
				WinAPIHandles.defWindowProcW(hwnd, uMsg, wParam, lParam) as MemoryAddress
			}
		}
	}


	fun isMainWindow(hwnd: MemoryAddress): Boolean {
		return (
			WinAPIHandles.isWindowVisible.invoke(hwnd) as Int != 0 &&
			WinAPIHandles.getWindow.invoke(hwnd, 4) == MemoryAddress.NULL
		)
	}


	fun getProcesses(): List<Process> {

		val confinedStatic = ResourceScope.newConfinedScope()
		val processes = mutableListOf<MemoryAddress>()

		tempRegister(processes) { id ->

			val idLocation = MemoryAddress.ofLong(id)
			val result = WinAPIHandles.enumWindows(forEachWindowUpcall, idLocation.address()) as Byte

			check(result != 0.toByte()) {
				"Callback failed"
			}
		}

		val PROCESS_QUERY_INFORMATION = 1024
		//val PROCESS_VM_READ = 16
		//val PROCESS_VM_WRITE = 32
		val rectPlaceholder = MemorySegment.allocateNative(CLinker.C_LONG.byteSize() * 4, confinedStatic)
		val denseProcesses = mutableListOf<Process>()

		processes.forEach { processData ->

			val pidSegment = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), confinedStatic)

			check(WinAPIHandles.getWindowThreadProcessId(processData, pidSegment.address()) as Int != 0) {
				"Could not get pid"
			}

			val pid = MemoryAccess.getInt(pidSegment)
			val processHandle = WinAPIHandles.openProcess(PROCESS_QUERY_INFORMATION, 0, pid) as MemoryAddress

			if (processHandle == MemoryAddress.NULL) {

				val code = WinAPIHandles.getLastError() as Int

				if (code != 5) {
					println("Error Code: ${code}:")
				}
			}

			val exeName = NativeUtils.getExpanding { size, segment ->

				MemoryAccess.setByte(segment, 0)

				val used =
					if (processHandle == MemoryAddress.NULL) {
						WinAPIHandles.getWindowModuleFileNameA(processData, segment.address(), size.toInt()) as Int
					}
					else {
						WinAPIHandles.getModuleFileNameExA(processHandle, MemoryAddress.NULL, segment.address(), size.toInt()) as Int
					}

				if (used == 0) {
					println(WinAPIHandles.getLastError())
				}

				if (used >= size) {
					null
				}
				else {
					CLinker.toJavaString(segment)
				}
			}

			if (exeName.contains("C:\\System32") || exeName.contains("C:\\Windows")) {
				return@forEach
			}

			check(WinAPIHandles.getWindowRect(processData, rectPlaceholder.address()) != 0.toByte()) {
				"Could not get rect"
			}

			val rect = RectReader.fromMemorySegment(rectPlaceholder)

			if (rect.area == 0u) {
				return@forEach
			}

			val clazzName = NativeUtils.getExpanding { size, handle ->
				if (WinAPIHandles.getClassNameA(processData, handle.address(), size.toInt()) as Int >= size) {
					null
				}
				else {
					CLinker.toJavaString(handle)
				}
			}

			val title = NativeUtils.getExpanding { size, handle ->
				if (WinAPIHandles.getWindowTextA(processData, handle.address(), size.toInt()) as Int >= size) {
					null
				}
				else {
					CLinker.toJavaString(handle)
				}
			}

			denseProcesses.add(Process(processData, processHandle, exeName, clazzName, title, pid, rect))

		}

		confinedStatic.close()
		return denseProcesses
	}

	@JvmStatic
	fun forEachWindow(address1: MemoryAddress, address2: MemoryAddress): Int {

		if (isMainWindow(address1)) {
			@Suppress("UNCHECKED_CAST")
			(registry[address2.toRawLongValue()] as MutableList<MemoryAddress>).add(address1)
		}

		return 1
	}

	fun getEnumerator(): MemoryAddress {

		if (!isCoInitialized) {

			val result = WinAPIHandles.coInitializeEx(MemoryAddress.NULL, 0) as Int

			check(result >= 0) {
				"CoInitializeEx failed: $result ${WinAPIHandles.getLastError()}"
			}

			isCoInitialized = true
		}

		val enumerator = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())

		val result = WinAPIHandles.coCreateInstance(
			WinAPIValues.GUID.CLSID_MMDeviceEnumerator.address(),
			MemoryAddress.NULL,
			WinAPIValues.CLSCTX_ALL,
			WinAPIValues.GUID.IID_IMMDeviceEnumerator.address(),
			enumerator.address()
		) as Int

		check(result >= 0) {
			"CoCreateInstance failed: $result ${WinAPIHandles.getLastError()}"
		}

		return MemoryAccess.getAddress(enumerator)
	}

	fun getDefaultAudioDevice(): MemoryAddress {

		val enumerator = getEnumerator()
		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = WinAPIHandles.getAudioDeviceEndpoint(enumerator, tmp.address()) as Int

		check(result >= 0) {
			"GetAudioDeviceEndpoint failed: $result ${WinAPIHandles.getLastError()}"
		}

		return MemoryAccess.getAddress(tmp).apply { tmp.scope().close() }
	}

	fun activateAudioClient(device: MemoryAddress): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = WinAPIHandles.deviceActivate(device, tmp.address()) as Int

		check(result >= 0) {
			"DeviceActivate failed: $result ${WinAPIHandles.getLastError()}"
		}

		return MemoryAccess.getAddress(tmp).apply { tmp.scope().close() }
	}

	fun getMixFormat(client: MemoryAddress, REFTIMES_PER_SEC: Int = 10_000_000): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = WinAPIHandles.getMixFormat(client, tmp.address(), REFTIMES_PER_SEC) as Int

		check(result >= 0) {
			"GetMixFormat failed: $result ${WinAPIHandles.getLastError()}"
		}

		return MemoryAccess.getAddress(tmp).apply {
			tmp.scope().close()
		}
	}

	fun getBufferSize(client: MemoryAddress): UInt {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = WinAPIHandles.getBufferSize(client, tmp.address()) as Int

		check(result >= 0) {
			"GetBufferSize failed: $result ${WinAPIHandles.getLastError()}"
		}

		return MemoryAccess.getInt(tmp).apply { tmp.scope().close() }.toUInt()
	}

	fun getService(client: MemoryAddress): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = WinAPIHandles.getService(client, tmp.address()) as Int

		check(result >= 0) {
			"DeviceGetService failed: $result ${WinAPIHandles.getLastError()}"
		}

		return MemoryAccess.getAddress(tmp).apply {
			tmp.scope().close()
		}
	}

	fun getActualDuration(format: MemoryAddress, bufferSize: UInt, REFTIMES_PER_SEC: Int = 1_000_000): Double {
		return WinAPIHandles.getService(format, bufferSize.toInt(), REFTIMES_PER_SEC) as Double
	}

	fun start(client: MemoryAddress) {

		val result = WinAPIHandles.clientStart(client) as Int

		check(result >= 0) {
			"Start failed: $result ${WinAPIHandles.getLastError()}"
		}
	}

	fun stop(client: MemoryAddress) {

		val result = WinAPIHandles.clientStop(client) as Int

		check(result >= 0) {
			"Stop failed: $result ${WinAPIHandles.getLastError()}"
		}
	}

	fun getNextPacketSize(service: MemoryAddress): UInt {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = WinAPIHandles.getNextPacketSize(service, tmp.address()) as Int

		check(result >= 0) {
			"GetNextPacketSize failed: $result ${WinAPIHandles.getLastError()}"
		}

		return MemoryAccess.getInt(tmp).apply { tmp.scope().close() }.toUInt()
	}

	fun getPacket(service: MemoryAddress): NativeBuffer {

		val pData = MemorySegment.allocateNative(CLinker.C_CHAR.byteSize(), ResourceScope.newConfinedScope())
		val numFramesAvailable = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), pData.scope())
		val flags = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), pData.scope())
		val result = WinAPIHandles.getBuffer(service, pData.address(), numFramesAvailable.address(), flags.address()) as Int

		check(result >= 0) {
			"GetPacket failed: $result ${WinAPIHandles.getLastError()}"
		}

		return NativeBuffer(
			MemoryAccess.getByte(pData),
			MemoryAccess.getInt(flags),
			MemoryAccess.getInt(numFramesAvailable).toUInt()
		).apply {
			pData.scope().close()
		}
	}

	fun deletePacket(service: MemoryAddress, buffer: NativeBuffer) {

		val result = WinAPIHandles.releaseBuffer(service, buffer.numFramesAvailable) as Int

		check(result >= 0) {
			"DeletePacket failed: $result ${WinAPIHandles.getLastError()}"
		}
	}


	private fun register(value: Any): Long {

		var id = Random.nextLong()

		while (id in registry) {
			id = Random.nextLong()
		}

		registry[id] = value

		return id
	}

	private inline fun tempRegister(value: Any, block: (id: Long) -> Unit) {
		val id = register(value)
		block(id)
		registry.remove(id)
	}

}