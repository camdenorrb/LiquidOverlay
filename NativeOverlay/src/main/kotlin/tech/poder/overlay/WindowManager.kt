package tech.poder.overlay

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.ResourceScope

@JvmInline
value class WindowManager(val window: MemoryAddress) : AutoCloseable {

    companion object {

        //Overlay = WS_EX_TOPMOST | WS_EX_TRANSPARENT | WS_EX_LAYERED
        //EX
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
        const val WS_OVERLAPPEDWINDOW = WS_OVERLAPPED or WS_CAPTION or WS_SYSMENU or WS_THICKFRAME or WS_MINIMIZEBOX or WS_MAXIMIZEBOX

        private val createWindow = run {
            NativeRegistry.loadLib("kernel32", "user32", "VCRUNTIME140", "api-ms-win-crt-runtime-l1-1-0", "api-ms-win-crt-math-l1-1-0", "api-ms-win-crt-stdio-l1-1-0", "api-ms-win-crt-locale-l1-1-0", "api-ms-win-crt-heap-l1-1-0")
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

        fun createWindow(
            exStyle: Int = 0,
            clazz: WindowClass? = null,
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
            val windowNameAddress = windowName?.let { CLinker.toCString(it, tmpScope) } ?: MemoryAddress.NULL
            /*val windowClazz = clazz?.clazzPointer ?: MemoryAddress.NULL


            val parentWindow = parent?.window ?: MemoryAddress.NULL
            val menuWindow = menu?.window ?: MemoryAddress.NULL

            Overlay.init
            val pidInstance = instance?.handle ?: NativeRegistry[Overlay.getModuleHandle].invoke(MemoryAddress.NULL) as MemoryAddress

            val storage = param?.segment?.address() ?: MemoryAddress.NULL*/
            val result = NativeRegistry[createWindow].invoke(
                0,
                windowNameAddress.address(),
                windowNameAddress.address(),
                0xcf0000,
                100,
                100,
                100,
                100,
                MemoryAddress.NULL,
                MemoryAddress.NULL,
                MemoryAddress.NULL,
                MemoryAddress.NULL
            ) as MemoryAddress
            println(result)
            check (result != MemoryAddress.NULL) {
                "Failed to create window: ${NativeRegistry[Callback.getLastError].invoke()}"
            }

            tmpScope.close()

            return WindowManager(MemoryAddress.NULL)
        }

    }

    override fun close() {
        TODO("Not yet implemented")
    }
}