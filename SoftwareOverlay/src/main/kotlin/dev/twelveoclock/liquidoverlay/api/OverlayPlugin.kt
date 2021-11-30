package dev.twelveoclock.liquidoverlay.api

import dev.twelveoclock.liquidoverlay.Main
import dev.twelveoclock.liquidoverlay.modules.base.BasicModule
import kotlinx.serialization.Serializable
import tech.poder.overlay.overlay.base.Overlay

abstract class OverlayPlugin : BasicModule() {

    lateinit var main: Main
        internal set

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