package tech.poder.overlay.audio

import java.util.*

enum class FormatFlag(val baseFlag: Short, val id: UUID? = null) {
    PCM(1),
    EXTENDED_PCM(-2), //TODO, get GUID of KSDATAFORMAT_SUBTYPE_PCM
}