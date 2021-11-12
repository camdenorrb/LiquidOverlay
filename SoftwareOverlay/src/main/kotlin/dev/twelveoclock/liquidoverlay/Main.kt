package dev.twelveoclock.liquidoverlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.twelveoclock.liquidoverlay.api.Liquipedia
import dev.twelveoclock.liquidoverlay.speech.GoogleSpeechAPI
import kotlinx.coroutines.runBlocking
import javax.sound.sampled.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess


val NAVIGATION_WIDTH = 200.dp

val BACKGROUND_COLOR = Color(43, 54, 72)

val liquipedia = Liquipedia("")

fun main() {
    
    //System.setOut(PrintStream(FileOutputStream("log.txt", true)))
    //System.setErr(PrintStream(FileOutputStream("err.txt", true)))
    createApplication()
    //val storage = ExternalStorage.fromString("Hi")
    //val clazz = WindowClass.fromStorage(storage)
    //WindowManager.createWindow(WindowManager.WS_EX_OVERLAPPEDWINDOW, clazz, "Hello", style = WindowManager.WS_OVERLAPPEDWINDOW, width = 100, height = 100)
    //streamingMicRecognize()

    /*
    runBlocking {
        println(Liquipedia.broadcasters(listOf(Liquipedia.Wiki.DOTA_2)))
    }
    */
}


// https://developer.android.com/jetpack/compose
private fun createApplication() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LiquidOverlay",
        state = rememberWindowState(width = 1000.dp, height = 600.dp, position = WindowPosition(Alignment.Center)),
        icon = painterResource("logo/logoOverlay.svg"),
    ) {

        val section = remember { mutableStateOf(Section.HOME) }

        MaterialTheme {
            when (section.value) {
                Section.HOME -> HomeScreen(section)
                Section.OVERLAY -> OverlayScreen(section)
                Section.SETTINGS -> SettingsScreen(section)
            }
        }
    }
}

@Composable
fun HomeScreen(section: MutableState<Section>) {

    NavigationMenu(section)

    Box(modifier = Modifier.offset(x = NAVIGATION_WIDTH).fillMaxSize().background(BACKGROUND_COLOR)){
        Text("Meow")
    }
}

@Composable
fun OverlayScreen(section: MutableState<Section>) {

    val rowPadding = 5.dp
    val rowHeight = 150.dp
    val colPadding = 20.dp
    val colWidth = 200.dp

    val offColor = Color(33, 41, 54)
    val onColor = Color(81, 142, 240)

    val notActivated = 0f
    val activated = 1f

    val footstepsOffsetX = 0.dp
    val footstepsOffsetY = 225.dp

    val gunshotsOffsetX = 0.dp
    val gunshotsOffsetY = 175.dp

    val textColor = Color.White

    var captionsActivated by remember { mutableStateOf(false) }
    var footstepsActivated by remember { mutableStateOf(false) }
    var gunshotsActivated by remember { mutableStateOf(false) }
    var translateActivated by remember { mutableStateOf(false) }
    var musicActivated by remember { mutableStateOf(false) }



    NavigationMenu(section)

    Column(modifier = Modifier.padding(start = NAVIGATION_WIDTH).fillMaxSize().background(BACKGROUND_COLOR)){

        // Menu
        Row(
            modifier = Modifier.padding(rowPadding).fillMaxWidth().height(rowHeight),
            horizontalArrangement = Arrangement.Center
        ) {

            // Captions
            Column(
                modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (captionsActivated) onColor else offColor).clickable { captionsActivated = !captionsActivated},
                horizontalAlignment = Alignment.CenterHorizontally){

                Icon(
                    painterResource("font-icons/captions.svg"),
                    modifier = Modifier.padding(5.dp),
                    tint = textColor,
                    contentDescription = "Captions",
                )

                Text(
                    text = "Captions",
                    color = textColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
            }

            // Visual Footsteps
            Column(
                modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (footstepsActivated) onColor else offColor).clickable { footstepsActivated = !footstepsActivated},
                horizontalAlignment = Alignment.CenterHorizontally){

                Icon(
                    painterResource("font-icons/shoe.svg"),
                    modifier = Modifier.padding(10.dp),
                    tint = textColor,
                    contentDescription = "Visual Footsteps",
                )

                Text(
                    text = "Visual Footsteps",
                    color = textColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
            }

            // Visual Gunshots
            Column(
                modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (gunshotsActivated) onColor else offColor).clickable { gunshotsActivated = !gunshotsActivated},
                horizontalAlignment = Alignment.CenterHorizontally){

                Icon(
                    painterResource("font-icons/gun.svg"),
                    modifier = Modifier.padding(15.dp),
                    tint = textColor,
                    contentDescription = "Visual Gunshots",
                )

                Text(
                    text = "Visual Gunshots",
                    color = textColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
            }

            // Translate
            Column(
                modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (translateActivated) onColor else offColor).clickable { translateActivated = !translateActivated},
                horizontalAlignment = Alignment.CenterHorizontally){

                Icon(
                    painterResource("font-icons/translate.svg"),
                    modifier = Modifier.padding(5.dp),
                    tint = textColor,
                    contentDescription = "Translate",
                )

                Text(
                    text = "Translate",
                    color = textColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
            }

            // Music
            Column(
                modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (musicActivated) onColor else offColor).clickable { musicActivated = !musicActivated},
                horizontalAlignment = Alignment.CenterHorizontally){

                Icon(
                    painterResource("font-icons/music.svg"),
                    modifier = Modifier.padding(17.dp),
                    tint = textColor,
                    contentDescription = "Music"
                )

                Text(
                    text = "Music",
                    color = textColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
            }
        }

        // Gameplay Image
        Row(
            modifier = Modifier.padding(rowPadding).fillMaxWidth().fillMaxHeight(),
            horizontalArrangement = Arrangement.Center
        ) {

            Box {
                //Gameplay Image

                Image(
                    painterResource("image/valorant.jpg"),
                    "Valorant Gameplay Image",
                    modifier = Modifier.fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )

                var captionOffsetX by remember { mutableStateOf(0f) }
                var captionOffsetY by remember { mutableStateOf(0f) }
                var translateOffsetX by remember { mutableStateOf(0f) }
                var translateOffsetY by remember { mutableStateOf(0f) }
                var musicOffsetX by remember { mutableStateOf(0f) }
                var musicOffsetY by remember { mutableStateOf(0f) }

                // Draggable Captions
                Image(
                    painterResource("draggable/captions.svg"),
                    "Draggable Captions",
                    alpha = if (captionsActivated) activated else notActivated,
                    modifier = Modifier
                        .offset { IntOffset(captionOffsetX.roundToInt(), captionOffsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                captionOffsetX += dragAmount.x
                                captionOffsetY += dragAmount.y
                            }
                        }
                )

                // Draggable Translate
                Image(
                    painterResource("draggable/translate.svg"),
                    "Draggable Translation",
                    alpha = if (translateActivated) activated else notActivated,
                    modifier = Modifier
                        .offset { IntOffset(translateOffsetX.roundToInt(), translateOffsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                translateOffsetX += dragAmount.x
                                translateOffsetY += dragAmount.y
                            }
                        }
                )

                // Draggable Music
                Image(
                    painterResource("draggable/music.svg"),
                    "Draggable Music",
                    alpha = if (musicActivated) activated else notActivated,
                    modifier = Modifier
                        .offset { IntOffset(musicOffsetX.roundToInt(), musicOffsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                musicOffsetX += dragAmount.x
                                musicOffsetY += dragAmount.y
                            }
                        }
                )

                // Footsteps Toggle
                Image(
                    painterResource("image/footprints.svg"),
                    "Visual Footprints",
                    alpha = if (footstepsActivated) activated else notActivated,
                    modifier = Modifier.padding(horizontal = footstepsOffsetX, vertical = footstepsOffsetY)
                )

                // Gunshots Toggle
                Image(
                    painterResource("image/gunshots.svg"),
                    "Visual Gunshots",
                    alpha = if (gunshotsActivated) activated else notActivated,
                    modifier = Modifier.padding(horizontal = gunshotsOffsetX, vertical = gunshotsOffsetY)
                )

            }
        }
    }
}

@Composable
fun SettingsScreen(section: MutableState<Section>) {

    NavigationMenu(section)

    val prefixColor = Color(81, 142, 240)
    val textColor = Color.White
    val rowColor = Color(33, 41, 54)
    val rowHeight = 50.dp
    val rowPadding = 20.dp

    Column(modifier = Modifier.padding(start = NAVIGATION_WIDTH).fillMaxSize().background(BACKGROUND_COLOR)){

        // Color blind
        Row(
            modifier = Modifier.padding(rowPadding).fillMaxWidth().height(rowHeight).background(rowColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Color Blind",
                    color = prefixColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "for vibrant colors and sharper contrast",
                    modifier = Modifier.padding(top = 5.dp),
                    color = textColor,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
            }

            Switch(
                checked = true,
                onCheckedChange = {},
            )
        }

        // Dark/Light
        Row(
            modifier = Modifier.padding(rowPadding).fillMaxWidth().height(rowHeight).background(rowColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Dark/Light",
                    color = prefixColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "to prove if you’re a true gamer",
                    modifier = Modifier.padding(top = 5.dp),
                    color = textColor,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
            }

            Switch(
                checked = true,
                onCheckedChange = {},
            )
        }

        // Language
        Row(
            modifier = Modifier.padding(rowPadding).fillMaxWidth().height(rowHeight).background(rowColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Language",
                    color = prefixColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "for message transcription and translating",
                    modifier = Modifier.padding(top = 5.dp),
                    color = textColor,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
                )
            }

            var isExpanded by remember { mutableStateOf(false) }
            val languages = listOf("English", "中文", "Español")

            Row(modifier = Modifier.padding(end = 10.dp, top = 10.dp), horizontalArrangement = Arrangement.Center) {

                Text(
                    text = "English",
                    modifier = Modifier.clickable { isExpanded = !isExpanded },
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font("font/Roboto-Medium.ttf")),
                )

                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = "DropDown",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp).offset(x = 0.dp, y = -7.dp).clickable { isExpanded = !isExpanded}
                )

                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.size(20.dp).background(Color.White)
                ) {

                }

            }
        }

    }
}

@Composable
fun NavigationMenu(section: MutableState<Section>) {

    val prefixIconColor = Color.White
    val textColor = Color(86, 101, 127)
    val selectedColor = Color(25, 118, 210)

    //BitmapPainter(useResource())
    Column(Modifier.width(NAVIGATION_WIDTH).fillMaxHeight().background(Color(33, 41, 54))) {

        // Header with Icon
        Row(
            modifier = Modifier.height(100.dp).fillMaxWidth().offset(y = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource("logo/logoTransparent.svg"),
                "Liquid Overlay Icon"
            )
        }

        Divider(color = Color.Transparent, thickness = 50.dp)

        // Home Row
        Row(
            modifier = Modifier.height(30.dp).fillMaxWidth().clickable { section.value = Section.HOME },
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // https://developer.android.com/reference/kotlin/androidx/compose/material/icons/Icons
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = "Home",
                tint = if (section.value == Section.HOME) selectedColor else prefixIconColor
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = "Home",
                color = if (section.value == Section.HOME) selectedColor else textColor,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
            )

        }

        // Overlay Row
        Row(
            modifier = Modifier.height(30.dp).fillMaxWidth().clickable { section.value = Section.OVERLAY },
            verticalAlignment = Alignment.CenterVertically
        ) {

            // https://developer.android.com/reference/kotlin/androidx/compose/material/icons/Icons
            Icon(
                painter = painterResource("font-icons/layers.svg"),
                contentDescription = "Overlay",
                tint = if (section.value == Section.OVERLAY) selectedColor else prefixIconColor
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = "Overlay",
                color = if (section.value == Section.OVERLAY) selectedColor else textColor,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
            )

        }

        // Settings Row
        Row(
            modifier = Modifier.height(30.dp).fillMaxWidth().clickable { section.value = Section.SETTINGS },
            verticalAlignment = Alignment.CenterVertically
        ) {

            // https://developer.android.com/reference/kotlin/androidx/compose/material/icons/Icons
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = if (section.value == Section.SETTINGS) selectedColor else prefixIconColor
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = "Settings",
                color = if (section.value == Section.SETTINGS) selectedColor else textColor,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
            )

        }

    }
}


enum class Section {
    HOME,
    OVERLAY,
    SETTINGS,
}


/** Performs microphone streaming speech recognition with a duration of 1 minute.  */

@Throws(Exception::class)
fun streamingMicRecognize() {

    //var responseObserver: ResponseObserver<StreamingRecognizeResponse?>? = null

    try {
        /*SpeechClient.create().use { client ->

            responseObserver = object : ResponseObserver<StreamingRecognizeResponse?> {

                val responses = mutableListOf<StreamingRecognizeResponse>()


                override fun onStart(controller: StreamController?) {

                }

                override fun onResponse(response: StreamingRecognizeResponse?) {
                    responses.add(response!!)
                }

                override fun onError(t: Throwable?) {
                    println(t)
                }

                override fun onComplete() {
                    for (response in responses) {
                        val result: StreamingRecognitionResult = response.resultsList[0]
                        val alternative: SpeechRecognitionAlternative = result.alternativesList[0]
                        System.out.printf("Transcript : %s\n", alternative.transcript)
                    }
                }

            }*/

        //val clientStream: ClientStream<StreamingRecognizeRequest> = client.streamingRecognizeCallable().splitCall(responseObserver)

        /*val recognitionConfig: RecognitionConfig = RecognitionConfig.newBuilder()
            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
            .setLanguageCode("en-US")
            .setSampleRateHertz(16000)
            .build()

        val streamingRecognitionConfig: StreamingRecognitionConfig =
            StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build()
        var request: StreamingRecognizeRequest = StreamingRecognizeRequest.newBuilder()
            .setStreamingConfig(streamingRecognitionConfig)
            .build() // The first request in a streaming call has to be a config
        //clientStream.send(request)*/
        // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
        // bigEndian: false

        val audioFormat = AudioFormat(16000f, 16, 1, true, false)
        val bytesPerSecond = (audioFormat.sampleRate * audioFormat.sampleSizeInBits) / 8.0

        val targetInfo = DataLine.Info(
            TargetDataLine::class.java, audioFormat
        )
        // Set the system information to read from the microphone audio stream
        if (!AudioSystem.isLineSupported(targetInfo)) {
            println("Microphone not supported")
            exitProcess(0)
        }

        // Target data line captures the audio stream the microphone produces.
        val targetDataLine = AudioSystem.getLine(targetInfo) as TargetDataLine
        targetDataLine.open(audioFormat)
        targetDataLine.start()

        val fiveSeconds = ByteArray(5 * bytesPerSecond.toInt())
        // Audio Input Stream
        val audio = AudioInputStream(targetDataLine)
        val googleSpeechAPI = GoogleSpeechAPI(rate = audioFormat.sampleRate.toInt())
        repeat(5) {
            println("Start speaking: $it")

            audio.read(fiveSeconds)

            println("Stop speaking: $it")
            val result = googleSpeechAPI.getSpeech(fiveSeconds)
            println(result)
        }
        audio.close()
        targetDataLine.stop()
        targetDataLine.close()
        targetDataLine.close()

        //}
    } catch (e: Exception) {
        println(e)
    }
    //responseObserver!!.onComplete()
}

/*
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Hello World")
    }

}*/