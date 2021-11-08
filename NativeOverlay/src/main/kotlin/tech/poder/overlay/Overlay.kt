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

        private val unhookWindowsHookEx = NativeRegistry.register(
            FunctionDescription( //17
                "UnhookWindowsHookEx", Boolean::class.java, listOf(MemoryAddress::class.java)
            )
        )

        private val getCurrentThreadId = NativeRegistry.register(
            FunctionDescription( //18
                "GetCurrentThreadId", Int::class.java, listOf()
            )
        )

        private val setWindowsHookExA = NativeRegistry.register(
            FunctionDescription( //17
                "SetWindowsHookExA", MemoryAddress::class.java, listOf(
                    Int::class.java, MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java
                )
            )
        )

        private val createThread = NativeRegistry.register(
            FunctionDescription( //19
                "CreateThread", MemoryAddress::class.java, listOf(
                    MemoryAddress::class.java,
                    Int::class.java,
                    MemoryAddress::class.java,
                    MemoryAddress::class.java,
                    Int::class.java,
                    MemoryAddress::class.java
                )
            )
        )

        private const val WH_CALLWNDPROC = 4 //before process receives messages
        private const val WH_CALLWNDPROCRET = 12 //after process receives messages
        private const val WH_CBT = 5
        private const val WH_DEBUG = 9
        private const val WH_FOREGROUNDIDLE = 11
        private const val WH_GETMESSAGE = 3
        private const val WH_JOURNALPLAYBACK = 1
        private const val WH_JOURNALRECORD = 0
        private const val WH_KEYBOARD = 2
        private const val WH_KEYBOARD_LL = 13
        private const val WH_MOUSE = 7
        private const val WH_MOUSE_LL = 14
        private const val WH_MSGFILTER = -1
        private const val WH_SHELL = 10
        private const val WH_SYSMSGFILTER = 6

        val hookProcUpcall = NativeRegistry.registerUpcallStatic(
            FunctionDescription(
                "hookProc",
                MemoryAddress::class.java,
                listOf(Int::class.java, MemoryAddress::class.java, MemoryAddress::class.java)
            ), Callback::class.java
        )
    }

    var hook = MemoryAddress.NULL

    val callbackThread = Thread {
        val id = NativeRegistry.registry[getCurrentThreadId].invoke()
        hook = NativeRegistry.registry[setWindowsHookExA].invoke(
            WH_CALLWNDPROCRET, hookProcUpcall, MemoryAddress.NULL, id
        ) as MemoryAddress
        check(hook != MemoryAddress.NULL) { "Failed to set hook: ${NativeRegistry.registry[Callback.getLastError].invoke()}" }
        while (!Thread.currentThread().isInterrupted) {
            Thread.sleep(1)
        }
    }

    init {
        callbackThread.start()
        while (callbackThread.isAlive && hook == MemoryAddress.NULL) {
            Thread.sleep(1)
        }
        check (callbackThread.isAlive) {
            "Failed to start callback thread"
        }
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
        if (hook != MemoryAddress.NULL) {
            val result = NativeRegistry.registry[unhookWindowsHookEx].invoke(hook) as Int
            check(result != 0) {
                "Failed to unhook from process"
            }
            hook = MemoryAddress.NULL
        }
        callbackThread.interrupt()
        scope.close()
    }
}