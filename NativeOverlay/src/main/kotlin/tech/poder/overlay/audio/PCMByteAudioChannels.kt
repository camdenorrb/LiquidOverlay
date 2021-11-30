package tech.poder.overlay.audio

import tech.poder.overlay.general.NumberUtils

@JvmInline
value class PCMByteAudioChannels(val data: Array<ByteArray>): AudioChannels {
    companion object {
        fun process(data: ByteArray, format: FormatData): AudioChannels {
            val buffers = Array(format.channels.size) {
                ByteArray(data.size / format.channels.size)
            }
            val bytesPerChannel = 1
            var offset = 0
            var index = 0
            repeat((data.size / bytesPerChannel) / format.channels.size) {
                repeat(format.channels.size) { bufferIndex ->
                    buffers[bufferIndex][index + it] = data[offset]
                    offset += bytesPerChannel
                }
                index += bytesPerChannel
            }
            return PCMByteAudioChannels(buffers)
        }
    }
}
