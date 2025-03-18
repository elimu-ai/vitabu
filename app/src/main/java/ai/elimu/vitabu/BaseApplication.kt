package ai.elimu.vitabu

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        Log.i(javaClass.name, "onCreate")
        super.onCreate()
    }
}
