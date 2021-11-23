package tech.poder.overlay.utils

import jdk.incubator.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.file.Path

object NativeUtils {

	private val symbolLookup = SymbolLookup.loaderLookup()

	private val handleLookup = MethodHandles.lookup()


	fun loadLibrary(path: Path) {
		System.loadLibrary(path.toAbsolutePath().toString())
	}

	fun loadLibraries(vararg names: String) {
		names.forEach { name ->
			System.loadLibrary(name)
		}
	}

	fun lookupMethodHandle(name: String, returnType: Class<*>? = null, parameterTypes: List<Class<*>> = emptyList()): MethodHandle {
		return dataTypesToMethod(symbolLookup.lookup(name).get(), returnType, parameterTypes)
	}

	fun lookupStaticMethodHandle(staticClazz: Class<*>, name: String, returnType: Class<*>? = null, parameterTypes: List<Class<*>> = emptyList()): MethodHandle {
		return handleLookup.findStatic(
			staticClazz,
			name,
			MethodType.methodType(classAsPrimitive(returnType ?: Void.TYPE), parameterTypes.map(::classAsPrimitive))
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

	fun classToMemoryLayout(type: Class<*>): MemoryLayout {
		return when (type) {

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
	}

	fun <T> getExpanding(invoke: (Long, MemorySegment) -> T?): T {

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


	private fun dataTypesToMethod(location: Addressable, returnType: Class<*>? = null, parameterTypes: List<Class<*>> = emptyList()): MethodHandle {
		return CLinker.getInstance().downcallHandle(location, generateType(), generateDescriptor(returnType, parameterTypes))
	}

	private fun generateType(returnType: Class<*>? = null, parameterTypes: List<Class<*>> = emptyList()): MethodType {
		return MethodType.methodType(classAsPrimitive(returnType ?: Void.TYPE), parameterTypes.map(::classAsPrimitive))
	}

	private fun generateDescriptor(returnType: Class<*>? = null, parameterTypes: List<Class<*>> = emptyList()): FunctionDescriptor {
		return if (returnType == null) {
			FunctionDescriptor.ofVoid(*parameterTypes.map(::classToMemoryLayout).toTypedArray())
		}
		else {
			FunctionDescriptor.of(
				classToMemoryLayout(returnType), *parameterTypes.map(::classToMemoryLayout).toTypedArray()
			)
		}
	}

}