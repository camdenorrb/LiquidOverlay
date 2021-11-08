package dev.twelveoclock.liquidoverlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.twelveoclock.liquidoverlay.speech.GoogleSpeechAPI
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.sound.sampled.*
import kotlin.system.exitProcess


fun main() {

    streamingMicRecognize()

    /*
    runBlocking {
        println(Liquipedia.broadcasters(listOf(Liquipedia.Wiki.DOTA_2)))
    }
    */

    //createApplication()
}

private fun createApplication() = application {

    // Needs to be declared here
    val density = LocalDensity.current

    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose for Desktop",
        state = rememberWindowState(width = 1000.dp, height = 600.dp),
        icon = useResource("Water-Drop.svg") { loadSvgPainter(it, density) }
    ) {

        //val count = remember { mutableStateOf(0) }

        MaterialTheme {

            Column(Modifier.size(200.dp, window.height.dp).background(Color(33, 41, 54))) {


                // Header
                Row(Modifier.height(100.dp).fillMaxWidth().offset(y = 20.dp), horizontalArrangement = Arrangement.Center) {
                    Image(useResource("Water-Drop-Transparent.svg") { loadSvgPainter(it, density) }, "Liquid Overlay Icon")
                }

                Divider(color = Color.Transparent, thickness = 40.dp)

                Row(Modifier.height(100.dp).fillMaxWidth().background(Color.Yellow)/*.offset(y = -40.dp)*/) {

                }
                // Sections
                //Rect(Offset.Zero, Size(200.toFloat(), window.height.toFloat()))
            }

        }
    }
}
            /*
                }
                Divider(color = Color.Red, thickness = 5.dp, modifier = Modifier.size(200.dp, 200.dp))

                // Sections
            //Rect(Offset.Zero, Size(200.toFloat(), window.height.toFloat()))
            }
            /*
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {

                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        count.value++
                    }
                ) {
                    Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
                }

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        count.value = 0
                    }
                ) {
                    Text("Reset")
                }

            }
            */

        }
    }
}
*/

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

            val targetInfo = DataLine.Info(
                TargetDataLine::class.java,
                audioFormat
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
            println("Start speaking")
            val startTime = System.currentTimeMillis()
            // Audio Input Stream
            val audio = AudioInputStream(targetDataLine)



            while (true) {

                val estimatedTime = System.currentTimeMillis() - startTime
                val data = ByteArray(1024 * 70)

                audio.read(data)

                if (estimatedTime > 1000) { // 1 seconds
                    println("Stop speaking.")
                    targetDataLine.stop()
                    targetDataLine.close()
                    break
                }

                println(GoogleSpeechAPI.getSpeech(data))
            }

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