package dev.twelveoclock.liquidoverlay.modules.base

abstract class BasicModule {

    var isEnabled = false
        private set


    open fun onEnable() = Unit

    open fun onDisable() = Unit


    fun enable() {
        if (isEnabled) {
            onEnable()
            isEnabled = true
        }
    }

    fun disable() {
        if (!isEnabled) {
            onDisable()
            isEnabled = false
        }
    }

}