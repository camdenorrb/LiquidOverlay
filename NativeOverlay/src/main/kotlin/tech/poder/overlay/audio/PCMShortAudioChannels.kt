package tech.poder.overlay.audio

import tech.poder.overlay.general.NumberUtils

@JvmInline
value class PCMShortAudioChannels(val data: Array<ShortArray>): AudioChannels {
    companion object {
        fun process(data: ByteArray, format: FormatData): AudioChannels {
            val buffers = Array(format.channels.size) {
                ShortArray(data.size / format.channels.size)
            }
            val bytesPerChannel = 2
            var offset = 0
            var index = 0
            repeat((data.size / bytesPerChannel) / format.channels.size) {
                repeat(format.channels.size) { bufferIndex ->
                    buffers[bufferIndex][index + it] = NumberUtils.shortFromBytes(data, offset)
                    offset += bytesPerChannel
                }
                index += bytesPerChannel
            }
            return PCMShortAudioChannels(buffers)
        }
    }
}
