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

        val registerClassA = kotlin.run {
            NativeRegistry.loadLib("User32")
            NativeRegistry.register(
                FunctionDescription(
                    "RegisterClassW",
                    MemoryAddress::class.java,
                    listOf(MemoryAddress::class.java)
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

        fun define(name: String): WindowClass {
            val instance = NativeRegistry[Overlay.getModuleHandle].invoke(MemoryAddress.NULL) as MemoryAddress
            val externalStorage = ExternalStorage.fromString(name)
            val struct = MemorySegment.allocateNative(windclassw.size, externalStorage.segment.scope())

            MemoryAccess.setAddressAtOffset(struct, windclassw.offset[1], Overlay.hookProcUpcall)
            MemoryAccess.setAddressAtOffset(struct, windclassw.offset[4], instance)
            MemoryAccess.setAddressAtOffset(struct, windclassw.offset[9], externalStorage.segment.address())
            println(struct.toByteArray().joinToString(", "))
            val result = NativeRegistry[registerClassA].invoke(struct.address()) as MemoryAddress

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
