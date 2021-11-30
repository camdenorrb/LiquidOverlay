package tech.poder.overlay.audio

import tech.poder.overlay.general.NumberUtils

@JvmInline
value class PCMShortAudioChannels(val data: Array<ShortArray>): AudioChannel {
    companion object {
        fun process(data: ByteArray, format: FormatData): AudioChannel {
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

    override fun toBytes(bigEndian: Boolean): ByteArray {
        val result = ByteArray(data[0].size * Short.SIZE_BYTES * data.size)
        var offset = 0
        repeat(data[0].size) {
            repeat(data.size) { index ->
                if (bigEndian) {
                    NumberUtils.bytesFromShort(data[index][it], result, offset) //TODO
                } else {
                    NumberUtils.bytesFromShort(data[index][it], result, offset)
                }
                offset += Short.SIZE_BYTES
            }
        }
        return result
    }
}
