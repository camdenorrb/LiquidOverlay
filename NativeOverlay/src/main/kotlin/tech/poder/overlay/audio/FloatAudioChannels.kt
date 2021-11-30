package tech.poder.overlay.audio

import tech.poder.overlay.general.NumberUtils

@JvmInline
value class FloatAudioChannels(val data: Array<FloatArray>): AudioChannels {
    companion object {

        var realMax = -2f
        var realMin = 2f

        fun process(data: ByteArray, format: FormatData, bigEndian: Boolean = true): AudioChannels {
            val bytesPerChannel = 4
            var offset = 0
            val amountOfFloats = (data.size / bytesPerChannel) / format.channels.size
            val buffers = Array(format.channels.size) {
                FloatArray(amountOfFloats)
            }
            repeat(amountOfFloats) { floatIndex ->
                repeat(format.channels.size) { bufferIndex ->
                    if (bigEndian) {
                        buffers[bufferIndex][floatIndex] = NumberUtils.floatFromBytesBE(data, offset)
                    } else {
                        buffers[bufferIndex][floatIndex] = NumberUtils.floatFromBytes(data, offset)
                    }
                    offset += bytesPerChannel
                }
            }
            return FloatAudioChannels(buffers)
        }
    }

    override fun toBytes(bigEndian: Boolean): ByteArray {
        var changed = false
        val min = data.minOf { it.minOf { it } }
        val max = data.maxOf { it.maxOf { it } }
        if (min < realMin) {
            realMin = min
            changed = true
        }
        if (max > realMax) {
            realMax = max
            changed = true
        }
        if (changed) {
            println("Min: $realMin")
            println("Max: $realMax")
        }
        val result = ByteArray(data[0].size * Float.SIZE_BYTES * data.size)
        var offset = 0
        repeat(data[0].size) {
            repeat(data.size) { index ->
                if (bigEndian) {
                    NumberUtils.bytesFromFloatBE(data[index][it], result, offset)
                } else {
                    NumberUtils.bytesFromFloat(data[index][it], result, offset)
                }
                offset += Float.SIZE_BYTES
            }
        }
        return result
    }
}
