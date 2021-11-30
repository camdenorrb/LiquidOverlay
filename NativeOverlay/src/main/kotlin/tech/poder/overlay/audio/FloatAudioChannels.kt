package tech.poder.overlay.audio

import tech.poder.overlay.general.NumberUtils

@JvmInline
value class FloatAudioChannels(val data: Array<FloatArray>): AudioChannels {
    companion object {
        fun process(data: ByteArray, format: FormatData): AudioChannels {
            val bytesPerChannel = 4
            var offset = 0
            val amountOfFloats = (data.size / bytesPerChannel) / format.channels.size
            val buffers = Array(format.channels.size) {
                FloatArray(amountOfFloats)
            }
            repeat(amountOfFloats) { floatIndex ->
                repeat(format.channels.size) { bufferIndex ->
                    buffers[bufferIndex][floatIndex] = NumberUtils.floatFromBytesBE(data, offset)
                    offset += bytesPerChannel
                }
            }
            return FloatAudioChannels(buffers)
        }
    }
}
