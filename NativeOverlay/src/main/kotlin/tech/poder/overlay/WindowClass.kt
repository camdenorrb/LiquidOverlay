package tech.poder.overlay

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.MemorySegment
import tech.poder.overlay.utils.NativeUtils

@JvmInline
value class WindowClass(val clazzPointer: MemoryAddress) : AutoCloseable {

    override fun close() {
        TODO("Not yet implemented")
    }


    companion object {

        val registerClassW = run {

            NativeUtils.loadLibraries("User32")

            NativeUtils.lookupMethodHandle(
                "RegisterClassW", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
            )
        }

        val windClassW = StructDefinition.generate(
            listOf(
                Int::class.java,
                MemoryAddress::class.java,
                Int::class.java,
                Int::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java
            )
        )

        val createSolidBrush = run {

            NativeUtils.loadLibraries("Gdi32")

            NativeUtils.lookupMethodHandle(
                "CreateSolidBrush", MemoryAddress::class.java, listOf(Int::class.java)
            )
        }

        private val invisibleBrush = (createSolidBrush(WindowManager.invisible) as MemoryAddress).apply {
            check(this != MemoryAddress.NULL) {
                "CreateSolidBrush failed: ${NativeAPI.getLastError()}"
            }
        }

        val hookProcUpcall = NativeUtils.lookupStaticMethodUpcall(
            NativeAPI::class.java,
            "hookProc",
            MemoryAddress::class.java,
            listOf(MemoryAddress::class.java, Int::class.java, MemoryAddress::class.java, MemoryAddress::class.java),
        )


        fun fromStorage(externalStorage: ExternalStorage): WindowClass {
            return WindowClass(externalStorage.segment.address())
        }

        fun define(name: String): WindowClass {

            val instance = NativeAPI.getModuleHandle(MemoryAddress.NULL) as MemoryAddress
            val externalStorage = ExternalStorage.fromString(name)
            val struct = MemorySegment.allocateNative(windClassW.size, externalStorage.segment.scope())

            MemoryAccess.setInt(struct, 0x0002 or 0x0001)
            MemoryAccess.setAddressAtOffset(struct, windClassW.offset[1], hookProcUpcall)
            MemoryAccess.setAddressAtOffset(struct, windClassW.offset[4], instance)
            MemoryAccess.setAddressAtOffset(struct, windClassW.offset[7], invisibleBrush)
            MemoryAccess.setAddressAtOffset(struct, windClassW.offset[9], externalStorage.segment.address())

            println(struct.toByteArray().joinToString(", "))

            // Error
            val result = registerClassW(struct.address()) as MemoryAddress

            check(result != MemoryAddress.NULL) {
                "Failed to register window class: ${NativeAPI.getLastError()}"
            }

            return WindowClass(result)

        }

    }

}
