package tech.poder.overlay

import jdk.incubator.foreign.*

class Overlay(val process: Process) : AutoCloseable {
    companion object {
        init {
            NativeRegistry.loadLib("gdi32")
            NativeRegistry.loadLib("oleacc")
        }

        private val beginPaint = NativeRegistry.register(
            FunctionDescription(
                "BeginPaint", MemoryAddress::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            )
        )

        private val endPaint = NativeRegistry.register(
            FunctionDescription(
                "EndPaint", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            )
        )

        private val textOutA = NativeRegistry.register(
            FunctionDescription(
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
            FunctionDescription(
                "UpdateWindow", Boolean::class.java, listOf(MemoryAddress::class.java)
            )
        )

        private val unhookWindowsHookEx = NativeRegistry.register(
            FunctionDescription(
                "UnhookWindowsHookEx", Boolean::class.java, listOf(MemoryAddress::class.java)
            )
        )

        private val getWindowThreadProcessId = NativeRegistry.register(
            FunctionDescription(
                "GetWindowThreadProcessId",
                Int::class.java,
                listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            )
        )

        private val getCurrentThreadId = NativeRegistry.register(
            FunctionDescription(
                "GetCurrentThreadId", Int::class.java
            )
        )

        private val setWindowsHookExA = NativeRegistry.register(
            FunctionDescription(
                "SetWindowsHookExA", MemoryAddress::class.java, listOf(
                    Int::class.java, MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java
                )
            )
        )

        private val createThread = NativeRegistry.register(
            FunctionDescription(
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

        private val getModuleBaseName = NativeRegistry.register(
            FunctionDescription(
                "GetModuleBaseNameA", Int::class.java, listOf(
                    MemoryAddress::class.java, MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java
                )
            )
        )

        private val getProcessHandleFromHwnd = NativeRegistry.register(
            FunctionDescription(
                "GetProcessHandleFromHwnd", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
            )
        )

        val getModuleHandle = NativeRegistry.register(
            FunctionDescription(
                "GetModuleHandleA", MemoryAddress::class.java, listOf(MemoryAddress::class.java)
            )
        )

        val getThreadId = NativeRegistry.register(
            FunctionDescription(
                "GetThreadId", Int::class.java, listOf(MemoryAddress::class.java)
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

        val dllCheckUpcall = NativeRegistry.registerUpcallStatic(
            FunctionDescription("dllCheck"), Callback::class.java
        )
    }

    var hook = MemoryAddress.NULL

    init {
        val self = NativeRegistry[getModuleHandle].invoke(MemoryAddress.NULL)
        /*val overlayWindow = WindowManager.createWindow(
            WS_EX_TOPMOST or WS_EX_TRANSPARENT or WS_EX_LAYERED,
            style = WS_POPUP.toInt(),
            width = 1024,
            height = 1024,
            hInstance = self,
        )
        check(overlayWindow.window != MemoryAddress.NULL) { "Failed to create overlay window: ${NativeRegistry[Callback.getLastError].invoke()}" }*/

        //val basic = NativeRegistry.register(dllCheckUpcall, FunctionDescription("dllCheck"))
        //NativeRegistry[basic].invoke()
        //println(NativeRegistry[getModuleHandle].invoke(MemoryAddress.NULL))
        //val pid2 = NativeRegistry[getProcessHandleFromHwnd].invoke(process.hWnd)
        //println(pid2)
        val id = NativeRegistry[getWindowThreadProcessId].invoke(process.hWnd, MemoryAddress.NULL)
        //println(id)
        hook = NativeRegistry[setWindowsHookExA].invoke(
            WH_CALLWNDPROC, hookProcUpcall, self, id
        ) as MemoryAddress
        check(hook != MemoryAddress.NULL) { "Failed to set hook: ${NativeRegistry[Callback.getLastError].invoke()}" }
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
        dc = NativeRegistry[beginPaint].invoke(process.hWnd, paintStruct.address()) as MemoryAddress
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
        val result = NativeRegistry[textOutA].invoke(dc, x, y, stringStorage.address(), text.length)
        check(result != 0) {
            "Failed to draw text"
        }
    }

    fun endPaint() {
        check(dc != MemoryAddress.NULL) {
            "Not painting"
        }
        NativeRegistry[endPaint].invoke(process.hWnd, dc)
        dc = MemoryAddress.NULL
        val result = NativeRegistry[updateWindow].invoke(process.hWnd)
        check(result != 0) {
            "Failed to update window!"
        }
    }

    override fun close() {
        if (hook != MemoryAddress.NULL) {
            val result = NativeRegistry[unhookWindowsHookEx].invoke(hook) as Int
            check(result != 0) {
                "Failed to unhook from process"
            }
            hook = MemoryAddress.NULL
        }
        scope.close()
    }
}