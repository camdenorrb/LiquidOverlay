package tech.poder.overlay.audio

import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

interface AudioChannels {
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