package tech.poder.overlay

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope

@JvmInline
value class ExternalStorage(val segment: MemorySegment): AutoCloseable {
    companion object {
        val nullChar = "\u0000".toByteArray(Charsets.UTF_16)
        fun fromString(str: String): ExternalStorage {
            val newScope = ResourceScope.newConfinedScope()
            str.toByteArray(Charsets.UTF_16).let { bytes ->
                val segment = MemorySegment.allocateNative(bytes.size + nullChar.size + 0L, newScope)
                bytes.forEachIndexed { index, byte ->
                    MemoryAccess.setByteAtOffset(segment, index.toLong(), byte)
                }
                val startIndex = bytes.size
                nullChar.forEachIndexed { index, byte ->
                    MemoryAccess.setByteAtOffset(segment, (startIndex + index).toLong(), byte)
                }

                return ExternalStorage(segment)
            }
            return ExternalStorage(CLinker.toCString(str, newScope))
        }
    }
    override fun close() {
        segment.scope().close()
    }
}