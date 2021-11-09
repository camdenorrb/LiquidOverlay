package tech.poder.overlay

import jdk.incubator.foreign.MemorySegment

@JvmInline
value class ExternalStorage(val segment: MemorySegment): AutoCloseable {
    override fun close() {
        segment.scope().close()
    }
}
