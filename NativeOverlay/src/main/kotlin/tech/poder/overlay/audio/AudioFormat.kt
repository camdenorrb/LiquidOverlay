package tech.poder.overlay.audio

import jdk.incubator.foreign.MemoryAccess
import tech.poder.overlay.general.Callback
import tech.poder.overlay.general.StructInstance
import kotlin.math.max

interface AudioFormat {
    companion object {

        fun getFormatData(format: StructInstance): FormatData {
            val tag = MemoryAccess.getShortAtOffset(format.segment, format[0])
            val numberOfChannels = MemoryAccess.getShortAtOffset(format.segment, format[1])
            val sampleRate = MemoryAccess.getIntAtOffset(format.segment, format[2])
            val bytesPerSecond = MemoryAccess.getIntAtOffset(format.segment, format[3])
            val blockAlignment = MemoryAccess.getShortAtOffset(format.segment, format[4])
            val bitsPerChannel = MemoryAccess.getShortAtOffset(format.segment, format[5])

            if (tag.toInt() == -2) {
                val upgraded = Callback.upgradeFormat(format)
                val samples = MemoryAccess.getShortAtOffset(upgraded.segment, upgraded[7])
                val channels = mutableListOf<Channel>()
                val mask = MemoryAccess.getIntAtOffset(upgraded.segment, upgraded[8])
                Channel.values().forEach {
                    if (mask and it.flag != 0) {
                        channels.add(it)
                    }
                }
                val tagData = when (val id = Callback.toJavaUUID(Callback.guidFromUpgradedFormat(upgraded))) {
                    FormatFlag.PCM.formatGUID -> {
                        FormatFlag.PCM
                    }
                    FormatFlag.IEEE_FLOAT.formatGUID -> {
                        FormatFlag.IEEE_FLOAT
                    }
                    else -> {
                        FormatFlag.UNKNOWN
                    }
                }
                return FormatData(
                    tagData,
                    channels,
                    sampleRate,
                    bytesPerSecond,
                    blockAlignment,
                    bitsPerChannel,
                    samples
                )
            } else {
                val channels = mutableListOf<Channel>()
                repeat(numberOfChannels.toInt()) {
                    channels.add(Channel.values()[it])
                }
                val tagData = when (tag) {
                    FormatFlag.PCM.baseFlag -> {
                        FormatFlag.PCM
                    }
                    FormatFlag.IEEE_FLOAT.baseFlag -> {
                        FormatFlag.IEEE_FLOAT
                    }
                    else -> {
                        FormatFlag.UNKNOWN
                    }
                }
                return FormatData(
                    tagData,
                    channels,
                    sampleRate,
                    bytesPerSecond,
                    blockAlignment,
                    bitsPerChannel,
                    bitsPerChannel
                )
            }
        }

        fun getFormat(format: StructInstance): String {
            return getFormat(getFormatData(format))
        }

        fun getFormat(data: FormatData): String {
            val builder = StringBuilder("Audio Format: \n")
            builder.appendLine("Format Tag: ${data.tag}")
            builder.appendLine("Sample Rate: ${data.sampleRate}Hz")
            builder.appendLine("Bytes Per Second: ${data.bytesPerSecond}")
            builder.appendLine("Block Alignment: ${data.blockAlignment}")
            builder.appendLine("Bits Per Channel: ${data.bitsPerChannel}")
            builder.appendLine("Samples: ${data.samples}")
            builder.appendLine("Channels = ${data.channels.joinToString(", ")}")

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
                MemoryAccess.setShortAtOffset(
                    format.segment,
                    format[7],
                    channelBitWidth
                )//todo set format[7]: UNION of Samples (channelBitWidth?)
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

    fun getInternalFormat(): FormatData {
        return getFormatData(this.getAudioStruct().format)
    }
}