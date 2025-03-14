package ai.elimu.vitabu

import ai.elimu.vitabu.databinding.ActivityMainBinding
import ai.elimu.vitabu.ui.StoryBooksActivity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        if (isContentProviderInstalled()) {
            val intent = Intent(this, StoryBooksActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isContentProviderInstalled(): Boolean {
        try {
            val packageInfoAppstore: PackageInfo =
                packageManager.getPackageInfo(BuildConfig.CONTENT_PROVIDER_APPLICATION_ID, 0)
            Log.i(TAG, "packageInfoAppstore.versionCode: " + packageInfoAppstore.versionCode)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG,"getPackageInfo exception: " + e.message)
            AlertDialog.Builder(this)
                .setMessage(resources.getString(R.string.content_provider_needed))
                .setPositiveButton(getString(R.string.install)
                ) { _, _ ->
                    val openProviderIntent = Intent().apply {
                        setClassName(BuildConfig.APPSTORE_APPLICATION_ID,
                            "ai.elimu.appstore.MainActivity")
                    }
                    try {
                        startActivity(openProviderIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e(TAG, "startActivity exception: " + e.message)
                    }
                }
                .setCancelable(false)
                .create().show()
            return false
        }
    }
}