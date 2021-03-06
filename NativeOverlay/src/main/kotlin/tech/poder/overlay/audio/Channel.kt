package tech.poder.overlay.audio

enum class Channel(val flag: Int) {
    FRONT_LEFT(0x1),
    FRONT_RIGHT(0x2),
    FRONT_CENTER(0x4),
    LOW_FREQUENCY(0x8),
    BACK_LEFT(0x10),
    BACK_RIGHT(0x20),
    FRONT_LEFT_OF_CENTER(0x40),
    FRONT_RIGHT_OF_CENTER(0x80),
    BACK_CENTER(0x100),
    SIDE_LEFT(0x200),
    SIDE_RIGHT(0x400),
    TOP_CENTER(0x800),
    TOP_FRONT_LEFT(0x1000),
    TOP_FRONT_CENTER(0x2000),
    TOP_FRONT_RIGHT(0x4000),
    TOP_BACK_LEFT(0x8000),
    TOP_BACK_CENTER(0x10000),
    TOP_BACK_RIGHT(0x20000),
}