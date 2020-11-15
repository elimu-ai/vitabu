package ai.elimu.vitabu;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BaseApplication extends Application {

    Executor executorService = Executors.newSingleThreadExecutor();

    private TextToSpeech tts;

    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate();

        // Initialize the Text-to-Speech (TTS) engine
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i(getClass().getName(), "onInit");

                // Fetch the chosen language from the Appstore
                // TODO
//                tts.setLanguage(new Locale("hin"));
            }
        });
    }

    public TextToSpeech getTTS() {
        return tts;
    }

    public Executor getExecutor() {
        return executorService;
    }
}
