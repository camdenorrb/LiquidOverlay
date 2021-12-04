package tech.poder.overlay.instance.winapi

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope
import tech.poder.overlay.data.Struct
import tech.poder.overlay.instance.base.NativeInstance
import tech.poder.overlay.structs.KatDLLStructs

// TODO: Make the C++ version return an instance rather than modifying one passed in, then this can be cached better.
//       Yay immutable data structures
@JvmInline
value class FormatListInstance(override val segment: MemorySegment): NativeInstance {

    override val struct: Struct
        get() = FormatListInstance.struct


    val amount: Int
        get() = MemoryAccess.getIntAtOffset(segment, struct[0])

    fun values(scope: ResourceScope): MemorySegment {
        return MemoryAccess.getAddressAtOffset(segment, struct[1])
            .asSegment(CLinker.C_POINTER.byteSize() * amount, scope)
    }


    companion object {

        val struct: Struct
            inline get() = KatDLLStructs.formatList

    }

}