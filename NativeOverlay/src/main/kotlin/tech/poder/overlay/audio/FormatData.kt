package tech.poder.overlay.audio

import javax.sound.sampled.AudioFormat

data class FormatData(val tag: FormatFlag, val channels: List<Channel>, val sampleRate: Int, val blockAlignment: Short, val bitsPerChannel: Short, val samples: Short, val unsigned: Boolean = false, val bigEndian: Boolean = false) {
    fun toFormat(): AudioFormat {
        return if (tag == FormatFlag.IEEE_FLOAT) {
            AudioFormat(
                AudioFormat.Encoding.PCM_FLOAT,
                sampleRate.toFloat(),
                blockAlignment * Byte.SIZE_BITS,
                channels.size,
                blockAlignment.toInt(),
                sampleRate.toFloat(),
                bigEndian
            )
        } else {
            if (unsigned) {
                AudioFormat(
                    AudioFormat.Encoding.PCM_UNSIGNED,
                    sampleRate.toFloat(),
                    blockAlignment * Byte.SIZE_BITS,
                    channels.size,
                    blockAlignment.toInt(),
                    sampleRate.toFloat(),
                    bigEndian
                )
            } else {
                AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sampleRate.toFloat(),
                    blockAlignment * Byte.SIZE_BITS,
                    channels.size,
                    blockAlignment.toInt(),
                    sampleRate.toFloat(),
                    bigEndian
                )
            }
        }
    }
}
