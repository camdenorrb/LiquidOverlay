package tech.poder.overlay

import jdk.incubator.foreign.MemoryAddress

@JvmInline
value class WindowClass(val clazzPointer: MemoryAddress)
