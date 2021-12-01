package tech.poder.overlay.audio

object SpeechToText : AudioFormat {
    private val format by lazy {
        AudioFormat.generateFormat(FormatFlag.PCM, ChannelShortCut.KSAUDIO_SPEAKER_STEREO.channels, 16000, 16)
    }

    override fun getAudioStruct(): GeneratedFormat {
        return format
    }
}
