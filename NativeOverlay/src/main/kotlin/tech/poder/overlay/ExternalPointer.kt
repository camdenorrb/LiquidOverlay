package tech.poder.overlay

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope

@JvmInline
value class ExternalPointer(val pointer: MemoryAddress)
