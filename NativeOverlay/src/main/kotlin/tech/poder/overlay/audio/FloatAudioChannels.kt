package tech.poder.overlay.audio

import tech.poder.overlay.utils.NumberUtils
import kotlin.math.round

@JvmInline
value class FloatAudioChannels(val data: Array<FloatArray>) : AudioChannel {
    companion object {

        fun process(data: ByteArray, format: FormatData, bigEndian: Boolean = false): AudioChannel {
            val bytesPerChannel = 4
            var offset = 0
            val amountOfFloats = (data.size / bytesPerChannel) / format.channels.size
            val buffers = Array(format.channels.size) {
                FloatArray(amountOfFloats)
            }
            repeat(amountOfFloats) { floatIndex ->
                repeat(format.channels.size) { bufferIndex ->
                    var res = if (bigEndian) {
                        NumberUtils.floatFromBytesBE(data, offset)
                    } else {
                        NumberUtils.floatFromBytes(data, offset)
                    }
                    if (res.isInfinite() || res.isNaN()) {
                        res = 0f
                    }
                    buffers[bufferIndex][floatIndex] = res
                    offset += bytesPerChannel
                }
            }
            return FloatAudioChannels(buffers)
        }

        fun min(channel: FloatArray): Float {
            return channel.minOf { it }
        }

        fun max(channel: FloatArray): Float {
            return channel.maxOf { it }
        }

        fun min(channel: FloatAudioChannels): Float {
            return channel.data.minOf { it.minOf { it } }
        }

        fun max(channel: FloatAudioChannels): Float {
            return channel.data.maxOf { it.maxOf { it } }
        }
    }

    override fun toBytes(bigEndian: Boolean): ByteArray {
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

    private fun normalize(target: Float, left: Float, right: Float): Float {
        return left * target + right
    }

    fun calcLeft(a: Float, b: Float, c: Float, d: Float): Float {
        return (d - c) / (b - a)
    }

    fun calcRight(a: Float, b: Float, c: Float, d: Float): Float {
        return (c*b - a*d) / (b - a)
    }

    fun toPCMShort(independent: Boolean = false): PCMShortAudioChannels {

        var min = if (independent) {
            0f
        } else {
            min(this)
        }
        var max = if (independent) {
            0f
        } else {
            max(this)
        }

        var left = if (independent) {
            0f
        } else {
            calcLeft(min, max, -1f, 1f)
        }
        var right = if (independent) {
            0f
        } else {
            calcRight(min, max, -1f, 1f)
        }

        return PCMShortAudioChannels(Array(data.size) { index1 ->
            if (independent) {
                min = min(data[index1])
                max = max(data[index1])
                left = calcLeft(min, max, -1f, 1f)
                right = calcRight(min, max, -1f, 1f)
            }
            ShortArray(data[index1].size) { index2 ->
                println("${normalize(data[index1][index2], left, right)} -> ${round((normalize(data[index1][index2], left, right) * 32767.0) + 0.5).toInt().toShort()}")
                round((normalize(data[index1][index2], left, right) * 32767.0) + 0.5).toInt().toShort()
            }
        })
    }

    fun toNormal(independent: Boolean = false): FloatAudioChannels {

        var min = if (independent) {
            0f
        } else {
            min(this)
        }
        var max = if (independent) {
            0f
        } else {
            max(this)
        }

        var left = if (independent) {
            0f
        } else {
            calcLeft(min, max, -1f, 1f)
        }
        var right = if (independent) {
            0f
        } else {
            calcRight(min, max, -1f, 1f)
        }

        return FloatAudioChannels(Array(data.size) { index1 ->
            if (independent) {
                min = min(data[index1])
                max = max(data[index1])
                left = calcLeft(min, max, -1f, 1f)
                right = calcRight(min, max, -1f, 1f)
            }
            FloatArray(data[index1].size) { index2 ->
                println("${data[index1][index2]} -> ${normalize(data[index1][index2], left, right)}")
                normalize(data[index1][index2], left, right)
            }
        })
    }
}
