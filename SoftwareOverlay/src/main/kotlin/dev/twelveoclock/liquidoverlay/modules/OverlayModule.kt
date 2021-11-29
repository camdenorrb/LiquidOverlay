package dev.twelveoclock.liquidoverlay.modules

import dev.twelveoclock.liquidoverlay.modules.base.BasicModule
import dev.twelveoclock.liquidoverlay.modules.sub.PluginModule
import tech.poder.overlay.video.OverlayImpl
import tech.poder.overlay.video.WindowManager
import java.nio.file.Path

class OverlayModule(
    val pluginsFolder: Path,
    val canvasWindowManager: WindowManager,
    val selectedWindowManager: WindowManager
) : BasicModule() {

    lateinit var overlay: OverlayImpl

    lateinit var pluginModule: PluginModule


    override fun onEnable() {

        overlay = OverlayImpl(canvasWindowManager, selectedWindowManager)

        overlay.onRedraw = {
            pluginModule.redraw()
        }

        pluginModule = PluginModule(pluginsFolder, overlay)
        pluginModule.enable()
    }

    override fun onDisable() {
        pluginModule.disable()
        overlay.close()
    }

}