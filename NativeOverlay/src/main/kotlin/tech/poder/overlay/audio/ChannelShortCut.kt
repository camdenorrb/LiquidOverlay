package tech.poder.overlay.audio

enum class ChannelShortCut(val flags: Int, val channels: List<Channel> = emptyList()) {
    KSAUDIO_SPEAKER_DIRECTOUT(0),
    KSAUDIO_SPEAKER_MONO(Channel.FRONT_CENTER.flag, listOf(Channel.FRONT_CENTER)),
    KSAUDIO_SPEAKER_STEREO(
        Channel.FRONT_LEFT.flag or Channel.FRONT_RIGHT.flag,
        listOf(Channel.FRONT_LEFT, Channel.FRONT_RIGHT)
    ),
    KSAUDIO_SPEAKER_QUAD(
        Channel.FRONT_LEFT.flag or Channel.FRONT_RIGHT.flag or Channel.BACK_LEFT.flag or Channel.BACK_RIGHT.flag,
        listOf(Channel.FRONT_LEFT, Channel.FRONT_RIGHT, Channel.BACK_LEFT, Channel.BACK_RIGHT)
    ),
    KSAUDIO_SPEAKER_SURROUND(
        Channel.FRONT_LEFT.flag or Channel.FRONT_RIGHT.flag or Channel.FRONT_CENTER.flag or Channel.BACK_CENTER.flag,
        listOf(Channel.FRONT_LEFT, Channel.FRONT_RIGHT, Channel.FRONT_CENTER, Channel.BACK_CENTER)
    ),
    KSAUDIO_SPEAKER_5POINT1(
        Channel.FRONT_LEFT.flag or Channel.FRONT_RIGHT.flag or Channel.FRONT_CENTER.flag or Channel.LOW_FREQUENCY.flag or Channel.BACK_LEFT.flag or Channel.BACK_RIGHT.flag,
        listOf(
            Channel.FRONT_LEFT,
            Channel.FRONT_RIGHT,
            Channel.FRONT_CENTER,
            Channel.LOW_FREQUENCY,
            Channel.BACK_LEFT,
            Channel.BACK_RIGHT
        )
    ),
    KSAUDIO_SPEAKER_5POINT1_SURROUND(
        Channel.FRONT_LEFT.flag or Channel.FRONT_RIGHT.flag or Channel.FRONT_CENTER.flag or Channel.LOW_FREQUENCY.flag or Channel.BACK_LEFT.flag or Channel.BACK_RIGHT.flag or Channel.SIDE_LEFT.flag or Channel.SIDE_RIGHT.flag,
        listOf(
            Channel.FRONT_LEFT,
            Channel.FRONT_RIGHT,
            Channel.FRONT_CENTER,
            Channel.LOW_FREQUENCY,
            Channel.BACK_LEFT,
            Channel.BACK_RIGHT,
            Channel.SIDE_LEFT,
            Channel.SIDE_RIGHT
        )
    ),
    KSAUDIO_SPEAKER_7POINT1(
        Channel.FRONT_LEFT.flag or Channel.FRONT_RIGHT.flag or Channel.FRONT_CENTER.flag or Channel.LOW_FREQUENCY.flag or Channel.BACK_LEFT.flag or Channel.BACK_RIGHT.flag or Channel.FRONT_LEFT_OF_CENTER.flag or Channel.FRONT_RIGHT_OF_CENTER.flag,
        listOf(
            Channel.FRONT_LEFT,
            Channel.FRONT_RIGHT,
            Channel.FRONT_CENTER,
            Channel.LOW_FREQUENCY,
            Channel.BACK_LEFT,
            Channel.BACK_RIGHT,
            Channel.FRONT_LEFT_OF_CENTER,
            Channel.FRONT_RIGHT_OF_CENTER
        )
    ),
    KSAUDIO_SPEAKER_7POINT1_SURROUND(
        Channel.FRONT_LEFT.flag or Channel.FRONT_RIGHT.flag or Channel.FRONT_CENTER.flag or Channel.LOW_FREQUENCY.flag or Channel.BACK_LEFT.flag or Channel.BACK_RIGHT.flag or Channel.SIDE_LEFT.flag or Channel.SIDE_RIGHT.flag or Channel.FRONT_LEFT_OF_CENTER.flag or Channel.FRONT_RIGHT_OF_CENTER.flag,
        listOf(
            Channel.FRONT_LEFT,
            Channel.FRONT_RIGHT,
            Channel.FRONT_CENTER,
            Channel.LOW_FREQUENCY,
            Channel.BACK_LEFT,
            Channel.BACK_RIGHT,
            Channel.SIDE_LEFT,
            Channel.SIDE_RIGHT,
            Channel.FRONT_LEFT_OF_CENTER,
            Channel.FRONT_RIGHT_OF_CENTER
        )
    ),
}