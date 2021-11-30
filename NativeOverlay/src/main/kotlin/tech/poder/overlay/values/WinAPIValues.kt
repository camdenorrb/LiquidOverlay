package tech.poder.overlay.values

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope
import tech.poder.overlay.structs.WinAPIStructs

object WinAPIValues {

    const val CLSCTX_ALL = 23


    object GUID {

        val CLSID_MMDeviceEnumerator = defineGUID(
            0xBCDE0395L,
            0xE52F.toShort(),
            0x467C,
            0x8E.toByte(),
            0x3D,
            0xC4.toByte(),
            0x57,
            0x92.toByte(),
            0x91.toByte(),
            0x69,
            0x2E
        )


        val IID_IMMDeviceEnumerator = defineGUID(
            0xa95664d2,
            0x9614.toShort(),
            0x4f35,
            0xa7.toByte(),
            0x46,
            0xde.toByte(),
            0x8d.toByte(),
            0xb6.toByte(),
            0x36,
            0x17,
            0xe6.toByte()
        )

        val IID_IAudioClient = defineGUID(
            0x1cb9ad4c,
            0xdbfa.toShort(),
            0x4c32,
            0xb1.toByte(),
            0x78,
            0xc2.toByte(),
            0xf5.toByte(),
            0x68,
            0xa7.toByte(),
            0x03,
            0xb2.toByte()
        )

        val IID_IAudioCaptureClient = defineGUID(
            0xc8adbd64,
            0xe71e.toShort(),
            0x48a0,
            0xa4.toByte(),
            0xde.toByte(),
            0x18,
            0x5c,
            0x39,
            0x5c,
            0xd3.toByte(),
            0x17
        )

        val PKEY_Device_FriendlyName = definePropertyId(
            0xa45c254e,
            0xdf1c.toShort(),
            0x4efd,
            0x80.toByte(),
            0x20,
            0x67,
            0xd1.toByte(),
            0x46,
            0xa8.toByte(),
            0x50,
            0xe0.toByte(),
            14
        )

        fun defineGUID(
            a: Long, b: Short, c: Short, d: Byte, e: Byte, f: Byte, g: Byte, h: Byte, i: Byte, j: Byte, k: Byte
        ): MemorySegment {

            ResourceScope.newSharedScope().use { confinedStatic ->

                val data = MemorySegment.allocateNative(WinAPIStructs.guid.size, confinedStatic)

                MemoryAccess.setLongAtOffset(data, WinAPIStructs.guid[0], a)
                MemoryAccess.setShortAtOffset(data, WinAPIStructs.guid[1], b)
                MemoryAccess.setShortAtOffset(data, WinAPIStructs.guid[2], c)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.guid[3], d)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.guid[4], e)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.guid[5], f)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.guid[6], g)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.guid[7], h)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.guid[8], i)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.guid[9], j)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.guid[10], k)

                return data
            }

        }

        fun definePropertyId(
            a: Long, b: Short, c: Short, d: Byte, e: Byte, f: Byte, g: Byte, h: Byte, i: Byte, j: Byte, k: Byte, l: Int
        ): MemorySegment {

            ResourceScope.newSharedScope().use { confinedStatic ->

                val data = MemorySegment.allocateNative(WinAPIStructs.propertyKey.size, confinedStatic)

                MemoryAccess.setLongAtOffset(data, WinAPIStructs.propertyKey[0], a)
                MemoryAccess.setShortAtOffset(data, WinAPIStructs.propertyKey[1], b)
                MemoryAccess.setShortAtOffset(data, WinAPIStructs.propertyKey[2], c)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.propertyKey[3], d)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.propertyKey[4], e)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.propertyKey[5], f)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.propertyKey[6], g)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.propertyKey[7], h)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.propertyKey[8], i)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.propertyKey[9], j)
                MemoryAccess.setByteAtOffset(data, WinAPIStructs.propertyKey[10], k)
                MemoryAccess.setIntAtOffset(data, WinAPIStructs.propertyKey[11], l)

                return data
            }

        }

    }

}