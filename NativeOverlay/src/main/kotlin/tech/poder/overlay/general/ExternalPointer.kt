package tech.poder.overlay.general

import jdk.incubator.foreign.MemoryAddress

@JvmInline
value class ExternalPointer(val pointer: MemoryAddress)
