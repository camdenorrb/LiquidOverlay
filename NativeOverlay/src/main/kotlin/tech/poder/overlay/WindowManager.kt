package tech.poder.overlay

import jdk.incubator.foreign.*
import java.awt.Color

data class WindowManager(val window: MemoryAddress) : AutoCloseable {

    companion object {

        //Overlay = WS_EX_TOPMOST | WS_EX_TRANSPARENT | WS_EX_LAYERED
        //EX
        const val WS_EX_TOPMOST = 0x00000008
        const val WS_EX_TRANSPARENT = 0x00000020
        const val WS_EX_LAYERED = 0x00080000
        const val WS_EX_CLIENTEDGE = 0x00000200
        const val WS_EX_WINDOWEDGE = 0x00000100
        const val WS_EX_OVERLAPPEDWINDOW = WS_EX_WINDOWEDGE or WS_EX_CLIENTEDGE

        //Normal
        const val WS_OVERLAPPED = 0x00000000
        const val WS_CAPTION = 0x00C00000
        const val WS_SYSMENU = 0x00080000
        const val WS_THICKFRAME = 0x00040000
        const val WS_MINIMIZEBOX = 0x00020000
        const val WS_MAXIMIZEBOX = 0x00010000
        const val WS_VISIBLE = 0x10000000
        const val WS_POPUP = 0x80000000
        const val WS_OVERLAPPEDWINDOW =
            WS_OVERLAPPED or WS_CAPTION or WS_SYSMENU or WS_THICKFRAME or WS_MINIMIZEBOX or WS_MAXIMIZEBOX

        private val createWindow = run {
            NativeRegistry.loadLib(
                "kernel32",
                "user32",
                "gdi32",
                "oleacc",
                "kernel32",
                "psapi"
            )
            NativeRegistry.register(
                FunctionDescription(
                    "CreateWindowExW", MemoryAddress::class.java, listOf(
                        Int::class.java,
                        MemoryAddress::class.java,
                        MemoryAddress::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        MemoryAddress::class.java,
                        MemoryAddress::class.java,
                        MemoryAddress::class.java,
                        MemoryAddress::class.java
                    )
                )
            )
        }

        private val showWindow = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "ShowWindow", Boolean::class.java, listOf(
                        MemoryAddress::class.java,
                        Int::class.java,
                    )
                )
            )
        }

        internal val defWindowProcW = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "DefWindowProcW", MemoryAddress::class.java, listOf(
                        MemoryAddress::class.java, Int::class.java, MemoryAddress::class.java, MemoryAddress::class.java
                    )
                )
            )
        }

        val invisibleColor = Color(255, 0, 0, 0)
        val invisibleRGB = Color(invisibleColor.red, invisibleColor.green, invisibleColor.blue, invisibleColor.alpha).rgb
        val invisible = Color(invisibleColor.blue, invisibleColor.green, invisibleColor.red, invisibleColor.alpha).rgb

        private val setLayeredWindowAttributes = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "SetLayeredWindowAttributes", Boolean::class.java, listOf(
                        MemoryAddress::class.java, Int::class.java, Byte::class.java, Int::class.java
                    )
                )
            )
        }

        fun createWindow(
            exStyle: Int = 0,
            clazz: WindowClass,
            windowName: String? = null,
            style: Int = 0,
            x: Int = 1,
            y: Int = 1,
            width: Int = 0,
            height: Int = 0,
            parent: WindowManager? = null,
            menu: WindowManager? = null,
            instance: Process? = null,
            param: ExternalStorage? = null
        ): WindowManager {

            val tmpScope = ResourceScope.newConfinedScope()
            val windowNameAddress =
                windowName?.let { ExternalStorage.fromString(windowName) }?.segment?.address() ?: MemoryAddress.NULL


            val parentWindow = parent?.window ?: MemoryAddress.NULL
            val menuWindow = menu?.window ?: MemoryAddress.NULL

            val pidInstance =
                instance?.handle ?: NativeRegistry[Callback.getModuleHandle].invoke(MemoryAddress.NULL) as MemoryAddress

            val storage = param?.segment?.address() ?: MemoryAddress.NULL
            val result = NativeRegistry[createWindow].invoke(
                exStyle,
                clazz.clazzPointer,
                windowNameAddress.address(),
                style,
                x,
                y,
                width,
                height,
                parentWindow,
                menuWindow,
                pidInstance,
                storage
            ) as MemoryAddress
            println(result)
            check(result != MemoryAddress.NULL) {
                "Failed to create window: ${NativeRegistry[Callback.getLastError].invoke()}"
            }


            val result2 =
                NativeRegistry[setLayeredWindowAttributes].invoke(result, invisible, 0.toByte(), 0x00000001) as Int
            check(result2 != 0) {
                "Failed to set layered window attributes: ${NativeRegistry[Callback.getLastError].invoke()}"
            }
            NativeRegistry[showWindow].invoke(result, 5)
            tmpScope.close()

            return WindowManager(result)
        }

        val getMessage = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "GetMessageW", Boolean::class.java, listOf(
                        MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java, Int::class.java
                    )
                )
            )
        }

        val dispatchMessage = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "DispatchMessageW", Int::class.java, listOf(
                        MemoryAddress::class.java
                    )
                )
            )
        }

        val translateMessage = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "TranslateMessage", Boolean::class.java, listOf(
                        MemoryAddress::class.java
                    )
                )
            )
        }
        val setWindowPosition = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "SetWindowPos", Boolean::class.java, listOf(
                        MemoryAddress::class.java,
                        MemoryAddress::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java
                    )
                )
            )
        }

        val msgStruct = StructDefinition.generate(
            listOf(
                MemoryAddress::class.java,
                Int::class.java,
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java
            )
        )

        val moveWindow = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "MoveWindow", Boolean::class.java, listOf(
                        MemoryAddress::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        Boolean::class.java
                    )
                )
            )
        }

        val isWindow = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "IsWindow", Boolean::class.java, listOf(
                        MemoryAddress::class.java
                    )
                )
            )
        }

        val isWindowVisible = run {
            NativeRegistry.loadLib("user32")
            NativeRegistry.register(
                FunctionDescription(
                    "IsWindowVisible", Boolean::class.java, listOf(
                        MemoryAddress::class.java
                    )
                )
            )
        }

        val beginPaint = NativeRegistry.register(
            FunctionDescription(
                "BeginPaint", MemoryAddress::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            )
        )

        val endPaint = NativeRegistry.register(
            FunctionDescription(
                "EndPaint", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
            )
        )

        val updateWindow = NativeRegistry.register(
            FunctionDescription(
                "UpdateWindow", Boolean::class.java, listOf(MemoryAddress::class.java)
            )
        )

        val textOutA = NativeRegistry.register(
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

        val invalidateRect = NativeRegistry.register(
            FunctionDescription(
                "InvalidateRect", Boolean::class.java, listOf(
                    MemoryAddress::class.java,
                    MemoryAddress::class.java,
                    Boolean::class.java
                )
            )
        )

        val HWND_TOPMOST = WindowManager(MemoryAddress.ofLong(-1L))
        val HWND_BOTTOM = WindowManager(MemoryAddress.ofLong(1L))
    }

    val scope = ResourceScope.newConfinedScope()
    var dc = MemoryAddress.NULL

    val paintStruct = MemorySegment.allocateNative(
        CLinker.C_POINTER.byteSize() + (CLinker.C_INT.byteSize() * 7) + (CLinker.C_CHAR.byteSize() * 32), scope
    )

    val stringStorage = MemorySegment.allocateNative(CLinker.C_CHAR.byteSize() * 128, scope)

    fun doLoop(extra: () -> Unit = {}) {
        val scope = ResourceScope.newConfinedScope()
        val storage = MemorySegment.allocateNative(msgStruct.size, scope)
        while (NativeRegistry[getMessage].invoke(storage.address(), window, 0, 0) as Int != 0) {
            NativeRegistry[translateMessage].invoke(storage.address())
            NativeRegistry[dispatchMessage].invoke(storage.address())
            extra.invoke()
        }
    }

    fun showWindow() {
        NativeRegistry[showWindow].invoke(window, 5)
    }

    fun hideWindow() {
        NativeRegistry[showWindow].invoke(window, 0)
    }

    fun setWindowPosition(
        afterOther: WindowManager? = null, x: Int = 0, y: Int = 0, cx: Int = 0, cy: Int = 0, flags: Int = 0
    ): Boolean {
        val other = afterOther?.window ?: MemoryAddress.NULL
        return NativeRegistry[setWindowPosition].invoke(window, other, x, y, cx, cy, flags) as Int != 0
    }

    fun getWindowRect(storage: ExternalStorage) {
        NativeRegistry[Callback.getWindowRect].invoke(window, storage.segment.address())
    }

    fun isAlive(): Boolean {
        return NativeRegistry[isWindow].invoke(window) as Int != 0
    }

    fun isVisible(): Boolean {
        return NativeRegistry[isWindowVisible].invoke(window) as Int != 0
    }

    fun moveWindow(x: Int = 0, y: Int = 0, width: Int = 0, height: Int = 0, repaint: Int = 0): Boolean {
        return NativeRegistry[moveWindow].invoke(window, x, y, width, height, repaint) != 0
    }

    var changed = false
    private fun zeroOut() {
        paintStruct.fill(0)

    }

    fun startPaint() {
        check(dc == MemoryAddress.NULL) {
            "Already started painting"
        }
        if (changed) {
            zeroOut()
        }
        dc = NativeRegistry[beginPaint].invoke(window, paintStruct.address()) as MemoryAddress
        check(dc != MemoryAddress.NULL) {
            "Failed to get DC"
        }
    }

    fun drawText(text: String, x: Int, y: Int) {
        changed = true
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

    fun updateWindow(): Boolean {
        val res1 = NativeRegistry[invalidateRect].invoke(window, MemoryAddress.NULL, 0)
        if (res1 == 0) {
            return false
        }
        return NativeRegistry[updateWindow].invoke(window) != 0
    }

    fun endPaint() {
        check(dc != MemoryAddress.NULL) {
            "Not painting"
        }
        NativeRegistry[endPaint].invoke(window, dc)
        dc = MemoryAddress.NULL
        changed = false
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}