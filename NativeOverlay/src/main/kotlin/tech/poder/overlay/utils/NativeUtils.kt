package tech.poder.overlay.utils

import jdk.incubator.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.file.Path
import kotlin.io.path.name

object NativeUtils {

	val loadedLibraries = mutableSetOf<String>()

	private val upcallScope = ResourceScope.newSharedScope()

	private val symbolLookup = SymbolLookup.loaderLookup()

	private val handleLookup = MethodHandles.lookup()



	fun loadLibrary(path: Path) {
		if (loadedLibraries.add(path.name.lowercase())) {
			System.load(path.toAbsolutePath().toString())
		}
	}

	fun loadLibraries(vararg names: String) {
		names.forEach { name ->
			if (loadedLibraries.add(name.lowercase())) {
				System.loadLibrary(name)
			}
		}
	}

	// The same as NativeRegister.register(name, function)
	fun lookupMethodHandle(name: String, returnType: Class<*>? = null, parameterTypes: List<Class<*>> = emptyList()): MethodHandle {
		return dataTypesToMethod(
			symbolLookup.lookup(name).get(),
			returnType,
			parameterTypes,
		)
	}

	fun lookupStaticMethodUpcall(staticClazz: Class<*>, name: String, returnType: Class<*>? = null, parameterTypes: List<Class<*>> = emptyList()): MemoryAddress {
		return methodToUpcall(
			lookupStaticMethodHandle(staticClazz, name, returnType, parameterTypes),
			returnType,
			parameterTypes
		)
	}

	fun lookupStaticMethodHandle(staticClazz: Class<*>, name: String, returnType: Class<*>? = null, parameterTypes: List<Class<*>> = emptyList()): MethodHandle {
		return handleLookup.findStatic(
			staticClazz,
			name,
			MethodType.methodType(classAsPrimitive(returnType ?: Void.TYPE), parameterTypes.map(::classAsPrimitive))
		)
	}


	private fun methodToUpcall(handle: MethodHandle, returnType: Class<*>?, parameterTypes: List<Class<*>>): MemoryAddress {
		return CLinker.getInstance().upcallStub(
			handle,
			generateDescriptor(returnType, parameterTypes),
			upcallScope
		)
	}

	fun classAsPrimitive(type: Class<*>): Class<*> = when (type) {

		Boolean::class.java -> Int::class.java
		Byte::class.java -> Byte::class.java
		Short::class.java -> Short::class.java
		Int::class.java -> Int::class.java
		Long::class.java -> Long::class.java
		Float::class.java -> Float::class.java
		Double::class.java -> Double::class.java
		MemoryAddress::class.java -> MemoryAddress::class.java
		Void.TYPE -> Void.TYPE

		else -> error("Unsupported type: $type")
	}

	fun classToMemoryLayout(type: Class<*>) = when (type) {

		Boolean::class.java -> CLinker.C_INT
		Byte::class.java -> CLinker.C_CHAR
		Short::class.java -> CLinker.C_SHORT
		Int::class.java -> CLinker.C_INT
		Long::class.java -> CLinker.C_LONG
		Float::class.java -> CLinker.C_FLOAT
		Double::class.java -> CLinker.C_DOUBLE
		MemoryAddress::class.java -> CLinker.C_POINTER

		else -> error("Unsupported type: $type")
	}

	fun <T> getExpanding(block: (Long, MemorySegment) -> T?): T {

		var result: T? = null
		var size = 256L

		while (result == null) {
			ResourceScope.newConfinedScope().use { scope ->
				size *= 2L
				result = block.invoke(size, MemorySegment.allocateNative(size, scope))
			}
		}

		return result!!
	}


	private fun dataTypesToMethod(location: Addressable, returnType: Class<*>?, parameterTypes: List<Class<*>>): MethodHandle {
		return CLinker.getInstance().downcallHandle(
			location,
			generateType(returnType, parameterTypes),
			generateDescriptor(returnType, parameterTypes)
		)
	}

	private fun generateType(returnType: Class<*>?, parameterTypes: List<Class<*>>): MethodType {
		return MethodType.methodType(classAsPrimitive(returnType ?: Void.TYPE), parameterTypes.map(::classAsPrimitive))
	}

	private fun generateDescriptor(returnType: Class<*>?, parameterTypes: List<Class<*>>): FunctionDescriptor {
		return if (returnType == null) {
			FunctionDescriptor.ofVoid(*parameterTypes.map(::classToMemoryLayout).toTypedArray())
		}
		else {
			FunctionDescriptor.of(classToMemoryLayout(returnType), *parameterTypes.map(::classToMemoryLayout).toTypedArray())
		}
	}

}