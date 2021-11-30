package tech.poder.overlay.audio

import tech.poder.overlay.general.NumberUtils

@JvmInline
value class PCMIntAudioChannels(val data: Array<IntArray>): AudioChannels {
    companion object {
        fun process(data: ByteArray, format: FormatData): AudioChannels {
            val buffers = Array(format.channels.size) {
                IntArray(data.size / format.channels.size)
            }
            val bytesPerChannel = 4
            var offset = 0
            var index = 0
            repeat((data.size / bytesPerChannel) / format.channels.size) {
                repeat(format.channels.size) { bufferIndex ->
                    buffers[bufferIndex][index + it] = NumberUtils.intFromBytes(data, offset)
                    offset += bytesPerChannel
                }
                index += bytesPerChannel
            }
            return PCMIntAudioChannels(buffers)
        }
    }
}
