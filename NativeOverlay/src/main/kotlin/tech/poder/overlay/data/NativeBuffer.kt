package tech.poder.overlay.data

data class NativeBuffer(
    val pData: Byte,
    val flags: Int,
    val numFramesAvailable: UInt,
)
