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
                        MemoryAddress::class.java,
                        Int::class.java,
                        MemoryAddress::class.java,
                        MemoryAddress::class.java
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
            val windowNameAddress = windowName?.let { ExternalStorage.fromString(windowName) }?.segment?.address() ?: MemoryAddress.NULL
            /*val windowClazz = clazz?.clazzPointer ?: MemoryAddress.NULL


            val parentWindow = parent?.window ?: MemoryAddress.NULL
            val menuWindow = menu?.window ?: MemoryAddress.NULL

            Overlay.init
            val pidInstance = instance?.handle ?: NativeRegistry[Overlay.getModuleHandle].invoke(MemoryAddress.NULL) as MemoryAddress

            val storage = param?.segment?.address() ?: MemoryAddress.NULL*/
            var result = MemoryAddress.NULL
            var error = 0
            var counter = 0
            while (result == MemoryAddress.NULL && error == 0 && counter < 100) {
                result = NativeRegistry[createWindow].invoke(
                    0,
                    clazz.clazzPointer,
                    windowNameAddress.address(),
                    13565952,
                    100,
                    100,
                    100,
                    100,
                    MemoryAddress.NULL,
                    MemoryAddress.NULL,
                    MemoryAddress.NULL,
                    MemoryAddress.NULL
                ) as MemoryAddress
                error = NativeRegistry[Callback.getLastError].invoke() as Int
                counter++
                println(result)
            }
            check (result != MemoryAddress.NULL) {
                "Failed to create window: ${NativeRegistry[Callback.getLastError].invoke()}"
            }

            NativeRegistry[showWindow].invoke(result, 5)
            tmpScope.close()

            return WindowManager(result)
        }

    }

    override fun close() {
        TODO("Not yet implemented")
    }
}