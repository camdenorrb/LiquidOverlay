package tech.poder.overlay

import jdk.incubator.foreign.*

class Overlay(val process: Process) : AutoCloseable {
    companion object {
        init {
            NativeRegistry.loadLib("gdi32")
        }
        private val beginPaint = NativeRegistry.register(
            FunctionDescription( //13
                "BeginPaint", MemoryAddress::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            )
        )

        private val endPaint = NativeRegistry.register(
            FunctionDescription( //14
                "EndPaint", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            )
        )

        private val textOutA = NativeRegistry.register(
            FunctionDescription( //15
                "TextOutA", Boolean::class.java, listOf(
                    MemoryAddress::class.java,
                    Int::class.java,
                    Int::class.java,
                    MemoryAddress::class.java,
                    Int::class.java
                )
            )
        )

        private val updateWindow = NativeRegistry.register(
            FunctionDescription( //16
                "UpdateWindow", Boolean::class.java, listOf(MemoryAddress::class.java)
            )
        )
    }

    val scope = ResourceScope.newConfinedScope()
    var dc = MemoryAddress.NULL

    val paintStruct = MemorySegment.allocateNative(
        CLinker.C_POINTER.byteSize() + (CLinker.C_INT.byteSize() * 7) + (CLinker.C_CHAR.byteSize() * 32), scope
    )

    val stringStorage = MemorySegment.allocateNative(CLinker.C_CHAR.byteSize() * 128, scope)

    private fun zeroOut() {
        paintStruct.fill(0)
    }

    fun startPaint() {
        check(dc == MemoryAddress.NULL) {
            "Already started painting"
        }
        zeroOut()
        dc = NativeRegistry.registry[beginPaint].invoke(process.hWnd, paintStruct.address()) as MemoryAddress
        check(dc != MemoryAddress.NULL) {
            "Failed to get DC"
        }
    }

    fun drawText(text: String, x: Int, y: Int) {
        check(dc != MemoryAddress.NULL) {
            "Not painting"
        }
        text.forEachIndexed { index, c ->
            MemoryAccess.setCharAtIndex(stringStorage, index.toLong(), c)
        }
        val result = NativeRegistry.registry[textOutA].invoke(dc, x, y, stringStorage.address(), text.length)
        check(result != 0) {
            "Failed to draw text"
        }
    }

    fun endPaint() {
        check(dc != MemoryAddress.NULL) {
            "Not painting"
        }
        NativeRegistry.registry[endPaint].invoke(process.hWnd, dc)
        dc = MemoryAddress.NULL
        val result = NativeRegistry.registry[updateWindow].invoke(process.hWnd)
        check(result != 0) {
            "Failed to update window!"
        }
    }

    override fun close() {
        scope.close()
    }
}