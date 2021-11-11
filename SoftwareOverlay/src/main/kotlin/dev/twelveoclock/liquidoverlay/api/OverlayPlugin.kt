package dev.twelveoclock.liquidoverlay.api

import dev.twelveoclock.liquidoverlay.modules.BasicModule
import dev.twelveoclock.liquidoverlay.modules.impl.OverlayModule
import kotlinx.serialization.Serializable

abstract class OverlayPlugin : BasicModule() {

    // Set by the plugin loader
    lateinit var overlayModule: OverlayModule
        internal set


    @Serializable
    data class Config(val mainClassPath: String)

}