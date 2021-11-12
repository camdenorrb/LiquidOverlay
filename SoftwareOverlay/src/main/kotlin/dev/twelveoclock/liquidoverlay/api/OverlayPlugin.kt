package dev.twelveoclock.liquidoverlay.api

import dev.twelveoclock.liquidoverlay.modules.BasicModule
import dev.twelveoclock.liquidoverlay.modules.impl.OverlayModule
import kotlinx.serialization.Serializable
import tech.poder.overlay.Overlay

abstract class OverlayPlugin : BasicModule() {

    val overlays = mutableListOf<Overlay>()


    @Serializable
    data class Config(val mainClassPath: String)

}