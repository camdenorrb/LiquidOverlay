package tech.poder.overlay.audio

import java.util.*


enum class FormatFlag(val baseFlag: Short, val formatGUID: UUID) {
    PCM(1, UUID(4294967312, -9223371306706625679)),
    IEEE_FLOAT(3, UUID(12884901904, -9223371306706625679));

    companion object {
        const val extendedBaseFlag: Short = -2
    }
}