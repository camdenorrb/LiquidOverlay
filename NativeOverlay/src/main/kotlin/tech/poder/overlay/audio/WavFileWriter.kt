package tech.poder.overlay.audio

import tech.poder.overlay.utils.NumberUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object WavFileWriter {
    private val riff = "RIFF".toByteArray(Charsets.US_ASCII)
    private val fmt = "fmt ".toByteArray(Charsets.US_ASCII)
    private val wav = "WAVE".toByteArray(Charsets.US_ASCII)
    private val dat = "data".toByteArray(Charsets.US_ASCII)
    private val fact = "fact".toByteArray(Charsets.US_ASCII)
    fun write(data: ByteArray, format: FormatData, output: Path) {
        val buffer = ByteBuffer.allocate(1024)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        val channelOut = Files.newByteChannel(output, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
        val bitsPerSample = format.bitsPerChannel * format.channels.size
        val bytesPerSample = bitsPerSample / 8
        buffer.put(riff)
        buffer.putInt(0) //fill in after
        buffer.put(wav)
        buffer.flip()
        channelOut.write(buffer)
        buffer.clear()
        when (format.tag) {
            FormatFlag.IEEE_FLOAT, FormatFlag.PCM -> {
                buffer.put(fmt)
                buffer.putInt(40)
                buffer.putShort(FormatFlag.extendedBaseFlag)
                buffer.putShort(format.channels.size.toShort())
                buffer.putInt(format.sampleRate)
                buffer.putInt(((format.sampleRate.toLong() * bitsPerSample) / 8L).toInt())
                buffer.putShort(bytesPerSample.toShort())
                buffer.putShort(format.bitsPerChannel)
                buffer.putShort(22)
                buffer.putShort(format.samples)
                var mask = 0
                format.channels.forEach {
                    mask = mask or it.flag
                }
                buffer.putInt(mask)
                val byteArray = ByteArray(16)
                NumberUtils.bytesFromLong(format.tag.formatGUID.mostSignificantBits, byteArray)
                NumberUtils.bytesFromLong(format.tag.formatGUID.leastSignificantBits, byteArray, 8)
                var offset = 0
                buffer.putInt(NumberUtils.intFromBytes(byteArray, offset))
                offset += Int.SIZE_BYTES
                buffer.putShort(NumberUtils.shortFromBytes(byteArray, offset))
                offset += Short.SIZE_BYTES
                buffer.putShort(NumberUtils.shortFromBytes(byteArray, offset))
                offset += Short.SIZE_BYTES
                buffer.put(byteArray[offset])
                offset++
                buffer.put(byteArray[offset])
                offset++
                buffer.put(byteArray[offset])
                offset++
                buffer.put(byteArray[offset])
                offset++
                buffer.put(byteArray[offset])
                offset++
                buffer.put(byteArray[offset])
                offset++
                buffer.put(byteArray[offset])
                offset++
                buffer.put(byteArray[offset])
                buffer.put(fact)
                buffer.putInt(4)
                buffer.putInt(data.size / bytesPerSample)
                buffer.flip()
                channelOut.write(buffer)
                buffer.clear()

            }
            FormatFlag.UNKNOWN -> error("Unsupported format: $format")
        }
        buffer.put(dat)
        buffer.putInt(data.size)
        buffer.flip()
        channelOut.write(buffer)
        buffer.clear()
        channelOut.write(ByteBuffer.wrap(data))

        val realSize = channelOut.size() - 8L
        channelOut.position(4)
        buffer.putInt(realSize.toInt())
        buffer.flip()
        channelOut.write(buffer)
    }
}