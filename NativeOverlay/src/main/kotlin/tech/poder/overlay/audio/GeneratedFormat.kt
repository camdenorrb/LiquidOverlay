package tech.poder.overlay.audio

import tech.poder.overlay.general.StructInstance

@JvmInline
value class GeneratedFormat(val format: StructInstance): AudioFormat {
    override fun getAudioStruct(): GeneratedFormat {
        return this
    }
}
