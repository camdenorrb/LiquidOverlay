package tech.poder.overlay.structs

import jdk.incubator.foreign.MemoryAddress
import tech.poder.overlay.data.Struct

object WinAPIStructs {

    val msgStruct = Struct.generate(
        MemoryAddress::class.java,
        Int::class.java,
        MemoryAddress::class.java,
        MemoryAddress::class.java,
        Int::class.java,
        Int::class.java,
        Int::class.java,
    )

    val waveFormatEx = Struct.generate(
        Short::class.java,
        Short::class.java,
        Int::class.java,
        Int::class.java,
        Short::class.java,
        Short::class.java,
        Short::class.java,
    )

    val guid = Struct.generate(
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

    val propertyKey = Struct.generate(
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

    val windClassW = Struct.generate(
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

    val waveFormatEx2 = Struct.generate(
        Short::class.java,
        Short::class.java,
        Int::class.java,
        Int::class.java,
        Short::class.java,
        Short::class.java,
        Short::class.java,
        Short::class.java,
        Int::class.java,
        Int::class.java,
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
    )

}