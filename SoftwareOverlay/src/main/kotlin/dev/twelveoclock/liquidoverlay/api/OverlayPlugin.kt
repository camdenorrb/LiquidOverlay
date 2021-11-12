package dev.twelveoclock.liquidoverlay.api

import dev.twelveoclock.liquidoverlay.modules.BasicModule
import dev.twelveoclock.liquidoverlay.modules.impl.OverlayModule
import kotlinx.serialization.Serializable
import tech.poder.overlay.Overlay

abstract class OverlayPlugin : BasicModule() {

    lateinit var overlay: Overlay
        internal set


    /**
     * This will automatically be called on resize,
     * otherwise manually calling after clear is necessary
     */
    abstract fun draw()


    @Serializable
    data class Config(val mainClassPath: String)

}