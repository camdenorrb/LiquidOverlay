package tech.poder.overlay

import jdk.incubator.foreign.MemoryAddress

@JvmInline
value class ExternalPointer(val pointer: MemoryAddress)
