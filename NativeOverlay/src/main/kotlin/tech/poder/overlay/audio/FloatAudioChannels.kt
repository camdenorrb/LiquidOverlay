package tech.poder.overlay.audio

import tech.poder.overlay.utils.NumberUtils
import kotlin.math.log10
import kotlin.math.pow
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
            return channel.data.minOf { min(it) }
        }

        fun max(channel: FloatAudioChannels): Double {
            return channel.data.maxOf { max(it) }
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

    private fun normalize(target: Double, left: Double, right: Double): Double {
        return left * target + right
    }

    fun calcLeft(a: Double, b: Double, c: Double, d: Double): Double {
        return (d - c) / (b - a)
    }

    fun calcRight(a: Double, b: Double, c: Double, d: Double): Double {
        return (c * b - a * d) / (b - a)
    }

    fun calcDb(target: Double): Double {
        if (target == 0.0) {
            return target
        }
        return 20 * log10(target)
    }

    fun raw(target: Double): Double {
        if (target == 0.0) {
            return target
        }
        return 10.0.pow(target / 20.0)
    }

    fun rawToShort(target: Double): Short {
        if (target == 0.0) {
            return 0
        }
        return round(target * 65536).toInt().toShort()
    }

    fun toPCMShort(independent: Boolean = false): PCMShortAudioChannels {
        val min = -758.0
        val max = 770.0

        val left = calcLeft(min, max, -96.0, 0.0)
        val right = calcRight(min, max, -96.0, 0.0)

        return PCMShortAudioChannels(Array(data.size) { index1 ->
            ShortArray(data[index1].size) { index2 ->
                println(
                    "${data[index1][index2]} -> ${calcDb(data[index1][index2].toDouble())} -> ${
                        normalize(
                            calcDb(
                                data[index1][index2].toDouble()
                            ), left, right
                        )
                    } -> ${raw(normalize(calcDb(data[index1][index2].toDouble()), left, right))} -> ${rawToShort(raw(normalize(calcDb(data[index1][index2].toDouble()), left, right)))}"
                )
                println(
                    "${data[index1][index2]} -> ${calcDb(data[index1][index2].toDouble())} -> ${
                        normalize(
                            calcDb(
                                data[index1][index2].toDouble()
                            ), left, right
                        )
                    } -> ${raw(normalize(calcDb(data[index1][index2].toDouble()), left, right))} -> ${rawToShort(raw(normalize(calcDb(data[index1][index2].toDouble()), left, right)))}"
                )
                rawToShort(raw(normalize(calcDb(data[index1][index2].toDouble()), left, right)))
                //normalize(data[index1][index2].toDouble(), left, right).toInt().toShort()
                //println("${normalize(data[index1][index2], left, right)} -> ${round((normalize(data[index1][index2], left, right) * 32767.0) + 0.5).toInt().toShort()}")
                //round((normalize(data[index1][index2], left, right) * 32767.0) + 0.5).toInt().toShort()
            }
        })
    }
}
