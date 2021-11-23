package tech.poder.overlay

import jdk.incubator.foreign.*
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.system.exitProcess

object Callback {

    val init = NativeRegistry.loadLib("Comctl32", "Ole32", "user32", "kernel32", "psapi")
    val init2 = NativeRegistry.loadLib(Paths.get("KatLib.dll").toAbsolutePath())

    val getLastError = NativeRegistry.register(
        FunctionDescription(
            "GetLastError", Int::class.java
        )
    )

    private val enumWindows = NativeRegistry.register(
        FunctionDescription(
            "EnumWindows", Byte::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        )
    )

    private val getWindowThreadProcessId = NativeRegistry.register(
        FunctionDescription(
            "GetWindowThreadProcessId", Int::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        )
    )

    private val isWindowVisible = NativeRegistry.register(
        FunctionDescription(
            "IsWindowVisible", Boolean::class.java, listOf(MemoryAddress::class.java)
        )
    )

    private val getWindow = NativeRegistry.register(
        FunctionDescription(
            "GetWindow", MemoryAddress::class.java, listOf(MemoryAddress::class.java, Int::class.java)
        )
    )

    private val getDC = NativeRegistry.register(
        FunctionDescription(
            "GetDC", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
        )
    )

    private val releaseDC = NativeRegistry.register(
        FunctionDescription(
            "ReleaseDC", MemoryAddress::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        )
    )

    private val getClassNameA = NativeRegistry.register(
        FunctionDescription(
            "GetClassNameA",
            Int::class.java,
            listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
        )
    )

    private val getWindowTextA = NativeRegistry.register(
        FunctionDescription(
            "GetWindowTextA",
            Int::class.java,
            listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
        )
    )

    val getWindowRect = NativeRegistry.register(
        FunctionDescription(
            "GetWindowRect", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        )
    )

    private val getWindowModuleFileNameA = NativeRegistry.register(
        FunctionDescription(
            "GetWindowModuleFileNameA",
            Int::class.java,
            listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
        )
    )

    private val openProcess = NativeRegistry.register(
        FunctionDescription(
            "OpenProcess", MemoryAddress::class.java, listOf(Int::class.java, Boolean::class.java, Int::class.java)
        )
    )

    private val getModuleFileNameExA = NativeRegistry.register(
        FunctionDescription(
            "GetModuleFileNameExA",
            Int::class.java,
            listOf(MemoryAddress::class.java, MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
        )
    )

    val closeHandle = NativeRegistry.register(
        FunctionDescription(
            "CloseHandle", Boolean::class.java, listOf(MemoryAddress::class.java)
        )
    )

    val enumProcessModules = NativeRegistry.register(
        FunctionDescription(
            "EnumProcessModules",
            Boolean::class.java,
            listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java, MemoryAddress::class.java)
        )
    )

    private fun <T> getExpanding(invoke: (Long, MemorySegment) -> T?): T {
        var result: T? = null
        var size = 0L
        while (result == null) {
            if (size == 0L) {
                size = 256L
            }
            size *= 2L
            val scope = ResourceScope.newConfinedScope()
            val holder = MemorySegment.allocateNative(size, scope)
            result = invoke.invoke(size, holder)
            scope.close()
        }
        return result
    }

    fun isMainWindow(hwnd: MemoryAddress): Boolean {
        return NativeRegistry[isWindowVisible].invoke(hwnd) as Int != 0 && NativeRegistry[getWindow].invoke(
            hwnd, 4
        ) as MemoryAddress == MemoryAddress.NULL
    }

    @JvmStatic
    fun forEachWindow(addr1: MemoryAddress, addr2: MemoryAddress): Int {

        val id = addr2.toRawLongValue()

        if (isMainWindow(addr1)) {
            (NativeRegistry.getRegistry(id) as MutableList<MemoryAddress>).add(addr1)
        }

        return 1
    }

    val imageListCreate = NativeRegistry.register(
        FunctionDescription(
            "ImageList_Create",
            MemoryAddress::class.java,
            listOf(Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java)
        )
    )

    val imageListDestroy = NativeRegistry.register(
        FunctionDescription(
            "ImageList_Destroy", Boolean::class.java, listOf(MemoryAddress::class.java)
        )
    )

    val imageListAdd = NativeRegistry.register(
        FunctionDescription(
            "ImageList_AddMasked",
            Int::class.java,
            listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
        )
    )

    val loadImage = NativeRegistry.register(
        FunctionDescription(
            "LoadImageW", MemoryAddress::class.java, listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java
            )
        )
    )

    val imageListDraw = NativeRegistry.register(
        FunctionDescription(
            "ImageList_Draw", Int::class.java, listOf(
                MemoryAddress::class.java,
                Int::class.java,
                MemoryAddress::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java
            )
        )
    )
    val lock = ReentrantReadWriteLock()

    var currentImageList = MemoryAddress.NULL
    var images = 0

    var lastWindow = WindowManager(MemoryAddress.NULL)

    fun loadImage(pathString: ExternalStorage): MemoryAddress {
        val res = NativeRegistry[loadImage].invoke(
            MemoryAddress.NULL, pathString.segment.address(), 0, 0, 0, 0x00000010
        ) as MemoryAddress
        check(res != MemoryAddress.NULL) { "LoadImage failed: ${NativeRegistry[getLastError].invoke()}" }
        return res
    }

    var firstDraw = false


    private fun repaint(hwnd: MemoryAddress) {
        lastWindow.startPaint()
        if (images > 0) {
            firstDraw = true
            val dc = NativeRegistry[getDC].invoke(hwnd) as MemoryAddress
            lock.read {
                repeat(images) {
                    val result = NativeRegistry[imageListDraw].invoke(
                        currentImageList, it, dc, 0, 0, 0
                    ) as Int
                    check(result != 0) {
                        "ImageList_Draw failed: ${NativeRegistry[getLastError].invoke()}"
                    }
                }
            }
            NativeRegistry[releaseDC].invoke(hwnd, dc)
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

    val getModuleHandle = NativeRegistry.register(
        FunctionDescription(
            "GetModuleHandleA", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
        )
    )

    private val forEachWindowUpcall = NativeRegistry.registerUpcallStatic(
        FunctionDescription(
            "forEachWindow", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        ), this::class.java
    )


    fun getProcesses(): List<Process> {

        val confinedStatic = ResourceScope.newConfinedScope()
        val id = NativeRegistry.newRegistryId(mutableListOf<MemoryAddress>())

        val idLocation = MemoryAddress.ofLong(id)
        val result = NativeRegistry[enumWindows].invoke(forEachWindowUpcall, idLocation.address()) as Byte

        check(result != 0.toByte()) {
            "Callback failed"
        }

        val processes = NativeRegistry.dropRegistry(id) as MutableList<MemoryAddress>
        val PROCESS_QUERY_INFORMATION = 1024
        //val PROCESS_VM_READ = 16
        //val PROCESS_VM_WRITE = 32
        val rectPlaceholder = MemorySegment.allocateNative(CLinker.C_LONG.byteSize() * 4, confinedStatic)
        val denseProcesses = mutableListOf<Process>()
        processes.forEach { processData ->
            val intHolderSeg = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), confinedStatic)
            val pidNoReason =
                NativeRegistry[getWindowThreadProcessId].invoke(processData, intHolderSeg.address()) as Int
            check(pidNoReason != 0) {
                "Could not get pid"
            }
            val pid = MemoryAccess.getInt(intHolderSeg)
            val processHandle = NativeRegistry[openProcess].invoke(PROCESS_QUERY_INFORMATION, 0, pid) as MemoryAddress
            if (processHandle == MemoryAddress.NULL) {
                val code = NativeRegistry[getLastError].invoke() as Int
                if (code == 5) {
                    return@forEach
                }
                println("Error Code: ${code}:")
            }

            val exeName = getExpanding { size, segment ->

                MemoryAccess.setByte(segment, 0)

                if (processHandle == MemoryAddress.NULL) {

                    val used = NativeRegistry[getWindowModuleFileNameA].invoke(
                        processData, segment.address(), size.toInt()
                    ) as Int

                    if (used >= size) {
                        null
                    } else {
                        CLinker.toJavaString(segment)
                    }
                } else {

                    val used = NativeRegistry[getModuleFileNameExA].invoke(
                        processHandle, MemoryAddress.NULL, segment.address(), size.toInt()
                    ) as Int

                    if (used == 0) {
                        println(NativeRegistry[getLastError].invoke())
                    }

                    if (used >= size) {
                        null
                    } else {
                        CLinker.toJavaString(segment)
                    }
                }
            }

            if (exeName.contains("C:\\System32") || exeName.contains("C:\\Windows")) {
                return@forEach
            }
            check(NativeRegistry[getWindowRect].invoke(processData, rectPlaceholder.address()) != 0.toByte()) {
                "Could not get rect"
            }
            val rect = RectReader.fromMemorySegment(rectPlaceholder)
            if (rect.area != 0u) {
                val clazzName = getExpanding { size, handle ->
                    val used = NativeRegistry[getClassNameA].invoke(processData, handle.address(), size.toInt()) as Int
                    if (used >= size) {
                        null
                    } else {
                        CLinker.toJavaString(handle)
                    }
                }
                val title = getExpanding { size, handle ->

                    val used = NativeRegistry[getWindowTextA].invoke(processData, handle.address(), size.toInt()) as Int

                    if (used >= size) {
                        null
                    } else {
                        CLinker.toJavaString(handle)
                    }
                }

                denseProcesses.add(Process(processData, processHandle, exeName, clazzName, title, pid, rect))
            } else {
                return@forEach
            }
        }
        confinedStatic.close()

        return denseProcesses
    }

    private val state = StructDefinition.generate(
        listOf(
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Double::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
        )
    )

    /*val createState = NativeRegistry.register(FunctionDescription(
        "CreateState",
        MemoryAddress::class.java
    ))*/

    private val startRecording = NativeRegistry.register(
        FunctionDescription(
            "StartRecording", params = listOf(MemoryAddress::class.java)
        )
    )

    private val getNextPacketSize = NativeRegistry.register(
        FunctionDescription(
            "GetNextPacketSize", params = listOf(MemoryAddress::class.java)
        )
    )

    private val getBuffer = NativeRegistry.register(
        FunctionDescription(
            "GetBuffer", params = listOf(MemoryAddress::class.java)
        )
    )

    private val releaseBuffer = NativeRegistry.register(
        FunctionDescription(
            "ReleaseBuffer", params = listOf(MemoryAddress::class.java)
        )
    )

    private val stopRecording = NativeRegistry.register(
        FunctionDescription(
            "StopRecording", params = listOf(MemoryAddress::class.java)
        )
    )

    fun newState(): StructInstance {
        return state.new()
    }

    fun startRecording(state: StructInstance) {
        NativeRegistry[startRecording].invoke(state.segment.address())
        val hr = MemoryAccess.getIntAtOffset(state.segment, state[0])
        check(hr >= 0) {
            "Failed to start recording! Code: $hr  MSG: ${CLinker.toJavaString(MemoryAccess.getAddressAtOffset(state.segment, state[2]))}"
        }
    }

    fun getNextPacketSize(state: StructInstance) {
        NativeRegistry[getNextPacketSize].invoke(state.segment.address())
        val hr = MemoryAccess.getIntAtOffset(state.segment, state[0])
        check(hr >= 0) {
            "Failed to get next packet size! Code: $hr  MSG: ${CLinker.toJavaString(MemoryAccess.getAddressAtOffset(state.segment, state[2]))}"
        }
    }

    fun getBuffer(state: StructInstance) {
        NativeRegistry[getBuffer].invoke(state.segment.address())
        val hr = MemoryAccess.getIntAtOffset(state.segment, state[0])
        check(hr >= 0) {
            "Failed to get buffer! Code: $hr  MSG: ${CLinker.toJavaString(MemoryAccess.getAddressAtOffset(state.segment, state[2]))}"
        }
    }

    fun releaseBuffer(state: StructInstance) {
        NativeRegistry[releaseBuffer].invoke(state.segment.address())
        val hr = MemoryAccess.getIntAtOffset(state.segment, state[0])
        check(hr >= 0) {
            "Failed to release buffer! Code: $hr  MSG: ${CLinker.toJavaString(MemoryAccess.getAddressAtOffset(state.segment, state[2]))}"
        }
    }

    fun stopRecording(state: StructInstance) {
        NativeRegistry[stopRecording].invoke(state.segment.address())
        val hr = MemoryAccess.getIntAtOffset(state.segment, state[0])
        check(hr >= 0) {
            "Failed to stop recording! Code: $hr  MSG: ${CLinker.toJavaString(MemoryAccess.getAddressAtOffset(state.segment, state[2]))}"
        }
    }

}