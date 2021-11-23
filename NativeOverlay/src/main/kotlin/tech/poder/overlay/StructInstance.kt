package tech.poder.overlay

import jdk.incubator.foreign.MemorySegment

data class StructInstance(val segment: MemorySegment, val def: StructDefinition): AutoCloseable {
    override fun close() {
        segment.scope().close()
    }

    operator fun get(index: Int): Long {
        return def[index]
    }
}
