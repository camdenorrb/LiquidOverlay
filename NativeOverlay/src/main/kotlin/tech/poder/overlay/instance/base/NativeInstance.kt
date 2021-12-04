package tech.poder.overlay.instance.base

import jdk.incubator.foreign.MemorySegment
import tech.poder.overlay.data.Struct

interface NativeInstance {

    val segment: MemorySegment

    val struct: Struct

}