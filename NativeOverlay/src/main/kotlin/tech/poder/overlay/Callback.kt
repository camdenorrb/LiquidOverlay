package tech.poder.overlay

import jdk.incubator.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

object Callback {

    val processStorage = ConcurrentHashMap<Long, Any>()

    val upcallScope = ResourceScope.newSharedScope()


    /*
    private fun dataTypesToMethod(location: Addressable, data: FunctionDescription): MethodHandle {
        val type = generateType(data)
        val description = generateDescription(data)

        return CLinker.getInstance().downcallHandle(location, type, description)
    }
    */

    private fun methodToUpcall(handle: MethodHandle, data: FunctionDescription): MemoryAddress {
        val description = NativeRegistry.generateDescriptor(data)
        return CLinker.getInstance().upcallStub(handle, description, upcallScope)
    }

    // TODO: Cache
    internal val methods: List<MethodHandle> by lazy {

        try {
            System.loadLibrary("user32")
            System.loadLibrary("kernel32")
            System.loadLibrary("psapi")
            System.loadLibrary("gdi32")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val methodNames = listOf(
            FunctionDescription( //0
                "GetLastError", Int::class.java
            ),
            FunctionDescription( //1
                "EnumWindows", Byte::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            ),
            FunctionDescription( //2
                "GetWindowThreadProcessId",
                Int::class.java,
                listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            ),
            FunctionDescription( //3
                "IsWindowVisible", Boolean::class.java, listOf(MemoryAddress::class.java)
            ),
            FunctionDescription( //4
                "GetWindow", MemoryAddress::class.java, listOf(MemoryAddress::class.java, Int::class.java)
            ),
            FunctionDescription( //5
                "GetDC", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
            ),
            FunctionDescription( //6
                "GetClassNameA",
                Int::class.java,
                listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
            ),
            FunctionDescription( //7
                "GetWindowTextA",
                Int::class.java,
                listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
            ),
            FunctionDescription( //8
                "GetWindowRect", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            ),
            FunctionDescription( //9
                "GetWindowModuleFileNameA",
                Int::class.java,
                listOf(MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
            ),
            FunctionDescription( //10
                "OpenProcess", MemoryAddress::class.java, listOf(Int::class.java, Boolean::class.java, Int::class.java)
            ),
            FunctionDescription( //11
                "GetModuleFileNameExA",
                Int::class.java,
                listOf(MemoryAddress::class.java, MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java)
            ),
            FunctionDescription( //12
                "CloseHandle", Boolean::class.java, listOf(MemoryAddress::class.java)
            ),
            FunctionDescription( //13
                "BeginPaint", MemoryAddress::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            ),
            FunctionDescription( //14
                "EndPaint", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            ),
            FunctionDescription( //15
                "TextOutA",
                Boolean::class.java,
                listOf(
                    MemoryAddress::class.java,
                    Int::class.java,
                    Int::class.java,
                    MemoryAddress::class.java,
                    Int::class.java
                )
            ),
            FunctionDescription( //16
                "UpdateWindow", Boolean::class.java, listOf(MemoryAddress::class.java)
            ),
        )

        methodNames.forEach(NativeRegistry::register)
        NativeRegistry.registery
    }

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
        return methods[3].invoke(hwnd) as Int != 0 && methods[4].invoke(hwnd, 4) as MemoryAddress == MemoryAddress.NULL
    }

    @JvmStatic
    fun forEachWindow(addr1: MemoryAddress, addr2: MemoryAddress): Int {

        val id = MemoryAccess.getLong(addr2.asSegment(CLinker.C_LONG_LONG.byteSize(), ResourceScope.globalScope()))

        if (isMainWindow(addr1)) {
            (processStorage[id] as MutableList<MemoryAddress>).add(addr1)
        }

        return 1
    }

    val forEachUpcall by lazy {

        val forEachDescriptor = FunctionDescription(
            "forEachWindow", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        )

        val lookup = MethodHandles.lookup()
        val type = NativeRegistry.generateType(forEachDescriptor)
        val method = lookup.findStatic(Callback::class.java, forEachDescriptor.name, type)

        methodToUpcall(method, forEachDescriptor)
    }

    fun getProcesses(): List<Process> {

        val confinedStatic = ResourceScope.newConfinedScope()
        var id = Random.nextLong()

        while (processStorage.putIfAbsent(id, mutableListOf<MemoryAddress>()) != null) {
            id = Random.nextLong()
        }

        val idLocation = MemorySegment.allocateNative(CLinker.C_LONG_LONG.byteSize(), confinedStatic)
        MemoryAccess.setLong(idLocation, id)
        val result = methods[1].invoke(forEachUpcall, idLocation.address()) as Byte

        check(result != 0.toByte()) {
            "Callback failed"
        }

        val processes = processStorage.remove(id)!!
        val PROCESS_QUERY_INFORMATION = 1024
        val rectPlaceholder = MemorySegment.allocateNative(CLinker.C_LONG.byteSize() * 4, confinedStatic)
        val denseProcesses = mutableListOf<Process>()
        (processes as MutableList<*>).forEach {
            val pidSeg = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), confinedStatic)
            val pidNoReason = methods[2].invoke(it, pidSeg.address()) as Int
            check(pidNoReason != 0) {
                "Could not get pid"
            }
            val pid = MemoryAccess.getInt(pidSeg)
            val handle = methods[10].invoke(PROCESS_QUERY_INFORMATION or 16, 0, pid) as MemoryAddress
            if (handle == MemoryAddress.NULL) {
                val code = methods[0].invoke() as Int
                if (code == 5) {
                    return@forEach
                }
                println("Error Code: ${code}:")
            }

            val exeName = getExpanding { size, segment ->

                MemoryAccess.setByte(segment, 0)

                if (handle == MemoryAddress.NULL) {

                    val used = methods[9].invoke(it, segment.address(), size.toInt()) as Int

                    if (used >= size) {
                        null
                    }
                    else {
                        CLinker.toJavaString(segment)
                    }
                }
                else {

                    val used = methods[11].invoke(handle, MemoryAddress.NULL, segment.address(), size.toInt()) as Int

                    if (used == 0) {
                        println(methods[0].invoke())
                    }

                    if (used >= size) {
                        null
                    }
                    else {
                        CLinker.toJavaString(segment)
                    }
                }
            }

            if (exeName.contains("C:\\System32") || exeName.contains("C:\\Windows")) {
                return@forEach
            }
            if (handle != MemoryAddress.NULL) {
                methods[12].invoke(handle)
            }
            check(methods[8].invoke(it, rectPlaceholder.address()) != 0.toByte()) {
                "Could not get rect"
            }
            val rect = RectReader.fromMemorySegment(rectPlaceholder)
            if (rect.area != 0u) {
                val clazzName = getExpanding { size, handle ->
                    val used = methods[6].invoke(it, handle.address(), size.toInt()) as Int
                    if (used >= size) {
                        null
                    } else {
                        CLinker.toJavaString(handle)
                    }
                }
                val title = getExpanding { size, handle ->

                    val used = methods[7].invoke(it, handle.address(), size.toInt()) as Int

                    if (used >= size) {
                        null
                    }
                    else {
                        CLinker.toJavaString(handle)
                    }
                }
                /*val dc = methods[5].invoke(it) as MemoryAddress
                check(dc != MemoryAddress.NULL) {
                    "Could not get DC"
                }*/
                denseProcesses.add(Process(it as MemoryAddress, exeName, clazzName, title, pid, rect))
            } else {
                return@forEach
            }
        }
        confinedStatic.close()

        return denseProcesses
    }
}