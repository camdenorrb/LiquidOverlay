package tech.poder.overlay.data

import jdk.incubator.foreign.MemoryLayout
import tech.poder.overlay.utils.NativeUtils

data class StructDefinition(val offset: List<Long>, val size: Long) {

    operator fun get(index: Int): Long {
        return offset[index]
    }


    companion object {

        fun generate(dataTypes: List<Class<*>>): StructDefinition {

            val types = dataTypes.map(NativeUtils::classToMemoryLayout)
            val maxByteSize = types.maxOfOrNull(MemoryLayout::byteSize) ?: 1L

            val offsets = mutableListOf<Long>()
            var size = 0L

            types.forEachIndexed { index, memoryLayout ->

                // If should apply padding
                if (index != 0 && types[index - 1].byteSize() != memoryLayout.byteSize()) {
                    while (size % maxByteSize != 0L) {
                        size++
                    }
                }

                offsets.add(size)
                size += memoryLayout.byteSize()
            }

            return StructDefinition(offsets, size)
        }

    }


}
