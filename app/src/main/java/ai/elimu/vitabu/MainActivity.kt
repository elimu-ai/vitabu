package ai.elimu.vitabu

import ai.elimu.common.utils.checkIfAppstoreIsInstalled
import ai.elimu.common.utils.isPackageInstalled
import ai.elimu.vitabu.databinding.ActivityMainBinding
import ai.elimu.vitabu.ui.StoryBooksActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        if (!checkIfAppstoreIsInstalled(BuildConfig.APPSTORE_APPLICATION_ID)) return

        if (isPackageInstalled(
                packageName = BuildConfig.CONTENT_PROVIDER_APPLICATION_ID,
                launchPackage = BuildConfig.APPSTORE_APPLICATION_ID,
                launchClass = "ai.elimu.appstore.MainActivity",
                dialogMessage = resources.getString(R.string.content_provider_needed),
                buttonText = getString(R.string.install))) {

            val intent = Intent(this, StoryBooksActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
