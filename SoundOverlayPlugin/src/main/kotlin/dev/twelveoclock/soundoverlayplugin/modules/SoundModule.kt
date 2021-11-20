package dev.twelveoclock.soundoverlayplugin.modules

import dev.twelveoclock.liquidoverlay.modules.base.BasicModule
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import kotlin.math.*

class SoundModule : BasicModule() {

    private val peakLefts = mutableListOf<Double>()

    private val peakRights = mutableListOf<Double>()


    override fun onEnable() {

    }

    override fun onDisable() {
        peakLefts.clear()
        peakRights.clear()
    }


    fun tick(audioFormat: AudioFormat, byteArray: ByteArray): TickData {

        val (left, right) = splitChannels(audioFormat, byteArray)

        val peakLeft = calculatePeakAndRms(audioFormat, left).peak
        val peakRight = calculatePeakAndRms(audioFormat, right).peak

        peakRights.add(calculatePeakAndRms(audioFormat, right).peak)

        if (peakLefts.size > SAMPLE_SIZE) {
            peakLefts.removeAt(0)
        }
        if (peakRights.size > SAMPLE_SIZE) {
            peakRights.removeAt(0)
        }

        return TickData(
            leftPeak = peakLeft,
            rightPeak = peakRight,
        )
    }


    fun averagePeakLeft(): Double {
        return peakLefts.average()
    }

    fun averagePeakRight(): Double {
        return peakRights.average()
    }


    fun splitChannels(audioFormat: AudioFormat, byteArray: ByteArray): List<ByteArray> {
        return splitChannels(
            AudioInputStream(ByteArrayInputStream(byteArray), audioFormat, byteArray.size.toLong())
        )
    }

    fun splitChannels(audioInputStream: AudioInputStream): List<ByteArray> {

        val bytesPerChannel = ceil(audioInputStream.format.frameSize / audioInputStream.format.channels.toDouble()).toInt()

        val outputs = List(audioInputStream.format.channels) {
            ByteArrayOutputStream()
        }

        val sampleBytes = ByteArray((audioInputStream.format.sampleSizeInBits / Byte.SIZE_BITS) * audioInputStream.format.channels)

        while (true) {

            if (audioInputStream.read(sampleBytes) == -1) {
                break
            }

            var index = 0

            repeat(outputs.size) { channel ->
                outputs[channel].write(sampleBytes, index, bytesPerChannel)
                index += bytesPerChannel
            }
        }

        return outputs.map { it.toByteArray() }
    }


    fun calculatePeakAndRms(audioFormat: AudioFormat, input: ByteArray): PeakAndRms {

        val samples = ByteBuffer.wrap(input).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()

        var sumOfSampleSq = 0.0 // sum of square of normalized samples.
        var peakSample = 0.0    // peak sample.

        val maxNumberInBits = floor(highestNumberWithNBits(audioFormat.sampleSizeInBits) / 2)

        for (index in 0 until samples.limit()) {

            val sample = samples.get(index)
            val normSample = sample.toDouble() / maxNumberInBits
            sumOfSampleSq += normSample * normSample

            if (abs(sample.toInt()) > peakSample) {
                peakSample = abs(sample.toInt()).toDouble()
            }
        }

        val rms = 10 * log10(sumOfSampleSq / samples.limit())
        val peak = 20 * log10(peakSample / maxNumberInBits)

        return PeakAndRms(rms, peak)
    }


    private fun highestNumberWithNBits(bits: Int): Double {
        return 2.0.pow(bits) - 1
    }

    companion object {

        const val SAMPLE_SIZE = 100

    }


    data class TickData(
        val leftPeak: Double,
        val rightPeak: Double,
    )

    data class PeakAndRms(
        val rms: Double,
        val peak: Double,
    )
}