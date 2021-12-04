package tech.poder.overlay.structs

import jdk.incubator.foreign.MemoryAddress
import tech.poder.overlay.data.Struct

object KatDLLStructs {

    val formatList = Struct.generate(
        Int::class.java,
        MemoryAddress::class.java
    )

    val state = Struct.generate(
        Int::class.java,
        Int::class.java,
        Int::class.java,
        Double::class.java,
        MemoryAddress::class.java,
        MemoryAddress::class.java,
        MemoryAddress::class.java,
        MemoryAddress::class.java,
        MemoryAddress::class.java,
        MemoryAddress::class.java,
    )

}