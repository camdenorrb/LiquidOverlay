package dev.twelveoclock.liquidoverlay.modules.sub

import dev.twelveoclock.liquidoverlay.api.OverlayPlugin
import dev.twelveoclock.liquidoverlay.modules.base.BasicModule
import kotlinx.serialization.json.Json
import tech.poder.overlay.Overlay
import java.net.URLClassLoader
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.reflect.KClass

class PluginModule(val pluginsFolder: Path, val overlay: Overlay) : BasicModule() {

    val loadedPlugins = mutableListOf<OverlayPlugin>()


    override fun onEnable() {

        if (!pluginsFolder.exists()) {
            pluginsFolder.createDirectories()
        }

        val mainClassLoader = this::class.java.classLoader

        pluginsFolder.listDirectoryEntries("*.jar").mapTo(loadedPlugins) { pluginPath ->

            val pluginClassLoader = URLClassLoader(arrayOf(pluginPath.toUri().toURL()), mainClassLoader)
            val config = Json.decodeFromString(OverlayPlugin.Config.serializer(), pluginClassLoader.getResource("plugin.json")!!.readText())

            (pluginClassLoader.loadClass(config.mainClassPath).kotlin as KClass<OverlayPlugin>).objectInstance!!.also {
                it.overlay = overlay
                it.enable()
                it.draw()
            }
        }

    }

    override fun onDisable() {
        loadedPlugins.forEach { it.disable() }
        loadedPlugins.clear()
    }


    internal fun redraw() {
        loadedPlugins.forEach {
            it.draw()
        }
    }

}