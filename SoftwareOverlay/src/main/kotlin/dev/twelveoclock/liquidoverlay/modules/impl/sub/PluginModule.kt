package dev.twelveoclock.liquidoverlay.modules.impl.sub

import dev.twelveoclock.liquidoverlay.api.OverlayPlugin
import dev.twelveoclock.liquidoverlay.modules.BasicModule
import dev.twelveoclock.liquidoverlay.modules.impl.OverlayModule
import kotlinx.serialization.json.Json
import java.net.URLClassLoader
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.reflect.KClass

class PluginModule(val overlayModule: OverlayModule, val pluginsFolder: Path) : BasicModule() {

    val loadedPlugins = mutableListOf<OverlayPlugin>()

    override fun onEnable() {

        val mainClassLoader = this::class.java.classLoader

        pluginsFolder.listDirectoryEntries("*.jar").mapTo(loadedPlugins) { pluginPath ->

            val pluginClassLoader = URLClassLoader(arrayOf(pluginPath.toUri().toURL()), mainClassLoader)
            val config = Json.decodeFromString(OverlayPlugin.Config.serializer(), pluginClassLoader.getResource("plugin.json")!!.readText())

            (pluginClassLoader.loadClass(config.mainClassPath).kotlin as KClass<OverlayPlugin>).objectInstance!!.also {
                //it.overlayModule = overlayModule
                it.enable()
            }
        }

    }

    override fun onDisable() {
        loadedPlugins.forEach { it.disable() }
        loadedPlugins.clear()
    }

}