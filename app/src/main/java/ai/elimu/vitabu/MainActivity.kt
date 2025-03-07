package ai.elimu.vitabu

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

        // Verify that the content-provider APK has been installed
        // TODO
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        val intent = Intent(this, StoryBooksActivity::class.java)
        startActivity(intent)

        finish()
    }
}