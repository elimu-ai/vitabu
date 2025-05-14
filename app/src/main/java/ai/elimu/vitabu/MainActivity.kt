package ai.elimu.vitabu

import ai.elimu.common.utils.ensurePackageInstalledOrPrompt
import ai.elimu.vitabu.ui.StoryBooksActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        setContent {
            VitabuTheme {
                MainScreen(
                    onPackageCheck = { hasPackage ->
                        if (hasPackage) {
                            val intent = Intent(this, StoryBooksActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun VitabuTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Composable
fun MainScreen(onPackageCheck: (Boolean) -> Unit) {
    val context = LocalContext.current
    
    // Empty Box as container - the UI is minimal since this is just a launcher activity
    Box(modifier = Modifier.fillMaxSize()) {
        // Check for required package when the composable is first launched
        LaunchedEffect(key1 = Unit) {
            val hasPackage = ensurePackageInstalledOrPrompt(
                packageName = BuildConfig.CONTENT_PROVIDER_APPLICATION_ID,
                launchPackage = BuildConfig.APPSTORE_APPLICATION_ID,
                launchClass = "ai.elimu.appstore.MainActivity",
                dialogMessage = context.resources.getString(R.string.content_provider_needed),
                buttonText = context.getString(R.string.install)
            )
            onPackageCheck(hasPackage)
        }
    }
}
