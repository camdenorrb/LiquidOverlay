package dev.twelveoclock.liquidoverlay

import dev.twelveoclock.liquidoverlay.api.Liquipedia
import dev.twelveoclock.liquidoverlay.modules.sub.PluginModule
import dev.twelveoclock.liquidoverlay.speech.GoogleSpeechAPI
import jdk.incubator.foreign.MemoryAccess
import tech.poder.overlay.audio.AudioChannels
import tech.poder.overlay.audio.FormatData
import tech.poder.overlay.audio.SpeechToText
import tech.poder.overlay.general.Callback
import tech.poder.overlay.video.OverlayImpl
import tech.poder.overlay.video.WindowClass
import tech.poder.overlay.video.WindowManager
import javax.sound.sampled.*
import kotlin.experimental.and
import kotlin.io.path.Path
import kotlin.math.min
import kotlin.system.exitProcess


val LIQUIPEDIA by lazy { Liquipedia(TODO()) }


object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        streamingSpeakerRecognize()
        //GUI.createApplication()
        //pluginThingy()

        /*
        val processes = Callback.getProcesses()
        var i = 0
        processes.forEachIndexed { index, process ->
            if (process.exeLocation.contains("Overwatch.exe")) {
                i = index
            }
            println("$index: ${process.title}(${process.exeLocation})")
        }
        println("Choose: $i")
        val selected = processes[i]
        val clazz = WindowClass.define("Kats")

        val window = WindowManager.createWindow(
            WindowManager.WS_EX_TOPMOST or WindowManager.WS_EX_TRANSPARENT or WindowManager.WS_EX_LAYERED,
            clazz = clazz,
            windowName = "LiquidOverlay",
            style = WindowManager.WS_POPUP.toInt(),
            x = selected.rect.left.toInt(),
            y = selected.rect.top.toInt(),
            width = selected.rect.width.toInt(),
            height = selected.rect.height.toInt()
        )

        val selectedWindow = selected.asWindow()

        val overlay = OverlayImpl(window, selectedWindow) {

            val bufferedImage = BufferedImage(it.canvasWidth, it.canvasHeight, BufferedImage.TYPE_INT_RGB)
            val graphics = bufferedImage.createGraphics()

            graphics.color = java.awt.Color(0, 100, 0)
            graphics.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

            it.image(bufferedImage, Overlay.Position(0, 0), it.canvasWidth, it.canvasHeight)

            graphics.dispose()
        }

        //System.setOut(PrintStream(FileOutputStream("log.txt", true)))
        //System.setErr(PrintStream(FileOutputStream("err.txt", true)))
        */




        //val storage = ExternalStorage.fromString("Hi")
        //val clazz = WindowClass.fromStorage(storage)
        //WindowManager.createWindow(WindowManager.WS_EX_OVERLAPPEDWINDOW, clazz, "Hello", style = WindowManager.WS_OVERLAPPEDWINDOW, width = 100, height = 100)
        //streamingMicRecognize()

        /*
    runBlocking {
        println(LIQUIPEDIA.player(listOf(Liquipedia.Wiki.VALORANT)))
    }
    */

    }

    private fun createOverlay(){
        val processes = Callback.getProcesses()
        println("hi")
    }

    private fun pluginThingy() {

        val selected = Callback.getProcesses().find { "Notepad.exe" in it.exeLocation }!!
        val clazz = WindowClass.define("Kats")

        val window = WindowManager.createWindow(
            WindowManager.WS_EX_TOPMOST or WindowManager.WS_EX_TRANSPARENT or WindowManager.WS_EX_LAYERED,
            clazz = clazz,
            windowName = "LiquidOverlay",
            style = WindowManager.WS_POPUP.toInt(),
            x = selected.rect.left.toInt(),
            y = selected.rect.top.toInt(),
            width = selected.rect.width.toInt(),
            height = selected.rect.height.toInt()
        )

        val selectedWindow = selected.asWindow()

        val overlay = OverlayImpl(window, selectedWindow)

        val pluginModule = PluginModule(Path("Plugins"), overlay).apply { enable() }

        overlay.onRedraw = {

            /*

            val bufferedImage = BufferedImage(it.canvasWidth, it.canvasHeight, BufferedImage.TYPE_INT_RGB)
            val graphics = bufferedImage.createGraphics()

            graphics.color = java.awt.Color(0, 100, 0)
            graphics.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

            it.image(bufferedImage, Overlay.Position(0, 0), it.canvasWidth, it.canvasHeight)

            graphics.dispose()
            */
            pluginModule.redraw()

        }


        window.doLoop()

    }

}

fun processFrame(buffer: ByteArray, amountOfSamples: Long, format: FormatData) {
    val result = AudioChannels.fromOther(buffer, amountOfSamples, SpeechToText.getInternalFormat(), format)
    println(result)
    /*
    val data: AudioChannels = when(format.bitsPerChannel.toInt()) {
        8 -> {
            PCMByteAudioChannels.process(buffer, format)
        }
        16 -> {
            PCMShortAudioChannels.process(buffer, format)
        }
        32 -> {
            if (format.tag == FormatFlag.IEEE_FLOAT) {
                FloatAudioChannels.process(buffer, format)
            } else {
                PCMIntAudioChannels.process(buffer, format)
            }
        }
        else -> {
            error("Unknown format $format")
        }
    }*/
    //process speech

    //end process speech
}

fun streamingSpeakerRecognize() {
    val state = Callback.newState()
    Callback.startRecording(state)
    val hnsPeriod = MemoryAccess.getDoubleAtOffset(state.segment, state[3])
    val sleepTime = ((hnsPeriod / 10_000.0) / 2.0).toLong()
    var counter = 0
    val format = tech.poder.overlay.audio.AudioFormat.getFormatData(Callback.getFormat(state))
    println(tech.poder.overlay.audio.AudioFormat.getFormat(format))
    val bytesPerFrame = format.blockAlignment
    val framesPerSecond = format.sampleRate
    val bytesPerSecond = bytesPerFrame * framesPerSecond.toLong()
    if (bytesPerSecond.toInt().toLong() != bytesPerSecond) {
        error("Bytes per second is too big")
    }

    while (counter < 30) {
        println("at top of loop")
        // Sleep for half the buffer duration.
        Thread.sleep(sleepTime)
        counter++
        Callback.getNextPacketSize(state)
        var packetLength = Callback.getPNumFramesInPacket(state)
        while (packetLength != 0u) {
            Callback.getBuffer(state)

            val flags = Callback.getPFlags(state)
            if (flags and Callback.AUDCLNT_BUFFERFLAGS_SILENT != 0.toByte()) {
                println("SILENT")
            }
            val amountOfFramesInBuffer = Callback.getPNumFramesInPacket(state)
            val pDataLocation = Callback.getPData(state, amountOfFramesInBuffer.toLong() * bytesPerFrame.toLong())
            var currentPos = 0L
            while (currentPos < pDataLocation.byteSize()) {
                val buffer =
                    pDataLocation.asSlice(currentPos, min(bytesPerSecond, pDataLocation.byteSize() - currentPos))
                processFrame(buffer.toByteArray(), amountOfFramesInBuffer.toLong(), format)
                currentPos += buffer.byteSize()
            }
            Callback.releaseBuffer(state)
            Callback.getNextPacketSize(state)
            packetLength = Callback.getPNumFramesInPacket(state)
        }
    }

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
        val mixer = AudioSystem.getMixer(AudioSystem.getMixerInfo().filter { it.name.contains("Port GNV32DB-DP") }.first())
        if (!mixer.isOpen) {
            mixer.open()
        }
        val targetInfo = DataLine.Info(
            TargetDataLine::class.java, audioFormat
        )

        val targetDataLine = mixer.getLine(targetInfo) as TargetDataLine

        // Set the system information to read from the microphone audio stream
        if (!AudioSystem.isLineSupported(targetInfo)) {
            println("Microphone not supported")
            exitProcess(0)
        }

        // Target data line captures the audio stream the microphone produces.
        //val targetDataLine = AudioSystem.getLine(targetInfo) as TargetDataLine
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