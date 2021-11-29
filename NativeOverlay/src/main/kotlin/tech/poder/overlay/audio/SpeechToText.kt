package tech.poder.overlay.audio

object SpeechToText : AudioFormat {
    private val format by lazy {
        AudioFormat.generateFormat(FormatFlag.IEEE_FLOAT, ChannelShortCut.KSAUDIO_SPEAKER_STEREO.channels, 48000, 32)
    }

    override fun getAudioStruct(): GeneratedFormat {
        return format
    }
}
