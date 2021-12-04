package tech.poder.overlay.audio.base

import tech.poder.overlay.audio.FormatData
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

interface AudioChannel {

    fun toBytes(bigEndian: Boolean = false): ByteArray


    companion object {
        fun fromOther(input: ByteArray, sampleCount: Long, target: FormatData, given: FormatData): ByteArray {
            val targetFormat = target.toFormat()
            val givenFormat = given.toFormat()
            val input = AudioInputStream(ByteArrayInputStream(input), givenFormat, sampleCount).use { from ->
                AudioSystem.getAudioInputStream(targetFormat, from).use {
                    it.readAllBytes()
                }
            }
            return input
        }
    }

}