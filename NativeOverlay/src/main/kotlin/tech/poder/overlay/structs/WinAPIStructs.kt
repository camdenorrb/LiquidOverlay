package tech.poder.overlay.structs

import jdk.incubator.foreign.MemoryAddress
import tech.poder.overlay.data.StructDefinition

object WinAPIStructs {

    val msgStruct = StructDefinition.generate(
        listOf(
            MemoryAddress::class.java,
            Int::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
        )
    )

    val guid = StructDefinition.generate(
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

}