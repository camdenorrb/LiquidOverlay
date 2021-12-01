package tech.poder.overlay.data

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope

data class RectReader(val segment: MemorySegment, val left: UInt, val top: UInt, val right: UInt, val bottom: UInt) {

    val width = right - left
    val height = bottom - top
    val area = width * height

    companion object {

        fun createSegment(): ExternalStorage {
            return ExternalStorage(
                MemorySegment.allocateNative(CLinker.C_LONG.byteSize() * 4, ResourceScope.newSharedScope())
            )
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
