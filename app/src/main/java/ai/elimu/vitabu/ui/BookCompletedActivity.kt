package ai.elimu.vitabu.ui

import ai.elimu.analytics.utils.LearningEventUtil
import ai.elimu.content_provider.utils.ContentProviderUtil
import ai.elimu.model.v2.enums.analytics.LearningEventType
import ai.elimu.vitabu.BuildConfig
import ai.elimu.vitabu.databinding.ActivityBookCompletedBinding
import ai.elimu.vitabu.ui.storybook.StoryBookActivity.Companion.EXTRA_KEY_STORYBOOK_ID
import ai.elimu.vitabu.ui.storybook.StoryBookActivity.Companion.EXTRA_KEY_TIME_SPENT
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class BookCompletedActivity : AppCompatActivity() {

    private val TAG = "BookCompletedActivity"
    private lateinit var binding: ActivityBookCompletedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        binding = ActivityBookCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            val storyBookId = intent.getLongExtra(EXTRA_KEY_STORYBOOK_ID, 0)
            val completedStoryBook = ContentProviderUtil.getStoryBookGson(
                storyBookId,
                applicationContext, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
            ) ?: return@launch

            withContext(Dispatchers.Main) {
                LearningEventUtil.reportStoryBookLearningEvent(
                    storyBookGson = completedStoryBook,
                    context = applicationContext,
                    additionalData = JSONObject().apply {
                        put("eventType", LearningEventType.STORYBOOK_COMPLETED)
                        put("seconds_spent_per_chapter", intent.getIntegerArrayListExtra(EXTRA_KEY_TIME_SPENT))
                    },
                    analyticsApplicationId = BuildConfig.ANALYTICS_APPLICATION_ID
                )

                binding.btnStar.postDelayed({
                    binding.btnStar.callOnClick()
                }, 500L)

                delay(2000L)
                finish()
            }
        }
    }
}
