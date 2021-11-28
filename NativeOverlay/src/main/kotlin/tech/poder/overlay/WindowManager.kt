package tech.poder.overlay

import jdk.incubator.foreign.*
import tech.poder.overlay.utils.NativeUtils
import java.awt.Color

data class WindowManager(val window: MemoryAddress) : AutoCloseable {

    override fun close() {
        TODO("Not yet implemented")
    }


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

            NativeUtils.loadLibraries(
                "kernel32",
                "user32",
                "gdi32",
                "oleacc",
                "kernel32",
                "psapi"
            )

            NativeUtils.lookupMethodHandle(
                "CreateWindowExW",
                MemoryAddress::class.java,
                listOf(
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
        }

        private val showWindow = NativeUtils.lookupMethodHandle(
            "ShowWindow",
            Boolean::class.java,
            listOf(
                MemoryAddress::class.java,
                Int::class.java,
            )
        )

        internal val defWindowProcW = NativeUtils.lookupMethodHandle(
            "DefWindowProcW",
            MemoryAddress::class.java,
            listOf(
                MemoryAddress::class.java, Int::class.java, MemoryAddress::class.java, MemoryAddress::class.java
            )
        )


        val invisibleColor = Color(255, 0, 0, 0)
        val invisibleRGB =
            Color(invisibleColor.red, invisibleColor.green, invisibleColor.blue, invisibleColor.alpha).rgb
        val invisible = Color(invisibleColor.blue, invisibleColor.green, invisibleColor.red, invisibleColor.alpha).rgb

        private val setLayeredWindowAttributes = NativeUtils.lookupMethodHandle(
            "SetLayeredWindowAttributes",
            Boolean::class.java,
            listOf(MemoryAddress::class.java, Int::class.java, Byte::class.java, Int::class.java)
        )

        val getMessage = NativeUtils.lookupMethodHandle(
            "GetMessageW",
            Boolean::class.java, listOf(
                MemoryAddress::class.java, MemoryAddress::class.java, Int::class.java, Int::class.java
            )
        )

        val dispatchMessage = NativeUtils.lookupMethodHandle(
            "DispatchMessageW",
            Int::class.java,
            listOf(
                MemoryAddress::class.java
            )
        )

        val translateMessage = NativeUtils.lookupMethodHandle(
            "TranslateMessage",
            Boolean::class.java,
            listOf(
                MemoryAddress::class.java
            )
        )

        val setWindowPosition = NativeUtils.lookupMethodHandle(
            "SetWindowPos",
            Boolean::class.java,
            listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java
            )
        )

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

        val moveWindow = NativeUtils.lookupMethodHandle(
            "MoveWindow",
            Boolean::class.java,
            listOf(
                MemoryAddress::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                Boolean::class.java
            )
        )

        val isWindow = NativeUtils.lookupMethodHandle(
            "IsWindow",
            Boolean::class.java,
            listOf(
                MemoryAddress::class.java
            )
        )


        val isWindowVisible = NativeUtils.lookupMethodHandle(
            "IsWindowVisible",
            Boolean::class.java,
            listOf(
                MemoryAddress::class.java
            )
        )

        val beginPaint = NativeUtils.lookupMethodHandle(
            "BeginPaint", MemoryAddress::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        )


        val endPaint = NativeUtils.lookupMethodHandle(
            "EndPaint", Boolean::class.java, listOf(MemoryAddress::class.java, MemoryAddress::class.java)
        )

        val updateWindow = NativeUtils.lookupMethodHandle(
            "UpdateWindow", Boolean::class.java, listOf(MemoryAddress::class.java)
        )

        val textOutA = NativeUtils.lookupMethodHandle(
            "TextOutA",
            Boolean::class.java,
            listOf(
                MemoryAddress::class.java,
                Int::class.java,
                Int::class.java,
                MemoryAddress::class.java,
                Int::class.java
            )
        )

        val invalidateRect = NativeUtils.lookupMethodHandle(
            "InvalidateRect",
            Boolean::class.java,
            listOf(
                MemoryAddress::class.java,
                MemoryAddress::class.java,
                Boolean::class.java
            )
        )

        val HWND_TOPMOST = WindowManager(MemoryAddress.ofLong(-1L))
        val HWND_BOTTOM = WindowManager(MemoryAddress.ofLong(1L))

        val scope = ResourceScope.newConfinedScope()
        var dc = MemoryAddress.NULL

        val paintStruct = MemorySegment.allocateNative(
            CLinker.C_POINTER.byteSize() + (CLinker.C_INT.byteSize() * 7) + (CLinker.C_CHAR.byteSize() * 32), scope
        )

        val stringStorage = MemorySegment.allocateNative(CLinker.C_CHAR.byteSize() * 128, scope)


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

            //val tmpScope = ResourceScope.newConfinedScope()

            val windowNameAddress = windowName?.let { ExternalStorage.fromString(windowName) }?.segment?.address()
                ?: MemoryAddress.NULL

            val result = createWindow.invoke(
                exStyle,
                clazz.clazzPointer,
                windowNameAddress.address(),
                style,
                x,
                y,
                width,
                height,
                parent?.window ?: MemoryAddress.NULL,
                menu?.window ?: MemoryAddress.NULL,
                instance?.handle ?: NativeAPI.getModuleHandle(MemoryAddress.NULL) as MemoryAddress,
                param?.segment?.address() ?: MemoryAddress.NULL
            ) as MemoryAddress

            println(result)

            check(result != MemoryAddress.NULL) {
                "Failed to create window: ${NativeAPI.getLastError.invoke()}"
            }

            check(setLayeredWindowAttributes.invoke(result, invisible, 0.toByte(), 0x00000001) != 0) {
                "Failed to set layered window attributes: ${NativeAPI.getLastError()}"
            }

            showWindow(result, 5)
            //tmpScope.close()

            return WindowManager(result)
        }
    }

    fun doLoop(extra: () -> Unit = {}) {

        val storageAddress = MemorySegment.allocateNative(msgStruct.size, ResourceScope.newConfinedScope()).address()

        while (getMessage(storageAddress, window, 0, 0) != 0) {
            translateMessage(storageAddress)
            dispatchMessage(storageAddress)
            extra.invoke()
        }
    }

    fun showWindow() {
        showWindow(window, 5)
    }

    fun hideWindow() {
        showWindow(window, 0)
    }

    fun setWindowPosition(afterOther: WindowManager? = null, x: Int = 0, y: Int = 0, cx: Int = 0, cy: Int = 0, flags: Int = 0): Boolean {
        return setWindowPosition(window, afterOther?.window ?: MemoryAddress.NULL, x, y, cx, cy, flags) != 0
    }

    fun getWindowRect(storage: ExternalStorage) {
        NativeAPI.getWindowRect(window, storage.segment.address())
    }

    fun isAlive(): Boolean {
        return isWindow(window) != 0
    }

    fun isVisible(): Boolean {
        return isWindowVisible(window) != 0
    }

    fun moveWindow(x: Int = 0, y: Int = 0, width: Int = 0, height: Int = 0, repaint: Int = 0): Boolean {
        return moveWindow(window, x, y, width, height, repaint) != 0
    }

    fun startPaint() {

        check(dc == MemoryAddress.NULL) {
            "Already started painting"
        }

        paintStruct.fill(0)
        dc = beginPaint(window, paintStruct.address()) as MemoryAddress

        check(dc != MemoryAddress.NULL) {
            "Failed to get DC"
        }
    }

    fun drawText(text: String, x: Int, y: Int) {

        check(dc != MemoryAddress.NULL) {
            "Not painting"
        }

        text.forEachIndexed { index, char ->
            MemoryAccess.setCharAtIndex(stringStorage, index.toLong(), char)
        }

        check(textOutA(dc, x, y, stringStorage.address(), text.length) != 0) {
            "Failed to draw text"
        }
    }

    fun updateWindow(): Boolean {

        if (invalidateRect(window, MemoryAddress.NULL, 0) == 0) {
            return false
        }

        return updateWindow(window) != 0
    }

    fun endPaint() {

        check(dc != MemoryAddress.NULL) {
            "Not painting"
        }

        endPaint(window, dc)
        dc = MemoryAddress.NULL
    }

}