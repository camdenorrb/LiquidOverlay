package dev.twelveoclock.liquidoverlay.modules.impl

import dev.twelveoclock.liquidoverlay.modules.BasicModule
import dev.twelveoclock.liquidoverlay.modules.impl.sub.PluginModule
import tech.poder.overlay.Overlay
import tech.poder.overlay.OverlayImpl
import tech.poder.overlay.WindowManager
import java.nio.file.Path

class OverlayModule(
    val pluginsFolder: Path,
    val canvasWindowManager: WindowManager,
    val selectedWindowManager: WindowManager
) : BasicModule() {

    lateinit var overlay: Overlay

    lateinit var pluginModule: PluginModule


    override fun onEnable() {

        overlay = OverlayImpl(canvasWindowManager, selectedWindowManager) {
            pluginModule.onResize()
        }

        pluginModule = PluginModule(pluginsFolder, overlay)
        pluginModule.enable()
    }

    override fun onDisable() {
        pluginModule.disable()
        overlay.close()
    }

}