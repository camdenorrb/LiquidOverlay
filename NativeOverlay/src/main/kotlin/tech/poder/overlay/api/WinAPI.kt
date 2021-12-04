package tech.poder.overlay.api

import jdk.incubator.foreign.*
import tech.poder.overlay.audio.base.AudioFormat
import tech.poder.overlay.data.ExternalStorage
import tech.poder.overlay.data.NativeBuffer
import tech.poder.overlay.data.Process
import tech.poder.overlay.data.RectReader
import tech.poder.overlay.handles.KatDLLHandles
import tech.poder.overlay.handles.WinAPIHandles
import tech.poder.overlay.instance.BasicInstance
import tech.poder.overlay.instance.katdll.StateInstance
import tech.poder.overlay.instance.winapi.FormatListInstance
import tech.poder.overlay.structs.KatDLLStructs
import tech.poder.overlay.structs.WinAPIStructs
import tech.poder.overlay.utils.NativeUtils
import tech.poder.overlay.utils.NumberUtils
import tech.poder.overlay.values.WinAPIValues
import tech.poder.overlay.window.WindowManager
import java.util.*
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


	var currentImageList = MemoryAddress.NULL
	var images = 0

	var lastWindow = WindowManager(MemoryAddress.NULL)

	val forEachWindowUpcall = NativeUtils.lookupStaticMethodUpcall(
		this::class.java,
		"forEachWindow",
		Boolean::class.java,
		listOf(
			MemoryAddress::class.java,
			MemoryAddress::class.java
		)
	)


	fun loadImage(pathString: ExternalStorage): MemoryAddress {

		val handle = WinAPIHandles.loadImageW(MemoryAddress.NULL, pathString.segment.address(), 0, 0, 0, 0x00000010) as MemoryAddress

		check(handle != MemoryAddress.NULL) {
			"LoadImage failed: ${WinAPIHandles.getLastError()}"
		}

		return handle
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

	fun getProcesses(scope: ResourceScope): List<Process> {

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
		val rectPlaceholder = MemorySegment.allocateNative(CLinker.C_LONG.byteSize() * 4, scope)
		val denseProcesses = mutableListOf<Process>()

		processes.forEach { processData ->

			val pidSegment = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), scope)

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

	fun getEnumerator(scope: ResourceScope): MemoryAddress {

		if (!isCoInitialized) {

			checkSuccess(
				"CoInitializeEx",
				WinAPIHandles.coInitializeEx(MemoryAddress.NULL, 0) as Int
			)

			isCoInitialized = true
		}

		val enumerator = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), scope)

		val result = WinAPIHandles.coCreateInstance(
			WinAPIValues.GUID.CLSID_MMDeviceEnumerator.address(),
			MemoryAddress.NULL,
			WinAPIValues.CLSCTX_ALL,
			WinAPIValues.GUID.IID_IMMDeviceEnumerator.address(),
			enumerator.address()
		) as Int

		checkSuccess("CoCreateInstance", result)

		return MemoryAccess.getAddress(enumerator)
	}

	fun getDefaultAudioDevice(scope: ResourceScope): MemoryAddress {

		val enumerator = getEnumerator(scope)
		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), scope)

		checkSuccess(
			"GetAudioDeviceEndpoint",
			WinAPIHandles.getAudioDeviceEndpoint(enumerator, tmp.address()) as Int
		)

		return MemoryAccess.getAddress(tmp).apply { tmp.unload() }
	}

	fun activateAudioClient(device: MemoryAddress, scope: ResourceScope): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), scope)

		checkSuccess(
			"DeviceActivate",
			WinAPIHandles.deviceActivate(device, tmp.address()) as Int
		)

		return MemoryAccess.getAddress(tmp).apply { tmp.unload() }
	}

	fun getMixFormat(client: MemoryAddress, scope: ResourceScope, REFTIMES_PER_SEC: Int = 10_000_000): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), scope)

		checkSuccess(
			"GetMixFormat",
			WinAPIHandles.getMixFormat(client, tmp.address(), REFTIMES_PER_SEC) as Int
		)

		return MemoryAccess.getAddress(tmp).apply {
			tmp.unload()
		}
	}

	fun getBufferSize(client: MemoryAddress, scope: ResourceScope): UInt {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), scope)

		checkSuccess(
			"GetBufferSize",
			WinAPIHandles.getBufferSize(client, tmp.address()) as Int
		)

		return MemoryAccess.getInt(tmp).apply { tmp.unload() }.toUInt()
	}

	fun getService(client: MemoryAddress, scope: ResourceScope): MemoryAddress {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), scope)

		checkSuccess(
			"DeviceGetService",
			WinAPIHandles.getService(client, tmp.address()) as Int
		)

		return MemoryAccess.getAddress(tmp).apply {
			tmp.unload()
		}
	}

	fun getActualDuration(format: MemoryAddress, bufferSize: UInt, REFTIMES_PER_SEC: Int = 1_000_000): Double {
		return WinAPIHandles.getService(format, bufferSize.toInt(), REFTIMES_PER_SEC) as Double
	}

	fun start(client: MemoryAddress) {
		checkSuccess(
			"Start",
			WinAPIHandles.clientStart(client) as Int
		)
	}

	fun stop(client: MemoryAddress) {
		checkSuccess(
			"Stop",
			WinAPIHandles.clientStop(client) as Int
		)
	}

	fun getNextPacketSize(service: MemoryAddress, scope: ResourceScope): UInt {

		val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), scope)

		checkSuccess(
			"GetNextPacketSize",
			WinAPIHandles.getNextPacketSize(service, tmp.address()) as Int
		)

		return MemoryAccess.getInt(tmp).apply { tmp.unload() }.toUInt()
	}

	fun getPacket(service: MemoryAddress, scope: ResourceScope): NativeBuffer {

		val pData = MemorySegment.allocateNative(CLinker.C_CHAR.byteSize(), scope)
		val numFramesAvailable = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), pData.scope())
		val flags = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), pData.scope())

		checkSuccess(
			"GetNextPacket",
			WinAPIHandles.getBuffer(service, pData.address(), numFramesAvailable.address(), flags.address()) as Int
		)

		return NativeBuffer(
			MemoryAccess.getByte(pData),
			MemoryAccess.getInt(flags),
			MemoryAccess.getInt(numFramesAvailable).toUInt()
		).apply {
			pData.unload()
		}
	}

	fun deletePacket(service: MemoryAddress, buffer: NativeBuffer) {
		checkSuccess(
			"DeletePacket",
			WinAPIHandles.releaseBuffer(service, buffer.numFramesAvailable) as Int
		)
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
		register(value).let { id ->
			block(id)
			registry.remove(id)
		}
	}



	// TODO: New things to cleanup

	fun upgradeFormat(scope: ResourceScope, format: BasicInstance): BasicInstance {
		return BasicInstance(
			format.segment.address().asSegment(WinAPIStructs.waveFormatEx2.size, scope),
			WinAPIStructs.waveFormatEx2
		)
	}

	fun newFormat(scope: ResourceScope): BasicInstance {
		return WinAPIStructs.waveFormatEx.new(scope)
	}

	fun newFormatList(scope: ResourceScope): BasicInstance {
		return KatD.formatList.new(scope)
	}

	fun getPCMgUID(scope: ResourceScope): BasicInstance {
		return BasicInstance(
			(KatDLLHandles.getPCMID() as MemoryAddress).asSegment(WinAPIStructs.guid.size, scope),
			WinAPIStructs.guid
		)
	}

	fun toJavaUUID(guid: BasicInstance): UUID {

		val data = ByteArray(16)
		var offset = 0

		NumberUtils.bytesFromInt(MemoryAccess.getIntAtOffset(guid.segment, guid.struct[0]), data, offset)
		offset += Int.SIZE_BYTES
		NumberUtils.bytesFromShort(MemoryAccess.getShortAtOffset(guid.segment, guid.struct[1]), data, offset)
		offset += Short.SIZE_BYTES
		NumberUtils.bytesFromShort(MemoryAccess.getShortAtOffset(guid.segment, guid.struct[2]), data, offset)
		offset += Short.SIZE_BYTES
		data[offset] = (MemoryAccess.getByteAtOffset(guid.segment, guid.struct[3]))
		offset++
		data[offset] = (MemoryAccess.getByteAtOffset(guid.segment, guid.struct[4]))
		offset++
		data[offset] = (MemoryAccess.getByteAtOffset(guid.segment, guid.struct[5]))
		offset++
		data[offset] = (MemoryAccess.getByteAtOffset(guid.segment, guid.struct[6]))
		offset++
		data[offset] = (MemoryAccess.getByteAtOffset(guid.segment, guid.struct[7]))
		offset++
		data[offset] = (MemoryAccess.getByteAtOffset(guid.segment, guid.struct[8]))
		offset++
		data[offset] = (MemoryAccess.getByteAtOffset(guid.segment, guid.struct[9]))
		offset++
		data[offset] = (MemoryAccess.getByteAtOffset(guid.segment, guid.struct[10]))

		return UUID(NumberUtils.longFromBytes(data), NumberUtils.longFromBytes(data, 8))
	}

	fun toGUID(uuid: UUID, guid: BasicInstance) {

		val byteArray = ByteArray(16)

		NumberUtils.bytesFromLong(uuid.mostSignificantBits, byteArray)
		NumberUtils.bytesFromLong(uuid.leastSignificantBits, byteArray, 8)

		var offset = 0
		MemoryAccess.setIntAtOffset(guid.segment, guid.struct[0], NumberUtils.intFromBytes(byteArray, offset))
		offset += Int.SIZE_BYTES
		MemoryAccess.setShortAtOffset(guid.segment, guid.struct[1], NumberUtils.shortFromBytes(byteArray, offset))
		offset += Short.SIZE_BYTES
		MemoryAccess.setShortAtOffset(guid.segment, guid.struct[2], NumberUtils.shortFromBytes(byteArray, offset))
		offset += Short.SIZE_BYTES
		MemoryAccess.setByteAtOffset(guid.segment, guid.struct[3], byteArray[offset])
		offset++
		MemoryAccess.setByteAtOffset(guid.segment, guid.struct[4], byteArray[offset])
		offset++
		MemoryAccess.setByteAtOffset(guid.segment, guid.struct[5], byteArray[offset])
		offset++
		MemoryAccess.setByteAtOffset(guid.segment, guid.struct[6], byteArray[offset])
		offset++
		MemoryAccess.setByteAtOffset(guid.segment, guid.struct[7], byteArray[offset])
		offset++
		MemoryAccess.setByteAtOffset(guid.segment, guid.struct[8], byteArray[offset])
		offset++
		MemoryAccess.setByteAtOffset(guid.segment, guid.struct[9], byteArray[offset])
		offset++
		MemoryAccess.setByteAtOffset(guid.segment, guid.struct[10], byteArray[offset])
	}

	fun toGUID(scope: ResourceScope, uuid: UUID): BasicInstance {
		return WinAPIStructs.guid.new(scope).also {
			toGUID(uuid, it)
		}
	}

	fun toFormat(address: MemoryAddress, scope: ResourceScope): BasicInstance {
		return BasicInstance(
			address.asSegment(WinAPIStructs.waveFormatEx2.size, scope),
			WinAPIStructs.waveFormatEx
		)
	}

	fun guidFromUpgradedFormat(format: BasicInstance): BasicInstance {
		return BasicInstance(format.segment.asSlice(format.struct[9]), WinAPIStructs.guid)
	}

	fun newState(scope: ResourceScope): StateInstance {
		return StateInstance(
			MemorySegment.allocateNative(KatDLLStructs.state.size, scope)
		)
	}

	val AUDCLNT_BUFFERFLAGS_SILENT: Byte = 2


	fun startRecording(scope: ResourceScope, formats: FormatListInstance? = null): StateInstance {

		val state = newState(scope)

		KatDLLHandles.startRecording(
			state.segment.address(),
			formats?.segment?.address() ?: MemoryAddress.NULL
		)

		checkSuccess("startRecording", state) {

			if (formats == null) {
				return@checkSuccess emptyMap()
			}

			val start = formats.start

			mapOf(
				// TODO: Test this, please for the love of cats
				"Alts: " to List(formats.amount) { index ->
					AudioFormat.getFormat(scope, toFormat(start.addOffset(index * CLinker.C_POINTER.byteSize()), scope))
				}.joinToString(", ")
			)
		}

		return state
	}

	// TODO: Scream at moocow for making everything store to State lmao
	fun getNextPacketSize(scope: ResourceScope) {
		val state = newState(scope)
		KatDLLHandles.getNextPacketSize(state.segment.address())
		checkSuccess("getNextPacketSize", state)
	}

	fun getBuffer(scope: ResourceScope) {
		val state = newState(scope)
		KatDLLHandles.getBuffer(state.segment.address())
		checkSuccess("getBuffer", state)
	}

	fun releaseBuffer(scope: ResourceScope) {
		val state = newState(scope)
		KatDLLHandles.releaseBuffer(state.segment.address())
		checkSuccess("releaseBuffer", state)
	}

	fun stopRecording(scope: ResourceScope) {
		val state = newState(scope)
		KatDLLHandles.stopRecording(state.segment.address())
		checkSuccess("stopRecording", state)
	}


	private fun checkSuccess(methodName: String, result: Int, lastError: Int = WinAPIHandles.getLastError() as Int) {
		check(result >= 0) {
			"""
				Failed to $methodName!
				Result Code: $result
				Last Error Code: $lastError
			""".trimIndent()
		}
	}

	private inline fun checkSuccess(
		methodName: String,
		state: StateInstance,
		additionalMessages: () -> Map<String, Any> = { emptyMap() }
	) {

		val hresult = state.hresult

		check(hresult >= 0) {
			"""
				Failed to $methodName! 
				Code: $hresult
				Message: ${state.message} 
				${additionalMessages().entries.joinToString("\n") { "${it.key}: ${it.value}" }}
			""".trimIndent()
		}
	}

}