package ai.elimu.vitabu

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log

class BaseApplication : Application() {

    private val TAG = javaClass.name

    var tts: TextToSpeech? = null
        private set

    override fun onCreate() {
        Log.i(javaClass.name, "onCreate")
        super.onCreate()

        // Initialize the Text-to-Speech (TTS) engine
        tts = TextToSpeech(applicationContext, object : OnInitListener {
            override fun onInit(status: Int) {
                Log.i(TAG, "onInit with status: $status")

                // Fetch the chosen language from the Appstore
                // TODO
//                tts.setLanguage(new Locale("hin"));
                if (status == TextToSpeech.SUCCESS) {
                    tts?.setSpeechRate(SPEECH_RATE)
                } else {
                    Log.e(TAG, "TTS initialization failed with status: $status")
                }

            }
        })
    }

    companion object {
        const val SPEECH_RATE: Float = 0.5f
    }
}
