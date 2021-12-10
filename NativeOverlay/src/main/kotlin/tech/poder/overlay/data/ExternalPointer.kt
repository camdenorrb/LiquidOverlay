package tech.poder.overlay.data

import jdk.incubator.foreign.MemoryAddress

@JvmInline
value class ExternalPointer(val pointer: MemoryAddress)
