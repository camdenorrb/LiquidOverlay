package dev.twelveoclock.liquidoverlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.twelveoclock.liquidoverlay.speech.GoogleSpeechAPI
import javax.sound.sampled.*
import kotlin.system.exitProcess

fun main() {

    //streamingMicRecognize()

    /*
    runBlocking {
        println(Liquipedia.broadcasters(listOf(Liquipedia.Wiki.DOTA_2)))
    }
    */

    createApplication()
}


val NAVIGATION_WIDTH = 200.dp

val BACKGROUND_COLOR = Color(43, 54, 72)

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
            //HomeScreen(section)
            IconToggleButton(false, {}, Modifier.size(100.dp).background(Color.Blue)) {}
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

    NavigationMenu(section)

    Box(modifier = Modifier.offset(x = NAVIGATION_WIDTH).fillMaxSize().background(BACKGROUND_COLOR)){
        Text("Meow")
    }
}

@Composable
fun SettingsScreen(section: MutableState<Section>) {

    NavigationMenu(section)

    Box(modifier = Modifier.offset(x = NAVIGATION_WIDTH).fillMaxSize().background(BACKGROUND_COLOR)){
        Text("Meow")
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