package tech.poder.overlay.structs

import jdk.incubator.foreign.MemoryAddress
import tech.poder.overlay.data.StructDefinition

object KatDLLStructs {

    val state = StructDefinition.generate(
        listOf(
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
    )

}