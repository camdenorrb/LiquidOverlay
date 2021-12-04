package tech.poder.overlay.audio

import tech.poder.overlay.audio.base.AudioFormat
import tech.poder.overlay.instance.BasicInstance

@JvmInline
value class GeneratedFormat(val format: BasicInstance) : AudioFormat {
    override fun getAudioStruct(): GeneratedFormat {
        return this
    }
}
