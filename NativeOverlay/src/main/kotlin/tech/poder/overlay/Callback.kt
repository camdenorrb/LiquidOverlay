package tech.poder.overlay

import jdk.incubator.foreign.*

object Callback {

    init {
        NativeRegistry.loadLib("user32")
        NativeRegistry.loadLib("kernel32")
        NativeRegistry.loadLib("psapi")
    }

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

    /*private val getDC = NativeRegistry.register(
        FunctionDescription(
            "GetDC", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
        )
    )*/

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

    private val getWindowRect = NativeRegistry.register(
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
        return NativeRegistry.registry[isWindowVisible].invoke(hwnd) as Int != 0 && NativeRegistry.registry[getWindow].invoke(
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

    @JvmStatic
    fun hookProc(code: Int, wParam: MemoryAddress, lParam: MemoryAddress): MemoryAddress {
        println("Called: $code")
        return MemoryAddress.NULL //todo run CallNextHookEx
    }

    private val forEachWindowUpcall = NativeRegistry.registerUpcallStatic(
        FunctionDescription(
            "forEachWindow", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        ), this::class.java
    )


    fun getProcesses(): List<Process> {

        val confinedStatic = ResourceScope.newConfinedScope()
        val id = NativeRegistry.newRegistryId(mutableListOf<MemoryAddress>())

        val idLocation = MemoryAddress.ofLong(id)
        val result = NativeRegistry.registry[enumWindows].invoke(forEachWindowUpcall, idLocation.address()) as Byte

        check(result != 0.toByte()) {
            "Callback failed"
        }

        val processes = NativeRegistry.dropRegistry(id) as MutableList<MemoryAddress>
        val PROCESS_QUERY_INFORMATION = 1024
        val PROCESS_VM_READ = 16
        //val PROCESS_VM_WRITE = 32
        val rectPlaceholder = MemorySegment.allocateNative(CLinker.C_LONG.byteSize() * 4, confinedStatic)
        val denseProcesses = mutableListOf<Process>()
        processes.forEach { processData ->
            val intHolderSeg = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), confinedStatic)
            val pidNoReason = NativeRegistry.registry[getWindowThreadProcessId].invoke(processData, intHolderSeg.address()) as Int
            check(pidNoReason != 0) {
                "Could not get pid"
            }
            val pid = MemoryAccess.getInt(intHolderSeg)
            val processHandle =
                NativeRegistry.registry[openProcess].invoke(0xFFFF or 983040 or 1048576, 0, pid) as MemoryAddress
            if (processHandle == MemoryAddress.NULL) {
                val code = NativeRegistry.registry[getLastError].invoke() as Int
                if (code == 5) {
                    return@forEach
                }
                println("Error Code: ${code}:")
            }

            val exeName = getExpanding { size, segment ->

                MemoryAccess.setByte(segment, 0)

                if (processHandle == MemoryAddress.NULL) {

                    val used = NativeRegistry.registry[getWindowModuleFileNameA].invoke(
                        processData, segment.address(), size.toInt()
                    ) as Int

                    if (used >= size) {
                        null
                    } else {
                        CLinker.toJavaString(segment)
                    }
                } else {

                    val used = NativeRegistry.registry[getModuleFileNameExA].invoke(
                        processHandle, MemoryAddress.NULL, segment.address(), size.toInt()
                    ) as Int

                    if (used == 0) {
                        println(NativeRegistry.registry[getLastError].invoke())
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
            check(NativeRegistry.registry[getWindowRect].invoke(processData, rectPlaceholder.address()) != 0.toByte()) {
                "Could not get rect"
            }
            val rect = RectReader.fromMemorySegment(rectPlaceholder)
            if (rect.area != 0u) {
                val clazzName = getExpanding { size, handle ->
                    val used = NativeRegistry.registry[getClassNameA].invoke(processData, handle.address(), size.toInt()) as Int
                    if (used >= size) {
                        null
                    } else {
                        CLinker.toJavaString(handle)
                    }
                }
                val title = getExpanding { size, handle ->

                    val used = NativeRegistry.registry[getWindowTextA].invoke(processData, handle.address(), size.toInt()) as Int

                    if (used >= size) {
                        null
                    } else {
                        CLinker.toJavaString(handle)
                    }
                }

                /*var result1 = NativeRegistry.registry[enumProcessModules].invoke(processHandle, MemoryAddress.NULL, 0, intHolderSeg.address()) as Int

                check(result1 != 0) {
                    "Could not get modules: ${NativeRegistry.registry[getLastError]}"
                }
                val tmpScope = ResourceScope.newConfinedScope()
                val bytesNeeded = MemoryAccess.getInt(intHolderSeg).toUInt().toLong()
                val moduleInfo = MemorySegment.allocateNative(bytesNeeded, tmpScope)
                result1 = NativeRegistry.registry[enumProcessModules].invoke(processHandle, moduleInfo.address(), bytesNeeded.toInt(), intHolderSeg.address()) as Int
                check(result1 != 0) {
                    "Could not get modules: ${NativeRegistry.registry[getLastError]}"
                }
                val modules = List((bytesNeeded / CLinker.C_POINTER.byteSize()).toInt()) {
                    MemoryAccess.getAddressAtIndex(moduleInfo, it.toLong())
                }
                tmpScope.close()*/
                denseProcesses.add(Process(processData, processHandle, exeName, clazzName, title, pid, rect))
            } else {
                return@forEach
            }
        }
        confinedStatic.close()

        return denseProcesses
    }
}