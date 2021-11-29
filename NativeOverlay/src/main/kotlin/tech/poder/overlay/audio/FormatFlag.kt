package tech.poder.overlay.audio

import tech.poder.overlay.general.Callback
import java.util.*


enum class FormatFlag(val baseFlag: Short, val subformatGUID: UUID) {
    PCM(1, UUID(4294967312, -9223371306706625679)),
    ;
    companion object {
        const val extendedBaseFlag: Short = -2
    }
}