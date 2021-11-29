package tech.poder.overlay.audio

import jdk.incubator.foreign.MemoryAccess
import tech.poder.overlay.general.Callback

object SpeechToText : AudioFormat {
    private val format by lazy {
        val format = Callback.newFormat()
        MemoryAccess.setShortAtOffset(format.segment, format[0], 1) //WAVE_FORMAT_PCM
        MemoryAccess.setShortAtOffset(format.segment, format[1], 1) //Mono sound
        MemoryAccess.setIntAtOffset(format.segment, format[2], 16000) //16kHz
        MemoryAccess.setShortAtOffset(format.segment, format[3], 32000) //Average bytes per second = 16000 * ((16*1) / 8) = 32000
        MemoryAccess.setShortAtOffset(format.segment, format[4], 8) //Block Alignment = (16*1) / 8) = 8
        MemoryAccess.setShortAtOffset(format.segment, format[5], 16) //16bit
        MemoryAccess.setShortAtOffset(format.segment, format[6], 0) //No Extra Data
        GeneratedFormat(format)
    }

    override fun getAudioStruct(): GeneratedFormat {
        return format
    }
}
