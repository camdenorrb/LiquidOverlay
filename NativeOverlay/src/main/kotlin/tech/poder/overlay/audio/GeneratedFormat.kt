package tech.poder.overlay.audio

import tech.poder.overlay.data.StructInstance

@JvmInline
value class GeneratedFormat(val format: StructInstance) : AudioFormat {
    override fun getAudioStruct(): GeneratedFormat {
        return this
    }
}
