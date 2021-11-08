package tech.poder.overlay

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment

data class RectReader(val left: UInt, val top: UInt, val right: UInt, val bottom: UInt) {
    companion object {
        fun fromMemorySegment(segment: MemorySegment): RectReader {
            return RectReader(
                MemoryAccess.getIntAtIndex(segment, 0).toUInt(),
                MemoryAccess.getIntAtIndex(segment, 1).toUInt(),
                MemoryAccess.getIntAtIndex(segment, 2).toUInt(),
                MemoryAccess.getIntAtIndex(segment, 3).toUInt()
            )
        }
    }

    val width = right - left
    val height = bottom - top
    val area = width * height
}
