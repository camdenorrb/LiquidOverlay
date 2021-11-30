package tech.poder.overlay.audio

import tech.poder.overlay.general.NumberUtils

@JvmInline
value class PCMIntAudioChannels(val data: Array<IntArray>): AudioChannel {
    companion object {
        fun process(data: ByteArray, format: FormatData): AudioChannel {
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

    override fun toBytes(bigEndian: Boolean): ByteArray {
        val result = ByteArray(data[0].size * Float.SIZE_BYTES * data.size)
        var offset = 0
        repeat(data[0].size) {
            repeat(data.size) { index ->
                if (bigEndian) {
                    TODO()
                } else {
                    NumberUtils.bytesFromInt(data[index][it], result, offset)
                }
                offset += Int.SIZE_BYTES
            }
        }
        return result
    }
}
