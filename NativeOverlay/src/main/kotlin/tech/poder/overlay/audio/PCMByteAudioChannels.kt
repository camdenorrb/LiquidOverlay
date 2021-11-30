package tech.poder.overlay.audio

@JvmInline
value class PCMByteAudioChannels(val data: Array<ByteArray>) : AudioChannel {
    companion object {
        fun process(data: ByteArray, format: FormatData): AudioChannel {
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

    override fun toBytes(bigEndian: Boolean): ByteArray {
        val result = ByteArray(data[0].size * data.size)
        var offset = 0
        repeat(data[0].size) {
            repeat(data.size) { index ->
                data[index][it] = result[offset]
                offset++
            }
        }
        return result
    }
}
