package tech.poder.overlay

import jdk.incubator.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

object NativeRegistry {

    private val processStorage = ConcurrentHashMap<Long, Any>()
    private val symbolLookup = SymbolLookup.loaderLookup()
    private val handleLookup = MethodHandles.lookup()
    private val loadedLibs = mutableSetOf<String>()
    private val upcallScope = ResourceScope.newSharedScope()

    val registry = mutableListOf<MethodHandle>()

    operator fun get(id: Int): MethodHandle {
        return registry[id]
    }

    //val upcallRegistry = mutableListOf<MemoryAddress>()

    fun newRegistryId(type: Any): Long {
        var long = Random.nextLong()
        while (processStorage.putIfAbsent(long, type) != null) {
            long = Random.nextLong()
        }
        return long
    }

    fun getRegistry(id: Long): Any {
        return processStorage[id]!!
    }

    fun dropRegistry(id: Long): Any {
        return processStorage.remove(id)!!
    }

    fun loadLib(vararg libs: String) {
        libs.forEach { lib ->
            val realLib = lib.lowercase()
            if (!loadedLibs.add(realLib)) return@forEach
            System.loadLibrary(realLib)
        }
    }

    internal val clazzToMemoryLayout: Map<Class<*>, MemoryLayout> = mapOf(
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

        val method = symbolLookup.lookup(descriptor.name)

        check(method.isPresent) {
            "Could not find: \"$method\""
        }

        return register(method.get(), descriptor)
    }

    /**
     * Registers a function descriptor at address
     *
     * @param descriptor The function descriptor to register
     * @return The index of the descriptor
     */
    fun register(method: Addressable, descriptor: FunctionDescription): Int {

        registry.add(dataTypesToMethod(method, descriptor))

        return registry.size - 1
    }

    /**
     * Obtains an address to a static JVM method
     *
     * @param descriptor The function descriptor to register
     * @param staticClazz The class to obtain the method from
     * @return The memory address of the method
     */
    fun registerUpcallStatic(descriptor: FunctionDescription, staticClazz: Class<*>): MemoryAddress {
        val type = generateType(descriptor)
        val method = handleLookup.findStatic(staticClazz, descriptor.name, type)

        return methodToUpcall(method, descriptor)
    }

    private fun methodToUpcall(handle: MethodHandle, data: FunctionDescription): MemoryAddress {
        val description = generateDescriptor(data)
        return CLinker.getInstance().upcallStub(handle, description, upcallScope)
    }

    private fun dataTypesToMethod(location: Addressable, data: FunctionDescription): MethodHandle {
        return CLinker.getInstance().downcallHandle(location, generateType(data), generateDescriptor(data))
    }

    internal fun generateType(data: FunctionDescription): MethodType {
        return MethodType.methodType(clazzToPrimitive[data.returnType ?: Void.TYPE]!!, data.params.map { clazzToPrimitive[it]!! })
    }

    private fun generateDescriptor(data: FunctionDescription): FunctionDescriptor {
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