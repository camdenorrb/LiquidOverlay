package tech.poder.overlay.audio.base

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.ResourceScope
import tech.poder.overlay.api.WinAPI
import tech.poder.overlay.audio.*
import tech.poder.overlay.instance.BasicInstance
import kotlin.math.max

interface AudioFormat {

    fun getAudioStruct(): GeneratedFormat

    fun getInternalFormat(scope: ResourceScope): FormatData {
        return getFormatData(scope, getAudioStruct().format)
    }


    companion object {

        fun getFormatData(scope: ResourceScope, format: BasicInstance): FormatData {
            val tag = MemoryAccess.getShortAtOffset(format.segment, format.struct[0])
            val numberOfChannels = MemoryAccess.getShortAtOffset(format.segment, format.struct[1])
            val sampleRate = MemoryAccess.getIntAtOffset(format.segment, format.struct[2])
            val bytesPerSecond = MemoryAccess.getIntAtOffset(format.segment, format.struct[3])
            val blockAlignment = MemoryAccess.getShortAtOffset(format.segment, format.struct[4])
            val bitsPerChannel = MemoryAccess.getShortAtOffset(format.segment, format.struct[5])

            if (tag.toInt() == -2) {
                val upgraded = WinAPI.upgradeFormat(scope, format)
                val samples = MemoryAccess.getShortAtOffset(upgraded.segment, upgraded.struct[7])
                val channels = mutableListOf<Channel>()
                val mask = MemoryAccess.getIntAtOffset(upgraded.segment, upgraded.struct[8])
                Channel.values().forEach {
                    if (mask and it.flag != 0) {
                        channels.add(it)
                    }
                }
                val tagData = when (val id = WinAPI.toJavaUUID(WinAPI.guidFromUpgradedFormat(upgraded))) {
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
                    blockAlignment,
                    bitsPerChannel,
                    bitsPerChannel
                )
            }
        }

        fun getFormat(scope: ResourceScope, format: BasicInstance): String {
            return getFormat(getFormatData(scope, format))
        }

        fun getFormat(data: FormatData): String {
            val builder = StringBuilder("Audio Format: \n")
            builder.appendLine("Format Tag: ${data.tag}")
            builder.appendLine("Sample Rate: ${data.sampleRate}Hz")
            builder.appendLine("Block Alignment: ${data.blockAlignment}")
            builder.appendLine("Bits Per Channel: ${data.bitsPerChannel}")
            builder.appendLine("Samples: ${data.samples}")
            builder.appendLine("Channels = ${data.channels.joinToString(", ")}")

            return builder.toString()
        }

        fun generateFormat(
            formatFlag: FormatFlag,
            scope: ResourceScope,
            channels: List<Channel> = ChannelShortCut.KSAUDIO_SPEAKER_STEREO.channels,
            sampleRate: Int = 44100,
            channelBitWidth: Short = 16,
            extended: Boolean = true,
        ): GeneratedFormat {
            var format = WinAPI.newFormat(scope)
            val channelSize = max(channels.size, 1).toShort()
            if (extended) {
                format = WinAPI.upgradeFormat(scope, format)
                MemoryAccess.setShortAtOffset(format.segment, format.struct[0], FormatFlag.extendedBaseFlag)
            } else {
                MemoryAccess.setShortAtOffset(format.segment, format.struct[0], formatFlag.baseFlag)
            }
            MemoryAccess.setShortAtOffset(format.segment, format.struct[1], channelSize)
            MemoryAccess.setIntAtOffset(format.segment, format.struct[2], sampleRate)
            val blockAlignment = ((channelBitWidth.toLong() * channelSize.toLong()) / 8L).toShort()
            MemoryAccess.setIntAtOffset(
                format.segment,
                format.struct[3],
                (sampleRate.toLong() * blockAlignment).toInt()
            )
            MemoryAccess.setShortAtOffset(
                format.segment,
                format.struct[4],
                blockAlignment
            )
            MemoryAccess.setShortAtOffset(format.segment, format.struct[5], channelBitWidth)
            if (extended) {
                MemoryAccess.setShortAtOffset(format.segment, format.struct[6], 22)
                MemoryAccess.setShortAtOffset(
                    format.segment,
                    format.struct[7],
                    channelBitWidth
                )//todo set format.struct[7]: UNION of Samples (channelBitWidth?)
                var mask = 0
                channels.forEach {
                    mask = mask or it.flag
                }
                MemoryAccess.setIntAtOffset(format.segment, format.struct[8], mask)
                val guidPart = WinAPI.guidFromUpgradedFormat(format)
                WinAPI.toGUID(formatFlag.formatGUID, guidPart)
            } else {
                MemoryAccess.setShortAtOffset(format.segment, format.struct[6], 0)
            }

            return GeneratedFormat(format)
        }
    }

}