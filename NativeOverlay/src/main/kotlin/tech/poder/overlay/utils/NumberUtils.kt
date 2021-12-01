package tech.poder.overlay.utils


object NumberUtils {

    private val shortUtils = listOf(8, 0)
    private val intUtils = listOf(24, 16, 8, 0)
    private val intUtilsBE = intUtils.reversed()
    private val longUtils = listOf(56, 48, 40, 32, 24, 16, 8, 0)

    fun shortFromBytes(bytes: ByteArray, offset: Int = 0): Short {
        var value = 0
        shortUtils.forEachIndexed { index, it ->
            value = value or (bytes[index + offset].toInt() and 0xFF shl it)
        }
        return value.toShort()
    }

    fun intFromBytes(bytes: ByteArray, offset: Int = 0): Int {
        var value = 0
        intUtils.forEachIndexed { index, it ->
            value = value or (bytes[index + offset].toInt() and 0xFF shl it)
        }
        return value
    }

    fun intFromBytesReversed(bytes: ByteArray, offset: Int = 0): Int {
        var value = 0
        intUtilsBE.forEachIndexed { index, it ->
            value = value or (bytes[index + offset].toInt() and 0xFF shl it)
        }
        return value
    }

    fun floatFromBytes(bytes: ByteArray, offset: Int = 0): Float {
        return Float.fromBits(intFromBytes(bytes, offset))
    }

    fun floatFromBytesBE(bytes: ByteArray, offset: Int = 0): Float {
        return Float.fromBits(intFromBytesReversed(bytes, offset))
    }

    fun longFromBytes(bytes: ByteArray, offset: Int = 0): Long {
        var value = 0L
        longUtils.forEachIndexed { index, it ->
            value = value or (bytes[index + offset].toLong() and 0xFFL shl it)
        }
        return value
    }

    fun bytesFromShort(short: Short, array: ByteArray = ByteArray(shortUtils.size), offset: Int = 0): ByteArray {
        repeat(shortUtils.size) {
            array[it + offset] = (short.toInt() shr shortUtils[it]).toByte()
        }
        return array
    }

    fun bytesFromInt(int: Int, array: ByteArray = ByteArray(intUtils.size), offset: Int = 0): ByteArray {
        repeat(intUtils.size) {
            array[it + offset] = (int shr intUtils[it]).toByte()
        }
        return array
    }

    fun bytesFromIntReversed(int: Int, array: ByteArray = ByteArray(intUtils.size), offset: Int = 0): ByteArray {
        repeat(intUtils.size) {
            array[it + offset] = (int shr intUtilsBE[it]).toByte()
        }
        return array
    }

    fun bytesFromFloat(float: Float, array: ByteArray = ByteArray(intUtils.size), offset: Int = 0): ByteArray {
        return bytesFromInt(float.toBits(), array, offset)
    }

    fun bytesFromFloatBE(float: Float, array: ByteArray = ByteArray(intUtils.size), offset: Int = 0): ByteArray {
        return bytesFromIntReversed(float.toBits(), array, offset)
    }

    fun bytesFromLong(long: Long, array: ByteArray = ByteArray(longUtils.size), offset: Int = 0): ByteArray {
        repeat(longUtils.size) {
            array[it + offset] = (long shr longUtils[it]).toByte()
        }
        return array
    }
}