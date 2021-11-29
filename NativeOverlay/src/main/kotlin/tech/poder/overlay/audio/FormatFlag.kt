package tech.poder.overlay.audio

import java.util.*


enum class FormatFlag(val baseFlag: Short, val subformatGUID: UUID) {
    PCM(1, TODO()), //TODO, get GUID of KSDATAFORMAT_SUBTYPE_PCM
    ;
    companion object {
        const val extendedBaseFlag: Short = -2
    }
}