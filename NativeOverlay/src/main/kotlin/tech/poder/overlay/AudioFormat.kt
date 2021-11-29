package tech.poder.overlay

import jdk.incubator.foreign.MemoryAccess
import java.util.*

interface AudioFormat {
    companion object {
        fun getFormat(format: StructInstance): String {
            val builder = StringBuilder("Audio Format: \n")
            val tag = MemoryAccess.getShortAtOffset(format.segment, format[0])
            builder.appendLine("Format Tag: $tag")
            builder.appendLine("Number of Channels: ${MemoryAccess.getShortAtOffset(format.segment, format[1])}")
            builder.appendLine("Sample Rate: ${MemoryAccess.getIntAtOffset(format.segment, format[2])}Hz")
            builder.appendLine("Bytes Per Second: ${MemoryAccess.getIntAtOffset(format.segment, format[3])}")
            builder.appendLine("Block Alignment: ${MemoryAccess.getShortAtOffset(format.segment, format[4])}")
            builder.appendLine("Bits Per Channel: ${MemoryAccess.getShortAtOffset(format.segment, format[5])}")
            builder.appendLine("Number of Extra Bytes: ${MemoryAccess.getShortAtOffset(format.segment, format[6])}")
            if (tag.toInt() == -2) {
                builder.appendLine("WAVE_FORMAT_EXTENSIBLE Detected!")
                val upgraded = Callback.upgradeFormat(format)
                builder.appendLine("Samples: ${MemoryAccess.getShortAtOffset(upgraded.segment, upgraded[7])}")
                builder.appendLine("Channel Mask: ${MemoryAccess.getIntAtOffset(upgraded.segment, upgraded[8])}")
                builder.appendLine("Sub Format: ${UUID.nameUUIDFromBytes(upgraded.segment.asSlice(upgraded[9]).toByteArray())}")
            }

            return builder.toString()
        }
    }

    fun getAudioStruct(): StructInstance
}