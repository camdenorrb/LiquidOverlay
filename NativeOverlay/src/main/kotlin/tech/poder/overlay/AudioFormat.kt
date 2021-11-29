package tech.poder.overlay

import jdk.incubator.foreign.MemoryAccess

interface AudioFormat {
    companion object {
        fun getFormat(format: StructInstance): String {
            val builder = StringBuilder("Audio Format: \n")
            val tag = MemoryAccess.getShortAtOffset(format.segment, format[0])
            builder.appendLine("Format Tag: $tag")
            if (tag.toInt() == -2) {
                builder.appendLine("WAVE_FORMAT_EXTENSIBLE Detected!")
            }
            builder.appendLine("Number of Channels: ${MemoryAccess.getShortAtOffset(format.segment, format[1])}")
            builder.appendLine("Sample Rate: ${MemoryAccess.getIntAtOffset(format.segment, format[2])}Hz")
            builder.appendLine("Bytes Per Second: ${MemoryAccess.getIntAtOffset(format.segment, format[3])}")
            builder.appendLine("Block Alignment: ${MemoryAccess.getShortAtOffset(format.segment, format[4])}")
            builder.appendLine("Bits Per Channel: ${MemoryAccess.getShortAtOffset(format.segment, format[5])}")
            builder.appendLine("Number of Extra Bytes: ${MemoryAccess.getShortAtOffset(format.segment, format[6])}")

            return builder.toString()
        }
    }

    fun getAudioStruct(): StructInstance
}