package tech.poder.overlay

import jdk.incubator.foreign.*
import tech.poder.overlay.utils.NativeUtils
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.random.Random
import kotlin.system.exitProcess

object NativeAPI {

	private val registry = ConcurrentSkipListMap<Long, Any>()

	private var isCoInitialized = false

	internal var isFirstDraw = false

	internal val repaintLock = ReentrantReadWriteLock()


	//region MethodHandle

	val getLastError = NativeUtils.lookupMethodHandle("GetLastError", Int::class.java)

	private val enumWindows = NativeUtils.lookupMethodHandle(
		"EnumWindows", Byte::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
	)

	private val getWindowThreadProcessId = NativeUtils.lookupMethodHandle(
		"GetWindowThreadProcessId", Int::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
	)

	private val isWindowVisible = NativeUtils.lookupMethodHandle(
		"IsWindowVisible", Boolean::class.java, listOf(MemoryAddress::class.java)
	)

	private val getWindow = NativeUtils.lookupMethodHandle(
		"GetWindow", MemoryAddress::class.java, listOf(MemoryAddress::class.java, Int::class.java)
	)

	private val getDC = NativeUtils.lookupMethodHandle(
		"GetDC", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
	)

	private val releaseDC = NativeUtils.lookupMethodHandle(
		"ReleaseDC", MemoryAddress::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
	)

	private val getClassNameA = NativeUtils.lookupMethodHandle(
		"GetClassNameA",
		Int::class.java,
		listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
	)

	private val getWindowTextA = NativeUtils.lookupMethodHandle(
		"GetWindowTextA",
		Int::class.java,
		listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
	)

	val getWindowRect = NativeUtils.lookupMethodHandle(
		"GetWindowRect", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
	)

	private val getWindowModuleFileNameA = NativeUtils.lookupMethodHandle(
		"GetWindowModuleFileNameA",
		Int::class.java,
		listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
	)

	private val openProcess = NativeUtils.lookupMethodHandle(
		"OpenProcess", MemoryAddress::class.java, listOf(Int::class.java, Boolean::class.java, Int::class.java)
	)

	private val getModuleFileNameExA = NativeUtils.lookupMethodHandle(
		"GetModuleFileNameExA",
		Int::class.java,
		listOf(MemoryAddress::class.java, MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
	)

	val closeHandle = NativeUtils.lookupMethodHandle(
		"CloseHandle", Boolean::class.java, listOf(MemoryAddress::class.java)
	)

	val enumProcessModules = NativeUtils.lookupMethodHandle(
		"EnumProcessModules",
		Boolean::class.java,
		listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java, MemoryAddress::class.java)
	)

	val imageListCreate = NativeUtils.lookupMethodHandle(
		"ImageList_Create",
		MemoryAddress::class.java,
		listOf(Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java)
	)

	val imageListDestroy = NativeUtils.lookupMethodHandle(
		"ImageList_Destroy", Boolean::class.java, listOf(MemoryAddress::class.java)
	)

	val imageListAdd = NativeUtils.lookupMethodHandle(
		"ImageList_AddMasked",
		Int::class.java,
		listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
	)

	val loadImage = NativeUtils.lookupMethodHandle(
		"LoadImageW",
		MemoryAddress::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java,
			Int::class.java,
			Int::class.java,
			Int::class.java,
			Int::class.java
		)
	)

	val imageListDraw = NativeUtils.lookupMethodHandle(
		"ImageList_Draw",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			Int::class.java,
			MemoryAddress::class.java,
			Int::class.java,
			Int::class.java,
			Int::class.java
		)
	)

	val getModuleHandle = NativeUtils.lookupMethodHandle(
		"GetModuleHandleA", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
	)

	val forEachWindowUpcall = NativeUtils.lookupStaticMethodHandle(
		this::class.java,
		"forEachWindow",
		Boolean::class.java,
		listOf(MemoryAddress::class.java, MemoryAddress::class.java)
	)

	val coCreateInstance = NativeUtils.lookupMethodHandle(
		"CoCreateInstance",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java,
			Int::class.java,
			MemoryAddress::class.java,
			MemoryAddress::class.java
		)
	)

	val coInitializeEx = NativeUtils.lookupMethodHandle(
		"CoInitializeEx",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			Int::class.java,
		)
	)

	val getAudioDeviceEndpoint = NativeUtils.lookupMethodHandle(
		"GetAudioDeviceEndpoint",
		Int::class.java,
		listOf(MemoryAddress::class.java, MemoryAddress::class.java)
	)

	val deviceActivate = NativeUtils.lookupMethodHandle(
		"DeviceActivate",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java,
		)
	)

	val getMixFormat = NativeUtils.lookupMethodHandle(
		"GetMixFormat",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java,
			Int::class.java
		)
	)

	val getBufferSize = NativeUtils.lookupMethodHandle(
		"GetBufferSize",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java
		)
	)

	val getService = NativeUtils.lookupMethodHandle(
		"GetService",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java
		)
	)

	val getHNSActualDuration = NativeUtils.lookupMethodHandle(
		"GetHNSActualDuration",
		Double::class.java,
		listOf(
			MemoryAddress::class.java,
			Int::class.java,
			Int::class.java
		)
	)

	val clientStart = NativeUtils.lookupMethodHandle(
		"Start",
		Int::class.java,
		listOf(
			MemoryAddress::class.java
		)
	)

	val clientStop = NativeUtils.lookupMethodHandle(
		"Stop",
		Int::class.java,
		listOf(
			MemoryAddress::class.java
		)
	)

	val getNextPacketSize = NativeUtils.lookupMethodHandle(
		"GetNextPacketSize",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java
		)
	)

	val getBuffer = NativeUtils.lookupMethodHandle(
		"GetBuffer",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java,
			MemoryAddress::class.java,
			MemoryAddress::class.java
		)
	)

	val releaseBuffer = NativeUtils.lookupMethodHandle(
		"ReleaseBuffer",
		Int::class.java,
		listOf(
			MemoryAddress::class.java,
			Int::class.java
		)
	)

	val failed = NativeUtils.lookupMethodHandle(
		"Failed",
		Int::class.java,
		listOf(
			Int::class.java
		)
	)

	//endregion


	val GUID = StructDefinition.generate(
		listOf(
			Long::class.java,
			Short::class.java,
			Short::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java
		)
	)

	val propertyKey = StructDefinition.generate(
		listOf(
			Long::class.java,
			Short::class.java,
			Short::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Byte::class.java,
			Int::class.java
		)
	)

	const val CLSCTX_ALL = 23

	val CLSID_MMDeviceEnumerator = defineGUID(
		0xBCDE0395L,
		0xE52F.toShort(),
		0x467C,
		0x8E.toByte(),
		0x3D,
		0xC4.toByte(),
		0x57,
		0x92.toByte(),
		0x91.toByte(),
		0x69,
		0x2E
	)


	val IID_IMMDeviceEnumerator = defineGUID(
		0xa95664d2,
		0x9614.toShort(),
		0x4f35,
		0xa7.toByte(),
		0x46,
		0xde.toByte(),
		0x8d.toByte(),
		0xb6.toByte(),
		0x36,
		0x17,
		0xe6.toByte()
	)

	val IID_IAudioClient = defineGUID(
		0x1cb9ad4c,
		0xdbfa.toShort(),
		0x4c32,
		0xb1.toByte(),
		0x78,
		0xc2.toByte(),
		0xf5.toByte(),
		0x68,
		0xa7.toByte(),
		0x03,
		0xb2.toByte()
	)

	val IID_IAudioCaptureClient = defineGUID(
		0xc8adbd64, 0xe71e.toShort(), 0x48a0, 0xa4.toByte(), 0xde.toByte(), 0x18, 0x5c, 0x39, 0x5c, 0xd3.toByte(), 0x17
	)

	val PKEY_Device_FriendlyName = definePropertyId(
		0xa45c254e,
		0xdf1c.toShort(),
		0x4efd,
		0x80.toByte(),
		0x20,
		0x67,
		0xd1.toByte(),
		0x46,
		0xa8.toByte(),
		0x50,
		0xe0.toByte(),
		14
	)


	init {
		// TODO: See if you can load libnew.dll without a Path
		NativeUtils.loadLibraries("Comctl32", "Ole32", "user32", "kernel32", "psapi", "libnew.dll")
		//NativeUtils.loadLibrary(Path("libnew.dll"))
	}



	fun defineGUID(
		a: Long, b: Short, c: Short, d: Byte, e: Byte, f: Byte, g: Byte, h: Byte, i: Byte, j: Byte, k: Byte
	): MemorySegment {
		val confinedStatic = ResourceScope.newSharedScope()
		val data = MemorySegment.allocateNative(GUID.size, confinedStatic)
		MemoryAccess.setLongAtOffset(data, GUID[0], a)
		MemoryAccess.setShortAtOffset(data, GUID[1], b)
		MemoryAccess.setShortAtOffset(data, GUID[2], c)
		MemoryAccess.setByteAtOffset(data, GUID[3], d)
		MemoryAccess.setByteAtOffset(data, GUID[4], e)
		MemoryAccess.setByteAtOffset(data, GUID[5], f)
		MemoryAccess.setByteAtOffset(data, GUID[6], g)
		MemoryAccess.setByteAtOffset(data, GUID[7], h)
		MemoryAccess.setByteAtOffset(data, GUID[8], i)
		MemoryAccess.setByteAtOffset(data, GUID[9], j)
		MemoryAccess.setByteAtOffset(data, GUID[10], k)
		return data
	}

	fun definePropertyId(
		a: Long, b: Short, c: Short, d: Byte, e: Byte, f: Byte, g: Byte, h: Byte, i: Byte, j: Byte, k: Byte, l: Int
	): MemorySegment {
		val confinedStatic = ResourceScope.newSharedScope()
		val data = MemorySegment.allocateNative(propertyKey.size, confinedStatic)
		MemoryAccess.setLongAtOffset(data, propertyKey[0], a)
		MemoryAccess.setShortAtOffset(data, propertyKey[1], b)
		MemoryAccess.setShortAtOffset(data, propertyKey[2], c)
		MemoryAccess.setByteAtOffset(data, propertyKey[3], d)
		MemoryAccess.setByteAtOffset(data, propertyKey[4], e)
		MemoryAccess.setByteAtOffset(data, propertyKey[5], f)
		MemoryAccess.setByteAtOffset(data, propertyKey[6], g)
		MemoryAccess.setByteAtOffset(data, propertyKey[7], h)
		MemoryAccess.setByteAtOffset(data, propertyKey[8], i)
		MemoryAccess.setByteAtOffset(data, propertyKey[9], j)
		MemoryAccess.setByteAtOffset(data, propertyKey[10], k)
		MemoryAccess.setIntAtOffset(data, propertyKey[11], l)
		return data
	}


	class Window(hwnd: MemoryAddress, uMsg: Int, wParam: MemoryAddress, lParam: MemoryAddress) {

		companion object {

			@JvmStatic
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
						WindowManager.defWindowProcW(hwnd, uMsg, wParam, lParam) as MemoryAddress
					}
				}
			}

		}

	}

	var currentImageList = MemoryAddress.NULL
	var images = 0

	var lastWindow = WindowManager(MemoryAddress.NULL)

	fun loadImage(pathString: ExternalStorage): MemoryAddress {

		val res = loadImage(MemoryAddress.NULL, pathString.segment.address(), 0, 0, 0, 0x00000010) as MemoryAddress

		check(res != MemoryAddress.NULL) {
			"LoadImage failed: ${getLastError()}"
		}

		return res
	}

	private fun repaint(hwnd: MemoryAddress) {

		lastWindow.startPaint()

		if (images > 0) {

			isFirstDraw = true

			val dc = getDC(hwnd) as MemoryAddress

			repaintLock.read {
				repeat(images) {
					check(imageListDraw(currentImageList, it, dc, 0, 0, 0) != 0) {
						"ImageList_Draw failed: ${getLastError()}"
					}
				}
			}

			releaseDC(hwnd, dc)
		}

		lastWindow.endPaint()
	}

	@JvmStatic
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
				WindowManager.defWindowProcW(hwnd, uMsg, wParam, lParam) as MemoryAddress
			}
		}
	}


	fun isMainWindow(hwnd: MemoryAddress): Boolean {
		return (
			isWindowVisible.invoke(hwnd) as Int != 0 &&
			getWindow.invoke(hwnd, 4) == MemoryAddress.NULL
		)
	}


	fun getProcesses(): List<Process> {

		val confinedStatic = ResourceScope.newConfinedScope()
		val processes = mutableListOf<MemoryAddress>()

		tempRegister(processes) { id ->

			val idLocation = MemoryAddress.ofLong(id)
			val result = enumWindows(forEachWindowUpcall, idLocation.address()) as Byte

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

			check(getWindowThreadProcessId(processData, pidSegment.address()) as Int != 0) {
				"Could not get pid"
			}

			val pid = MemoryAccess.getInt(pidSegment)
			val processHandle = openProcess(PROCESS_QUERY_INFORMATION, 0, pid) as MemoryAddress

			if (processHandle == MemoryAddress.NULL) {

				val code = getLastError() as Int

				if (code != 5) {
					println("Error Code: ${code}:")
				}
			}

			val exeName = NativeUtils.getExpanding { size, segment ->

				MemoryAccess.setByte(segment, 0)

				val used =
					if (processHandle == MemoryAddress.NULL) {
						getWindowModuleFileNameA(processData, segment.address(), size.toInt()) as Int
					}
					else {
						getModuleFileNameExA(processHandle, MemoryAddress.NULL, segment.address(), size.toInt()) as Int
					}

				if (used == 0) {
					println(getLastError())
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

			check(getWindowRect(processData, rectPlaceholder.address()) != 0.toByte()) {
				"Could not get rect"
			}

			val rect = RectReader.fromMemorySegment(rectPlaceholder)

			if (rect.area == 0u) {
				return@forEach
			}

			val clazzName = NativeUtils.getExpanding { size, handle ->
				if (getClassNameA(processData, handle.address(), size.toInt()) as Int >= size) {
					null
				}
				else {
					CLinker.toJavaString(handle)
				}
			}

			val title = NativeUtils.getExpanding { size, handle ->
				if (getWindowTextA(processData, handle.address(), size.toInt()) as Int >= size) {
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

			val result = coInitializeEx(MemoryAddress.NULL, 0) as Int

			check(result >= 0) {
				"CoInitializeEx failed: $result ${getLastError()}"
			}

			isCoInitialized = true
		}

		val enumerator = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())

		val result = coCreateInstance(
			CLSID_MMDeviceEnumerator.address(),
			MemoryAddress.NULL,
			CLSCTX_ALL,
			IID_IMMDeviceEnumerator.address(),
			enumerator.address()
		) as Int

		check(result >= 0) {
			"CoCreateInstance failed: $result ${getLastError()}"
		}

		return MemoryAccess.getAddress(enumerator)
	}

	fun getDefaultAudioDevice(): MemoryAddress {

		val enumerator = getEnumerator()
		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = getAudioDeviceEndpoint(enumerator, tmp.address()) as Int

		check(result >= 0) {
			"GetAudioDeviceEndpoint failed: $result ${getLastError()}"
		}

		return MemoryAccess.getAddress(tmp).apply { tmp.scope().close() }
	}

	fun activateAudioClient(device: MemoryAddress): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = deviceActivate(device, tmp.address()) as Int

		check(result >= 0) {
			"DeviceActivate failed: $result ${getLastError()}"
		}

		return MemoryAccess.getAddress(tmp).apply { tmp.scope().close() }
	}

	fun getMixFormat(client: MemoryAddress, REFTIMES_PER_SEC: Int = 10_000_000): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = getMixFormat(client, tmp.address(), REFTIMES_PER_SEC) as Int

		check(result >= 0) {
			"GetMixFormat failed: $result ${getLastError()}"
		}

		return MemoryAccess.getAddress(tmp).apply {
			tmp.scope().close()
		}
	}

	fun getBufferSize(client: MemoryAddress): UInt {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = getBufferSize(client, tmp.address()) as Int

		check(result >= 0) {
			"GetBufferSize failed: $result ${getLastError()}"
		}

		return MemoryAccess.getInt(tmp).apply { tmp.scope().close() }.toUInt()
	}

	fun getService(client: MemoryAddress): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = getService(client, tmp.address()) as Int

		check(result >= 0) {
			"DeviceGetService failed: $result ${getLastError()}"
		}

		return MemoryAccess.getAddress(tmp).apply {
			tmp.scope().close()
		}
	}

	fun getActualDuration(format: MemoryAddress, bufferSize: UInt, REFTIMES_PER_SEC: Int = 1_000_000): Double {
		return getService(format, bufferSize.toInt(), REFTIMES_PER_SEC) as Double
	}

	fun start(client: MemoryAddress) {

		val result = clientStart(client) as Int

		check(result >= 0) {
			"Start failed: $result ${getLastError()}"
		}
	}

	fun stop(client: MemoryAddress) {

		val result = clientStop(client) as Int

		check(result >= 0) {
			"Stop failed: $result ${getLastError()}"
		}
	}

	fun getNextPacketSize(service: MemoryAddress): UInt {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
		val result = getNextPacketSize(service, tmp.address()) as Int

		check(result >= 0) {
			"GetNextPacketSize failed: $result ${getLastError()}"
		}

		return MemoryAccess.getInt(tmp).apply { tmp.scope().close() }.toUInt()
	}

	fun getPacket(service: MemoryAddress): NativeBuffer {

		val pData = MemorySegment.allocateNative(CLinker.C_CHAR.byteSize(), ResourceScope.newConfinedScope())
		val numFramesAvailable = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), pData.scope())
		val flags = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), pData.scope())
		val result = getBuffer(service, pData.address(), numFramesAvailable.address(), flags.address()) as Int

		check(result >= 0) {
			"GetPacket failed: $result ${getLastError()}"
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

		val result = releaseBuffer(service, buffer.numFramesAvailable) as Int

		check(result >= 0) {
			"DeletePacket failed: $result ${getLastError()}"
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