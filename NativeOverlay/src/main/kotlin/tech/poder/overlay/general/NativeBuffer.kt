package tech.poder.overlay.general

data class NativeBuffer(
    val pData: Byte,
    val flags: Int,
    val numFramesAvailable: UInt
)
