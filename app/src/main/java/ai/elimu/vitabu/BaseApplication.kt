package ai.elimu.vitabu

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log

class BaseApplication : Application() {

    var tts: TextToSpeech? = null
        private set

    override fun onCreate() {
        Log.i(javaClass.name, "onCreate")
        super.onCreate()

        // Initialize the Text-to-Speech (TTS) engine
        tts = TextToSpeech(applicationContext, object : OnInitListener {
            override fun onInit(status: Int) {
                Log.i(javaClass.name, "onInit")

                // Fetch the chosen language from the Appstore
                // TODO
//                tts.setLanguage(new Locale("hin"));
                tts!!.setSpeechRate(SPEECH_RATE)
            }
        })
    }

    companion object {
        const val SPEECH_RATE: Float = 0.5f
    }
}
