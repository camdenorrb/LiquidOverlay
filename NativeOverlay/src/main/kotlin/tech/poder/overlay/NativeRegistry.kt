package tech.poder.overlay

import jdk.incubator.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodType

object NativeRegistry {

    val lookupSystem = SymbolLookup.loaderLookup()

    val registery = mutableListOf<MethodHandle>()


    private val clazzToMemoryLayout: Map<Class<*>, MemoryLayout> = mapOf(
        Boolean::class.java to CLinker.C_INT,
        Byte::class.java to CLinker.C_CHAR,
        Short::class.java to CLinker.C_SHORT,
        Int::class.java to CLinker.C_INT,
        Long::class.java to CLinker.C_LONG,
        Float::class.java to CLinker.C_FLOAT,
        Double::class.java to CLinker.C_DOUBLE,
        MemoryAddress::class.java to CLinker.C_POINTER
    )

    private val clazzToPrimitive: Map<Class<*>, Class<*>> = mapOf(
        Boolean::class.java to Int::class.java,
        Byte::class.java to Byte::class.java,
        Short::class.java to Short::class.java,
        Int::class.java to Int::class.java,
        Long::class.java to Long::class.java,
        Float::class.java to Float::class.java,
        Double::class.java to Double::class.java,
        MemoryAddress::class.java to MemoryAddress::class.java,
        Void.TYPE to Void.TYPE
    )


    /**
     * Registers a function descriptor
     *
     * @param descriptor The function descriptor to register
     * @return The index of the descriptor
     */
    fun register(descriptor: FunctionDescription): Int {

        val method = lookupSystem.lookup(descriptor.name)

        check(method.isPresent) {
            "Could not find: \"$method\""
        }

        registery.add(dataTypesToMethod(method.get(), descriptor))

        return registery.size - 1
    }


    private fun dataTypesToMethod(location: Addressable, data: FunctionDescription): MethodHandle {
        return CLinker.getInstance().downcallHandle(location, generateType(data), generateDescriptor(data))
    }

    internal fun generateType(data: FunctionDescription): MethodType {
        return MethodType.methodType(clazzToPrimitive[data.returnType ?: Void.TYPE]!!, data.params.map { clazzToPrimitive[it]!! })
    }

    internal fun generateDescriptor(data: FunctionDescription): FunctionDescriptor {
        return if (data.returnType == null) {
            FunctionDescriptor.ofVoid(*data.params.map { clazzToMemoryLayout[it]!! }.toTypedArray())
        }
        else {
            FunctionDescriptor.of(
                clazzToMemoryLayout[data.returnType]!!, *data.params.map { clazzToMemoryLayout[it]!! }.toTypedArray()
            )
        }
    }

}