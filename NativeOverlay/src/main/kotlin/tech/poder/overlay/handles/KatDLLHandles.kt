package tech.poder.overlay.handles

import jdk.incubator.foreign.MemoryAddress
import tech.poder.overlay.utils.NativeUtils
import kotlin.io.path.Path

object KatDLLHandles {

    init {
        NativeUtils.loadLibrary(Path("KatLib.dll"))
    }


    val startRecording = NativeUtils.lookupMethodHandle(
        "StartRecording",
        parameterTypes = listOf(MemoryAddress::class.java, MemoryAddress::class.java)
    )

    val stopRecording = NativeUtils.lookupMethodHandle(
        "StopRecording",
        parameterTypes = listOf(MemoryAddress::class.java)
    )

    val getNextPacketSize = NativeUtils.lookupMethodHandle(
        "GetNextPacketSize",
        parameterTypes = listOf(MemoryAddress::class.java)
    )

    val getBuffer = NativeUtils.lookupMethodHandle(
        "GetBuffer",
        parameterTypes = listOf(MemoryAddress::class.java)
    )

    val releaseBuffer = NativeUtils.lookupMethodHandle(
        "ReleaseBuffer",
        parameterTypes = listOf(MemoryAddress::class.java)
    )

    val getPCMID = NativeUtils.lookupMethodHandle(
        "GetPCMID",
        MemoryAddress::class.java
    )

}