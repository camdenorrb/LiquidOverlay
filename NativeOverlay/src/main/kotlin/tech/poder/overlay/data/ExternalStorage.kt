package tech.poder.overlay.data

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope

@JvmInline
value class ExternalStorage(val segment: MemorySegment) : AutoCloseable {

    override fun close() {
        segment.unload()
    }


    companion object {

        private val nullChar = "\u0000".toByteArray(Charsets.UTF_16LE)

        fun fromString(str: String): ExternalStorage {
            str.toByteArray(Charsets.UTF_16LE).let { bytes ->

                val segment = MemorySegment.allocateNative((bytes.size + nullChar.size).toLong(), ResourceScope.newSharedScope())

                bytes.forEachIndexed { index, byte ->
                    MemoryAccess.setByteAtOffset(segment, index.toLong(), byte)
                }

                nullChar.forEachIndexed { index, byte ->
                    MemoryAccess.setByteAtOffset(segment, (bytes.size + index).toLong(), byte)
                }

                return ExternalStorage(segment)
            }

        }
    }

}
