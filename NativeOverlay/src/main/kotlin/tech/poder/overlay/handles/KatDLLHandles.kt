package tech.poder.overlay.handles

import tech.poder.overlay.utils.NativeUtils
import kotlin.io.path.Path

object KatDLLHandles {

    init {
        NativeUtils.loadLibrary(Path("libnew.dll"))
    }


}