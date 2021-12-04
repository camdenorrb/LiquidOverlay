package tech.poder.overlay.audio

import jdk.incubator.foreign.ResourceScope
import tech.poder.overlay.audio.base.AudioFormat

object SpeechToText : AudioFormat {

    private val format = AudioFormat.generateFormat(
        FormatFlag.PCM,
        ResourceScope.globalScope(),
        ChannelShortCut.KSAUDIO_SPEAKER_STEREO.channels,
        16000,
        16
    )


    override fun getAudioStruct(): GeneratedFormat {
        return format
    }

}
