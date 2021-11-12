package dev.twelveoclock.liquidoverlay.modules.impl

import dev.twelveoclock.liquidoverlay.modules.BasicModule
import dev.twelveoclock.liquidoverlay.modules.impl.sub.PluginModule
import java.nio.file.Path

class OverlayModule(pluginsFolder: Path) : BasicModule() {

    val pluginModule = PluginModule(this, pluginsFolder)


    override fun onEnable() {
        pluginModule.enable()
    }

    override fun onDisable() {
        pluginModule.disable()
    }

}