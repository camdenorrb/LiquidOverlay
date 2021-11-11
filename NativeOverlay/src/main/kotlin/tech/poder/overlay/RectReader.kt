package tech.poder.overlay

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope

data class RectReader(val segment: MemorySegment, val left: UInt, val top: UInt, val right: UInt, val bottom: UInt) {

    val width = right - left
    val height = bottom - top
    val area by lazy { width * height }

    companion object {
        fun createSegment(): ExternalStorage {
            val scope = ResourceScope.newConfinedScope()
            val rectPlaceholder = MemorySegment.allocateNative(CLinker.C_LONG.byteSize() * 4, scope)
            return ExternalStorage(rectPlaceholder)
        }

        fun fromMemorySegment(segment: ExternalStorage): RectReader {
            return fromMemorySegment(segment.segment)
        }

        fun fromMemorySegment(segment: MemorySegment): RectReader {
            return RectReader(
                segment,
                MemoryAccess.getIntAtIndex(segment, 0).toUInt(),
                MemoryAccess.getIntAtIndex(segment, 1).toUInt(),
                MemoryAccess.getIntAtIndex(segment, 2).toUInt(),
                MemoryAccess.getIntAtIndex(segment, 3).toUInt()
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RectReader

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + height.hashCode()
        return result
    }

}
