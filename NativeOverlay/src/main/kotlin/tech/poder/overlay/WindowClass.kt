package tech.poder.overlay

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.MemorySegment

@JvmInline
value class WindowClass(val clazzPointer: MemoryAddress) : AutoCloseable {
    companion object {
        fun fromStorage(externalStorage: ExternalStorage): WindowClass {
            return WindowClass(externalStorage.segment.address())
        }

        val registerClassW = kotlin.run {
            NativeRegistry.loadLib("User32")
            NativeRegistry.register(
                FunctionDescription(
                    "RegisterClassW", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
                )
            )
        }
        val windclassw = StructDefinition.generate(
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

        val createSolidBrush = kotlin.run {
            NativeRegistry.loadLib("Gdi32")
            NativeRegistry.register(
                FunctionDescription(
                    "CreateSolidBrush", MemoryAddress::class.java, listOf(Int::class.java)
                )
            )
        }

        private val invisibleBrush = kotlin.run {
            val result = NativeRegistry[createSolidBrush].invoke(WindowManager.invisible) as MemoryAddress
            check(result != MemoryAddress.NULL) {
                "CreateSolidBrush failed: ${NativeRegistry[Callback.getLastError].invoke()}"
            }
            result
        }

        val hookProcUpcall = NativeRegistry.registerUpcallStatic(
            FunctionDescription(
                "hookProc",
                MemoryAddress::class.java,
                listOf(MemoryAddress::class.java, Int::class.java, MemoryAddress::class.java, MemoryAddress::class.java)
            ), Callback::class.java
        )

        fun define(name: String): WindowClass {
            val instance = NativeRegistry[Callback.getModuleHandle].invoke(MemoryAddress.NULL) as MemoryAddress
            val externalStorage = ExternalStorage.fromString(name)
            val struct = MemorySegment.allocateNative(windclassw.size, externalStorage.segment.scope())
            MemoryAccess.setInt(struct, 0x0002 or 0x0001)
            MemoryAccess.setAddressAtOffset(struct, windclassw.offset[1], hookProcUpcall)
            MemoryAccess.setAddressAtOffset(struct, windclassw.offset[4], instance)
            MemoryAccess.setAddressAtOffset(struct, windclassw.offset[7], invisibleBrush)
            MemoryAccess.setAddressAtOffset(struct, windclassw.offset[9], externalStorage.segment.address())
            println(struct.toByteArray().joinToString(", "))
            val result = NativeRegistry[registerClassW].invoke(struct.address()) as MemoryAddress

            check(result != MemoryAddress.NULL) {
                "Failed to register window class: ${NativeRegistry[Callback.getLastError].invoke()}"
            }

            return WindowClass(result)
        }
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
