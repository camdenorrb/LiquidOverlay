package tech.poder.overlay

data class StructDefinition(val offset: List<Long>, val size: Long) {
    companion object {
        fun generate(dataTypes: List<Class<*>>): StructDefinition {
            val types = dataTypes.map { NativeRegistry.clazzToMemoryLayout[it]!! }
            var largest = 1L
            types.forEach {
                if (it.byteSize() > largest) {
                    largest = it.byteSize()
                }
            }
            var offset = 0L
            val list = mutableListOf<Long>()

            types.forEachIndexed { index, memoryLayout ->
                if (index == 0 || types[index - 1].byteSize() == memoryLayout.byteSize()) {
                    list.add(offset)
                    offset += memoryLayout.byteSize()
                } else {
                    while (offset % largest != 0L) {
                        offset++ //padding
                    }
                    list.add(offset)
                    offset += memoryLayout.byteSize()
                }
            }
            return StructDefinition(list, offset)
        }
    }
}
