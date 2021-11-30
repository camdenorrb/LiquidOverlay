package tech.poder.overlay.handles

import jdk.incubator.foreign.MemoryAddress
import tech.poder.overlay.utils.NativeUtils

object WinAPIHandles {

    init {
        NativeUtils.loadLibraries(
            "kernel32",
            "user32",
            "gdi32",
            "oleacc",
            "kernel32",
            "psapi"
        )
    }


    val clientStart = NativeUtils.lookupMethodHandle(
        "Start",
        Int::class.java,
        listOf(MemoryAddress::class.java)
    )

    val clientStop = NativeUtils.lookupMethodHandle(
        "Stop",
        Int::class.java,
        listOf(MemoryAddress::class.java)
    )

    val getLastError = NativeUtils.lookupMethodHandle(
        "GetLastError",
        Int::class.java
    )

    val failed = NativeUtils.lookupMethodHandle(
        "Failed",
        Int::class.java,
        listOf(Int::class.java)
    )

    val createSolidBrush = NativeUtils.lookupMethodHandle(
        "CreateSolidBrush",
        MemoryAddress::class.java,
        listOf(Int::class.java),
    )

    val updateWindow = NativeUtils.lookupMethodHandle(
        "UpdateWindow",
        Boolean::class.java,
        listOf(MemoryAddress::class.java)
    )

    val dispatchMessageW = NativeUtils.lookupMethodHandle(
        "DispatchMessageW",
        Int::class.java,
        listOf(MemoryAddress::class.java)
    )

    val translateMessage = NativeUtils.lookupMethodHandle(
        "TranslateMessage",
        Boolean::class.java,
        listOf(MemoryAddress::class.java)
    )

    val isWindow = NativeUtils.lookupMethodHandle(
        "IsWindow",
        Boolean::class.java,
        listOf(MemoryAddress::class.java)
    )

    val isWindowVisible = NativeUtils.lookupMethodHandle(
        "IsWindowVisible",
        Boolean::class.java,
        listOf(MemoryAddress::class.java)
    )

    val registerClassW = NativeUtils.lookupMethodHandle(
        "RegisterClassW",
        MemoryAddress::class.java,
        listOf(MemoryAddress::class.java)
    )

    val closeHandle = NativeUtils.lookupMethodHandle(
        "CloseHandle",
        Boolean::class.java,
        listOf(MemoryAddress::class.java)
    )

    val imageListDestroy = NativeUtils.lookupMethodHandle(
        "ImageList_Destroy",
        Boolean::class.java,
        listOf(MemoryAddress::class.java)
    )

    val getModuleHandleA = NativeUtils.lookupMethodHandle(
        "GetModuleHandleA",
        MemoryAddress::class.java,
        listOf(MemoryAddress::class.java)
    )

    val releaseBuffer = NativeUtils.lookupMethodHandle(
        "ReleaseBuffer",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java
        )
    )

    val enumWindows = NativeUtils.lookupMethodHandle(
        "EnumWindows",
        Byte::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val getWindowThreadProcessId = NativeUtils.lookupMethodHandle(
        "GetWindowThreadProcessId",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val getWindow = NativeUtils.lookupMethodHandle(
        "GetWindow",
        MemoryAddress::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java
        )
    )


    val releaseDC = NativeUtils.lookupMethodHandle(
        "ReleaseDC",
        MemoryAddress::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val getWindowRect = NativeUtils.lookupMethodHandle(
        "GetWindowRect",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val coInitializeEx = NativeUtils.lookupMethodHandle(
        "CoInitializeEx",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java
        )
    )

    val getAudioDeviceEndpoint = NativeUtils.lookupMethodHandle(
        "GetAudioDeviceEndpoint",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val deviceActivate = NativeUtils.lookupMethodHandle(
        "DeviceActivate",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val getBufferSize = NativeUtils.lookupMethodHandle(
        "GetBufferSize",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val getService = NativeUtils.lookupMethodHandle(
        "GetService",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val endPaint = NativeUtils.lookupMethodHandle(
        "EndPaint",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
        )
    )

    val beginPaint = NativeUtils.lookupMethodHandle(
        "BeginPaint",
        MemoryAddress::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
        )
    )

    val getNextPacketSize = NativeUtils.lookupMethodHandle(
        "GetNextPacketSize",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
        )
    )

    val showWindow = NativeUtils.lookupMethodHandle(
        "ShowWindow",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java,
        )
    )

    val getDC = NativeUtils.lookupMethodHandle(
        "GetDC",
        MemoryAddress::class.java,
        listOf(MemoryAddress::class.java)
    )

    val getClassNameA = NativeUtils.lookupMethodHandle(
        "GetClassNameA",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java
        )
    )

    val getWindowTextA = NativeUtils.lookupMethodHandle(
        "GetWindowTextA",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java
        )
    )

    val getWindowModuleFileNameA = NativeUtils.lookupMethodHandle(
        "GetWindowModuleFileNameA",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java
        )
    )

    val openProcess = NativeUtils.lookupMethodHandle(
        "OpenProcess",
        MemoryAddress::class.java,
        listOf(
            Int::class.java,
            Boolean::class.java,
            Int::class.java
        )
    )

    val imageListAdd = NativeUtils.lookupMethodHandle(
        "ImageList_AddMasked",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java
        )
    )

    val getModuleFileNameExA = NativeUtils.lookupMethodHandle(
        "GetModuleFileNameExA",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java
        )
    )

    val enumProcessModules = NativeUtils.lookupMethodHandle(
        "EnumProcessModules",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java,
            MemoryAddress::class.java
        )
    )

    val imageListCreate = NativeUtils.lookupMethodHandle(
        "ImageList_Create",
        MemoryAddress::class.java,
        listOf(
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
        )
    )

    val loadImageW = NativeUtils.lookupMethodHandle(
        "LoadImageW",
        MemoryAddress::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )
    )

    val imageListDraw = NativeUtils.lookupMethodHandle(
        "ImageList_Draw",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java,
            MemoryAddress::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )
    )

    val coCreateInstance = NativeUtils.lookupMethodHandle(
        "CoCreateInstance",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java
        )
    )

    val getMixFormat = NativeUtils.lookupMethodHandle(
        "GetMixFormat",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java
        )
    )

    val getHNSActualDuration = NativeUtils.lookupMethodHandle(
        "GetHNSActualDuration",
        Double::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java,
            Int::class.java,
        )
    )

    val invalidateRect = NativeUtils.lookupMethodHandle(
        "InvalidateRect",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Boolean::class.java,
        )
    )

    val getBuffer = NativeUtils.lookupMethodHandle(
        "GetBuffer",
        Int::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
        )
    )

    val setLayeredWindowAttributes = NativeUtils.lookupMethodHandle(
        "SetLayeredWindowAttributes",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java,
            Byte::class.java,
            Int::class.java,
        )
    )

    val getMessageW = NativeUtils.lookupMethodHandle(
        "GetMessageW",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java,
            Int::class.java,
        )
    )

    val defWindowProcW = NativeUtils.lookupMethodHandle(
        "DefWindowProcW",
        MemoryAddress::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java,
            MemoryAddress::class.java,
            MemoryAddress::class.java,
        )
    )

    val textOutA = NativeUtils.lookupMethodHandle(
        "TextOutA",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            Int::class.java,
            Int::class.java,
            MemoryAddress::class.java,
            Int::class.java,
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
            Boolean::class.java,
        )
    )

    val setWindowPos = NativeUtils.lookupMethodHandle(
        "SetWindowPos",
        Boolean::class.java,
        listOf(
            MemoryAddress::class.java,
            MemoryAddress::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
            Int::class.java,
        )
    )

    val createWindowExW = NativeUtils.lookupMethodHandle(
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
            MemoryAddress::class.java,
        )
    )

}