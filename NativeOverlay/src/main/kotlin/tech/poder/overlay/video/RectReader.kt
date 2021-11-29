package tech.poder.overlay.video

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope
import tech.poder.overlay.general.ExternalStorage

data class RectReader(val segment: MemorySegment, val left: UInt, val top: UInt, val right: UInt, val bottom: UInt) {

    val width = right - left
    val height = bottom - top
    val area by lazy { width * height }

    companion object {

        fun createSegment(): ExternalStorage {
            val scope = ResourceScope.newSharedScope()
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

}