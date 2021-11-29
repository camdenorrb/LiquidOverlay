package tech.poder.overlay.audio

import jdk.incubator.foreign.MemoryAccess
import tech.poder.overlay.general.Callback
import tech.poder.overlay.general.StructInstance
import kotlin.math.max

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
                val channels = mutableListOf<Channel>()
                val mask = MemoryAccess.getIntAtOffset(upgraded.segment, upgraded[8])
                Channel.values().forEach {
                    if (mask and it.flag != 0) {
                        channels.add(it)
                    }
                }
                builder.appendLine("Channel Mask = $mask: ${channels.joinToString(", ")}")
                when(val id = Callback.toJavaUUID(Callback.guidFromUpgradedFormat(upgraded))) {
                    FormatFlag.PCM.formatGUID -> {
                        builder.appendLine("Sub Format: PCM")
                    }
                    FormatFlag.IEEE_FLOAT.formatGUID -> {
                        builder.appendLine("Sub Format: IEEE_FLOAT")
                    }
                    else -> {
                        builder.appendLine("Sub Format: UNKNOWN{${id}}")
                    }
                }

            }

            return builder.toString()
        }

        fun generateFormat(
            formatFlag: FormatFlag,
            channels: List<Channel> = ChannelShortCut.KSAUDIO_SPEAKER_STEREO.channels,
            sampleRate: Int = 44100,
            channelBitWidth: Short = 16,
            extended: Boolean = true
        ): GeneratedFormat {
            var format = Callback.newFormat()
            val channelSize = max(channels.size, 1).toShort()
            if (extended) {
                format = Callback.upgradeFormat(format)
                MemoryAccess.setShortAtOffset(format.segment, format[0], FormatFlag.extendedBaseFlag)
            } else {
                MemoryAccess.setShortAtOffset(format.segment, format[0], formatFlag.baseFlag)
            }
            MemoryAccess.setShortAtOffset(format.segment, format[1], channelSize)
            MemoryAccess.setIntAtOffset(format.segment, format[2], sampleRate)
            val blockAlignment = ((channelBitWidth.toLong() * channelSize.toLong()) / 8L).toShort()
            MemoryAccess.setIntAtOffset(
                format.segment,
                format[3],
                (sampleRate.toLong() * blockAlignment).toInt()
            )
            MemoryAccess.setShortAtOffset(
                format.segment,
                format[4],
                blockAlignment
            )
            MemoryAccess.setShortAtOffset(format.segment, format[5], channelBitWidth)
            if (extended) {
                MemoryAccess.setShortAtOffset(format.segment, format[6], 22)
                MemoryAccess.setShortAtOffset(format.segment, format[7], channelBitWidth)//todo set format[7]: UNION of Samples (channelBitWidth?)
                var mask = 0
                channels.forEach {
                    mask = mask or it.flag
                }
                MemoryAccess.setIntAtOffset(format.segment, format[8], mask)
                val guidPart = Callback.guidFromUpgradedFormat(format)
                Callback.toGUID(formatFlag.formatGUID, guidPart)
            } else {
                MemoryAccess.setShortAtOffset(format.segment, format[6], 0)
            }

            return GeneratedFormat(format)
        }
    }

    fun getAudioStruct(): GeneratedFormat
}