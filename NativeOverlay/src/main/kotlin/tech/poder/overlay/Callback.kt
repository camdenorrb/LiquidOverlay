package tech.poder.overlay

import jdk.incubator.foreign.*
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.system.exitProcess

object Callback {

    val init = NativeRegistry.loadLib("Comctl32", "Ole32", "user32", "kernel32", "psapi")
    val init2 = NativeRegistry.loadLib(Paths.get("libnew.dll").toAbsolutePath())

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


    val CLSCTX_ALL = 23

    @JvmStatic
    fun dllCheck() {
        println("DLL_CHECK = ${NativeRegistry[getModuleHandle].invoke(MemoryAddress.NULL)}")
    }

    val coCreateInstance = NativeRegistry.register(
        FunctionDescription(
            "CoCreateInstance", Int::class.java, listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                Int::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java
            )
        )
    )
    var initCo = false

    val coInitializeEx = NativeRegistry.register(
        FunctionDescription(
            "CoInitializeEx", Int::class.java, listOf(
                MemoryAddress::class.java,
                Int::class.java,
            )
        )
    )

    val getAudioDeviceEndpoint = NativeRegistry.register(
        FunctionDescription(
            "GetAudioDeviceEndpoint",
            Int::class.java,
            listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        )
    )

    val deviceActivate = NativeRegistry.register(
        FunctionDescription(
            "DeviceActivate",
            Int::class.java,
            listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java,
            )
        )
    )

    val getMixFormat = NativeRegistry.register(
        FunctionDescription(
            "GetMixFormat",
            Int::class.java,
            listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                Int::class.java
            )
        )
    )

    val getBufferSize = NativeRegistry.register(
        FunctionDescription(
            "GetBufferSize",
            Int::class.java,
            listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java
            )
        )
    )

    val getService = NativeRegistry.register(
        FunctionDescription(
            "GetService",
            Int::class.java,
            listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java
            )
        )
    )

    val getHNSActualDuration = NativeRegistry.register(
        FunctionDescription(
            "GetHNSActualDuration",
            Double::class.java,
            listOf(
                MemoryAddress::class.java,
                Int::class.java,
                Int::class.java
            )
        )
    )

    val clientStart = NativeRegistry.register(
        FunctionDescription(
            "Start",
            Int::class.java,
            listOf(
                MemoryAddress::class.java
            )
        )
    )

    val clientStop = NativeRegistry.register(
        FunctionDescription(
            "Stop",
            Int::class.java,
            listOf(
                MemoryAddress::class.java
            )
        )
    )

    val getNextPacketSize = NativeRegistry.register(
        FunctionDescription(
            "GetNextPacketSize",
            Int::class.java,
            listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java
            )
        )
    )

    val getBuffer = NativeRegistry.register(
        FunctionDescription(
            "GetBuffer",
            Int::class.java,
            listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java
            )
        )
    )

    val releaseBuffer = NativeRegistry.register(
        FunctionDescription(
            "ReleaseBuffer",
            Int::class.java,
            listOf(
                MemoryAddress::class.java,
                Int::class.java
            )
        )
    )

    fun getEnumerator(): MemoryAddress {
        if (!initCo) {
            val res = NativeRegistry[coInitializeEx].invoke(MemoryAddress.NULL, 0)
            check(res == 0) {
                "CoInitializeEx failed: $res ${NativeRegistry[getLastError].invoke()}"
            }
            initCo = true
        }
        val enumerator = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
        val result = NativeRegistry[coCreateInstance].invoke(
            CLSID_MMDeviceEnumerator.address(),
            MemoryAddress.NULL,
            CLSCTX_ALL,
            IID_IMMDeviceEnumerator.address(),
            enumerator.address()
        )
        check(result == 0) {
            "CoCreateInstance failed: $result ${NativeRegistry[getLastError].invoke()}"
        }

        return MemoryAccess.getAddress(enumerator)
    }

    fun getDefaultAudioDevice(): MemoryAddress {
        val enumerator = getEnumerator()
        val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
        val result = NativeRegistry[getAudioDeviceEndpoint].invoke(enumerator, tmp.address()) as Int
        check(result == 0) {
            "GetAudioDeviceEndpoint failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
        val res = MemoryAccess.getAddress(tmp)
        tmp.scope().close()
        return res
    }

    fun activateAudioClient(device: MemoryAddress): MemoryAddress {
        val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
        val result = NativeRegistry[deviceActivate].invoke(device, tmp.address()) as Int
        check(result == 0) {
            "DeviceActivate failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
        val res = MemoryAccess.getAddress(tmp)
        tmp.scope().close()
        return res
    }

    fun getMixFormat(client: MemoryAddress, REFTIMES_PER_SEC: Int = 10000000): MemoryAddress {
        val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
        val result = NativeRegistry[getMixFormat].invoke(client, tmp.address(), REFTIMES_PER_SEC) as Int
        check(result == 0) {
            "GetMixFormat failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
        val res = MemoryAccess.getAddress(tmp)
        tmp.scope().close()
        return res
    }

    fun getBufferSize(client: MemoryAddress): UInt {
        val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
        val result = NativeRegistry[getBufferSize].invoke(client, tmp.address()) as Int
        check(result == 0) {
            "GetBufferSize failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
        val res = MemoryAccess.getInt(tmp)
        tmp.scope().close()
        return res.toUInt()
    }

    fun getService(client: MemoryAddress): MemoryAddress {
        val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
        val result = NativeRegistry[getService].invoke(client, tmp.address()) as Int
        check(result == 0) {
            "DeviceGetService failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
        val res = MemoryAccess.getAddress(tmp)
        tmp.scope().close()
        return res
    }

    fun getActualDuration(format: MemoryAddress, bufferSize: UInt, REFTIMES_PER_SEC: Int = 1000000): Double {
        val result = NativeRegistry[getService].invoke(format, bufferSize.toInt(), REFTIMES_PER_SEC) as Double
        return result
    }


    fun start(client: MemoryAddress) {
        val result = NativeRegistry[clientStart].invoke(client) as Int
        check(result == 0) {
            "Start failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
    }

    fun stop(client: MemoryAddress) {
        val result = NativeRegistry[clientStop].invoke(client) as Int
        check(result == 0) {
            "Stop failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
    }

    fun getNextPacketSize(service: MemoryAddress): UInt {
        val tmp = MemorySegment.allocateNative(CLinker.C_POINTER.byteSize(), ResourceScope.newConfinedScope())
        val result = NativeRegistry[getNextPacketSize].invoke(service, tmp.address()) as Int
        check(result == 0) {
            "GetNextPacketSize failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
        val res = MemoryAccess.getInt(tmp)
        tmp.scope().close()
        return res.toUInt()
    }

    fun getPacket(service: MemoryAddress): NativeBuffer {
        val pData = MemorySegment.allocateNative(CLinker.C_CHAR.byteSize(), ResourceScope.newConfinedScope())
        val numFramesAvailable = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), pData.scope())
        val flags = MemorySegment.allocateNative(CLinker.C_INT.byteSize(), pData.scope())
        val result = NativeRegistry[getBuffer].invoke(service, pData.address(), numFramesAvailable.address(), flags.address()) as Int
        check(result == 0) {
            "GetPacket failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
        val res = NativeBuffer(MemoryAccess.getByte(pData), MemoryAccess.getInt(flags), MemoryAccess.getInt(numFramesAvailable).toUInt())
        pData.scope().close()
        return res
    }

    fun deletePacket(service: MemoryAddress, buffer: NativeBuffer) {
        val result = NativeRegistry[releaseBuffer].invoke(service, buffer.numFramesAvailable) as Int
        check(result == 0) {
            "DeletePacket failed: $result ${NativeRegistry[getLastError].invoke()}"
        }
    }
}