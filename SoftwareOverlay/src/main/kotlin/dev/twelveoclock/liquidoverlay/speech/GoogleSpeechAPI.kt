package dev.twelveoclock.liquidoverlay.speech

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GoogleSpeechAPI(lang: String = "en-US", rate: Int = 16000) {

    private val requestBuilder = HttpRequest.newBuilder(URI("https://www.google.com/speech-api/v2/recognize?client=chromium&lang=$lang&key=AIzaSyDu4IO4v_XZf9Z9pAmiLOCyf01W-Q7k2pQ")).setHeader("Content-Type", "audio/l16; rate=$rate")// "audio/x-flac; rate=16000")


    fun getSpeech(data: ByteArray): String {

        val body = HttpRequest.BodyPublishers.ofByteArray(data)
        val request = requestBuilder.POST(body).build()
        val result = client.send(request, HttpResponse.BodyHandlers.ofString())

        check(result.statusCode() == 200) {
            "Google Speech API returned status code ${result.statusCode()}"
        }

        return result.body()
    }

    companion object {

        private val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build()

    }

}
