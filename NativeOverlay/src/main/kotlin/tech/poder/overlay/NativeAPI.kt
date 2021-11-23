package tech.poder.overlay

import jdk.incubator.foreign.*
import tech.poder.overlay.utils.NativeUtils
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.concurrent.read
import kotlin.random.Random
import kotlin.system.exitProcess

object NativeAPI {

	private val registry = ConcurrentSkipListMap<Long, Any>()


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

	//endregion

	init {
		// TODO: See if you can load libnew.dll without a Path
		NativeUtils.loadLibraries("Comctl32", "Ole32", "user32", "kernel32", "psapi", "libnew.dll")
		//NativeUtils.loadLibrary(Path("libnew.dll"))
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
						defWindowProcW.invoke(hwnd, uMsg, wParam, lParam) as MemoryAddress
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

	var firstDraw = false


	private fun repaint(hwnd: MemoryAddress) {
		lastWindow.startPaint()
		if (images > 0) {
			firstDraw = true
			val dc = NativeRegistry[Callback.getDC].invoke(hwnd) as MemoryAddress
			Callback.lock.read {
				repeat(images) {
					val result = NativeRegistry[Callback.imageListDraw].invoke(
						currentImageList, it, dc, 0, 0, 0
					) as Int
					check(result != 0) {
						"ImageList_Draw failed: ${NativeRegistry[Callback.getLastError].invoke()}"
					}
				}
			}
			NativeRegistry[Callback.releaseDC].invoke(hwnd, dc)
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
				NativeRegistry[WindowManager.defWindowProcW].invoke(hwnd, uMsg, wParam, lParam) as MemoryAddress
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

			val intHolderSeg = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), confinedStatic)
			val pidNoReason = getWindowThreadProcessId(processData, intHolderSeg.address()) as Int

			check(pidNoReason != 0) {
				"Could not get pid"
			}

			val pid = MemoryAccess.getInt(intHolderSeg)
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

			if (rect.area != 0u) {
				val clazzName = NativeUtils.getExpanding { size, handle ->

					val used = getClassNameA(processData, handle.address(), size.toInt()) as Int

					if (used >= size) {
						null
					}
					else {
						CLinker.toJavaString(handle)
					}
				}
				val title = NativeUtils.getExpanding { size, handle ->

					val used = getWindowTextA(processData, handle.address(), size.toInt()) as Int

					if (used >= size) {
						null
					}
					else {
						CLinker.toJavaString(handle)
					}
				}

				denseProcesses.add(Process(processData, processHandle, exeName, clazzName, title, pid, rect))
			}
			else {
				return@forEach
			}
		}
		confinedStatic.close()

		return denseProcesses
	}

	@JvmStatic
	fun forEachWindow(address1: MemoryAddress, address2: MemoryAddress): Int {

		if (Callback.isMainWindow(address1)) {
			@Suppress("UNCHECKED_CAST")
			(registry[address2.toRawLongValue()] as MutableList<MemoryAddress>).add(address1)
		}

		return 1
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

	private inline fun tempRegister(id: Long, value: Any, block: () -> Unit) {
		registry[id] = value
		block()
		registry.remove(id)
	}


}