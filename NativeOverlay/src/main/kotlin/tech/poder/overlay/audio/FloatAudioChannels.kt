package tech.poder.overlay.audio

import tech.poder.overlay.audio.base.AudioChannel
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

        fun min(channel: FloatArray): Double {
            return channel.minOf { it }.toDouble()
        }

        fun max(channel: FloatArray): Double {
            return channel.maxOf { it }.toDouble()
        }

        fun min(channel: FloatAudioChannels): Double {
            return channel.data.minOf { it.minOf { it } }.toDouble()
        }

        fun max(channel: FloatAudioChannels): Double {
            return channel.data.maxOf { it.maxOf { it } }.toDouble()
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

    private fun normalize(target: Float, left: Double, right: Double): Double {
        return left * target.toDouble() + right
    }

    fun calcLeft(a: Double, b: Double, c: Double, d: Double): Double {
        return (d - c) / (b - a)
    }

    fun calcRight(a: Double, b: Double, c: Double, d: Double): Double {
        return (c*b - a*d) / (b - a)
    }

    fun toPCMShort(independent: Boolean = false): PCMShortAudioChannels {

        var min = if (independent) {
            0.0
        } else {
            min(this)
        }
        var max = if (independent) {
            0.0
        } else {
            max(this)
        }

        var left = if (independent) {
            0.0
        } else {
            calcLeft(min, max, -1.0, 1.0)
        }
        var right = if (independent) {
            0.0
        } else {
            calcRight(min, max, -1.0, 1.0)
        }

        return PCMShortAudioChannels(Array(data.size) { index1 ->
            if (independent) {
                min = min(data[index1])
                max = max(data[index1])
                left = calcLeft(min, max, -1.0, 1.0)
                right = calcRight(min, max, -1.0, 1.0)
            }
            ShortArray(data[index1].size) { index2 ->
                println("${normalize(data[index1][index2], left, right)} -> ${round((normalize(data[index1][index2], left, right) * 32767.0) + 0.5).toInt().toShort()}")
                round((normalize(data[index1][index2], left, right) * 32767.0) + 0.5).toInt().toShort()
            }
        })
    }
}
