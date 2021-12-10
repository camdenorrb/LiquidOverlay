package tech.poder.overlay.window

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.MemorySegment
import tech.poder.overlay.api.WinAPI
import tech.poder.overlay.data.ExternalStorage
import tech.poder.overlay.handles.WinAPIHandles
import tech.poder.overlay.structs.WinAPIStructs
import tech.poder.overlay.utils.NativeUtils

@JvmInline
value class WindowClass(val clazzPointer: MemoryAddress) : AutoCloseable {

    override fun close() {
        TODO("Not yet implemented")
    }


    companion object {

        private val invisibleBrush = (WinAPIHandles.createSolidBrush(WindowManager.invisible) as MemoryAddress).apply {
            check(this != MemoryAddress.NULL) {
                "CreateSolidBrush failed: ${WinAPIHandles.getLastError()}"
            }
        }

        private val hookProcUpcall = NativeUtils.lookupStaticMethodUpcall(
            WinAPI::class.java,
            "hookProc",
            MemoryAddress::class.java,
            listOf(MemoryAddress::class.java, Int::class.java, MemoryAddress::class.java, MemoryAddress::class.java),
        )


        fun fromStorage(externalStorage: ExternalStorage): WindowClass {
            return WindowClass(externalStorage.segment.address())
        }

        fun define(name: String): WindowClass {

            val instance = WinAPIHandles.getModuleHandleA(MemoryAddress.NULL) as MemoryAddress
            val externalStorage = ExternalStorage.fromString(name)
            val struct = MemorySegment.allocateNative(WinAPIStructs.windClassW.size, externalStorage.segment.scope())

            MemoryAccess.setInt(struct, 0x0002 or 0x0001)
            MemoryAccess.setAddressAtOffset(struct, WinAPIStructs.windClassW.offset[1], hookProcUpcall)
            MemoryAccess.setAddressAtOffset(struct, WinAPIStructs.windClassW.offset[4], instance)
            MemoryAccess.setAddressAtOffset(struct, WinAPIStructs.windClassW.offset[7], invisibleBrush)
            MemoryAccess.setAddressAtOffset(struct, WinAPIStructs.windClassW.offset[9], externalStorage.segment.address())

            val result = WinAPIHandles.registerClassW(struct.address()) as MemoryAddress

            check(result != MemoryAddress.NULL) {
                "Failed to register window class: ${WinAPIHandles.getLastError()}"
            }

            return WindowClass(result)
        }
    }

}
