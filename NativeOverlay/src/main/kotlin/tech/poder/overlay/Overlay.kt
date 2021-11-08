package tech.poder.overlay

import jdk.incubator.foreign.*

class Overlay(val process: Process) : AutoCloseable {
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
        dc = Callback.methods[13].invoke(process.hWnd, paintStruct.address()) as MemoryAddress
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
        val result = Callback.methods[15].invoke(dc, x, y, stringStorage.address(), text.length)
        check(result != 0) {
            "Failed to draw text"
        }
    }

    fun endPaint() {
        check(dc != MemoryAddress.NULL) {
            "Not painting"
        }
        Callback.methods[14].invoke(process.hWnd, dc)
        dc = MemoryAddress.NULL
        val result = Callback.methods[16].invoke(process.hWnd)
        check(result != 0) {
            "Failed to update window!"
        }
    }

    override fun close() {
        scope.close()
    }
}