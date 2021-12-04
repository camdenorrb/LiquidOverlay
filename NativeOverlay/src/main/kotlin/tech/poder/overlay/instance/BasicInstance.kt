package tech.poder.overlay.instance

import jdk.incubator.foreign.MemorySegment
import tech.poder.overlay.data.Struct
import tech.poder.overlay.instance.base.NativeInstance

data class BasicInstance(
    override val segment: MemorySegment,
    override val struct: Struct,
) : NativeInstance