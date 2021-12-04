package tech.poder.overlay.instance.katdll

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope
import tech.poder.overlay.data.Struct
import tech.poder.overlay.instance.BasicInstance
import tech.poder.overlay.instance.base.NativeInstance
import tech.poder.overlay.structs.KatDLLStructs
import tech.poder.overlay.structs.WinAPIStructs

// TODO: Make the C++ version return an instance rather than modifying one passed in, then this can be cached better.
//       Yay immutable data structures
@JvmInline
value class StateInstance(override val segment: MemorySegment) : NativeInstance {

    override val struct: Struct
        get() = StateInstance.struct


    val hresult: Int
        get() = MemoryAccess.getIntAtOffset(
            segment,
            KatDLLStructs.state[0]
        )

    val pNumFramesInPacket: UInt
        get() = MemoryAccess.getIntAtOffset(segment, struct[1]).toUInt()

    val pFlags: Byte
        get() = MemoryAccess.getByteAtOffset(segment, struct[2])

    val message: String
        get() = CLinker.toJavaString(
            MemoryAccess.getAddressAtOffset(
                segment,
                KatDLLStructs.state[4]
            )
        )


    fun getPData(scope: ResourceScope, size: Long): MemorySegment {
        return MemoryAccess.getAddressAtOffset(segment, struct[9]).asSegment(size, scope)
    }

    fun getFormat(scope: ResourceScope): BasicInstance {
        return BasicInstance(
            MemoryAccess.getAddressAtOffset(segment, struct[8]).asSegment(
                WinAPIStructs.waveFormatEx.size,
                scope
            ),
            WinAPIStructs.waveFormatEx
        )
    }


    companion object {

        val struct: Struct
            inline get() = KatDLLStructs.state

    }

}