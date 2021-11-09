package tech.poder.overlay

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.ResourceScope

data class WindowManager(val window: MemoryAddress) : AutoCloseable {
    companion object {
        init {
            NativeRegistry.loadLib("User32")
        }

        //Overlay = WS_EX_TOPMOST | WS_EX_TRANSPARENT | WS_EX_LAYERED
        const val WS_EX_ACCEPTFILES = 0x00000010
        const val WS_EX_APPWINDOW = 0x00040000
        const val WS_EX_CLIENTEDGE = 0x00000200
        const val WS_EX_COMPOSITED = 0x02000000
        const val WS_EX_CONTEXTHELP = 0x00000400
        const val WS_EX_CONTROLPARENT = 0x00010000
        const val WS_EX_DLGMODALFRAME = 0x00000001
        const val WS_EX_LAYERED = 0x00080000
        const val WS_EX_LAYOUTRTL = 0x00400000
        const val WS_EX_LEFT = 0x00000000
        const val WS_EX_LEFTSCROLLBAR = 0x00004000
        const val WS_EX_LTRREADING = 0x00000000
        const val WS_EX_MDICHILD = 0x00000040
        const val WS_EX_NOACTIVATE = 0x08000000
        const val WS_EX_NOINHERITLAYOUT = 0x00100000
        const val WS_EX_NOPARENTNOTIFY = 0x00000004
        const val WS_EX_NOREDIRECTIONBITMAP = 0x00200000
        const val WS_EX_OVERLAPPEDWINDOW = 0x00000300
        const val WS_EX_PALETTEWINDOW = 0x00000188
        const val WS_EX_RIGHT = 0x00001000
        const val WS_EX_RIGHTSCROLLBAR = 0x00000000
        const val WS_EX_RTLREADING = 0x00002000
        const val WS_EX_STATICEDGE = 0x00020000
        const val WS_EX_TOOLWINDOW = 0x00000080
        const val WS_EX_TOPMOST = 0x00000008
        const val WS_EX_TRANSPARENT = 0x00000020
        const val WS_EX_WINDOWEDGE = 0x00000100

        //Normal
        const val WS_BORDER = 0x00800000
        const val WS_CAPTION = 0x00C00000
        const val WS_CHILD = 0x40000000
        const val WS_CHILDWINDOW = 0x40000000
        const val WS_CLIPCHILDREN = 0x02000000
        const val WS_CLIPSIBLINGS = 0x04000000
        const val WS_DISABLED = 0x08000000
        const val WS_DLGFRAME = 0x00400000
        const val WS_GROUP = 0x00020000
        const val WS_HSCROLL = 0x00100000
        const val WS_ICONIC = 0x20000000
        const val WS_MAXIMIZE = 0x01000000
        const val WS_POPUP = 0x80000000


        private val createWindow = NativeRegistry.register(
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

        fun createWindow(
            exStyle: Int = 0,
            className: String? = null,
            windowName: String? = null,
            style: Int = 0,
            x: Int = 1,
            y: Int = 1,
            width: Int = 0,
            height: Int = 0,
            parent: MemoryAddress = MemoryAddress.NULL,
            menu: MemoryAddress = MemoryAddress.NULL,
            instance: MemoryAddress = MemoryAddress.NULL,
            param: MemoryAddress = MemoryAddress.NULL
        ): WindowManager {
            val tmpScope = ResourceScope.newConfinedScope()
            val classNameAddress = if (className == null) {
                MemoryAddress.NULL
            } else {
                CLinker.toCString(className, tmpScope)
            }
            val windowNameAddress = if (windowName == null) {
                MemoryAddress.NULL
            } else {
                CLinker.toCString(windowName, tmpScope)
            }
            val result = NativeRegistry[createWindow].invoke(
                exStyle, classNameAddress, windowNameAddress, style, x, y, width, height, parent, menu, instance, param
            ) as MemoryAddress
            tmpScope.close()
            return WindowManager(result)
        }

    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
