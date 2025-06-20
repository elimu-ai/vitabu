package ai.elimu.vitabu.ui.storybook

import ai.elimu.common.utils.getParcelableCompat
import ai.elimu.content_provider.utils.ContentProviderUtil
import ai.elimu.model.v2.enums.ReadingLevel
import ai.elimu.model.v2.gson.content.StoryBookChapterGson
import ai.elimu.vitabu.BuildConfig
import ai.elimu.vitabu.databinding.ActivityStorybookBinding
import ai.elimu.vitabu.ui.BookCompletedActivity
import ai.elimu.vitabu.ui.viewpager.ZoomOutPageTransformer
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryBookActivity : AppCompatActivity() {

    private val TAG = javaClass.name
    private lateinit var binding: ActivityStorybookBinding
    private var startReadingTime = SystemClock.elapsedRealtime()
    private val timeSpent by lazy { mutableMapOf<Int, Int>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        binding = ActivityStorybookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyBookId = intent.getLongExtra(EXTRA_KEY_STORYBOOK_ID, 0)
        Log.i(TAG, "storyBookId: $storyBookId")
        val readingLevel = intent.extras?.getParcelableCompat(
            EXTRA_KEY_STORYBOOK_LEVEL, ReadingLevel::class.java
        )
        val description = intent.getStringExtra(EXTRA_KEY_STORYBOOK_DESCRIPTION) ?: ""
        
        // Fetch StoryBookChapters from the elimu.ai Content Provider (see https://github.com/elimu-ai/content-provider)
        val storyBookChapters = ContentProviderUtil.getStoryBookChapterGsons(
            storyBookId,
            applicationContext, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
        ).toMutableList().apply {

            // Add an empty page at the end of this book for the purpose of
            // analytics tracking
            add(StoryBookChapterGson())
        }.toList()

        val chapterPagerAdapter = ChapterPagerAdapter(
            supportFragmentManager,
            storyBookChapters,
            readingLevel ?: ReadingLevel.LEVEL1,
            description
        )
        binding.viewPager.adapter = chapterPagerAdapter

        val zoomOutPageTransformer = ZoomOutPageTransformer()
        binding.viewPager.setPageTransformer(true, zoomOutPageTransformer)
        binding.viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position > 1) {
                    val timeSpentOnPage = ((SystemClock.elapsedRealtime() - startReadingTime) / 1000).toInt()

                    // Check if this page has been read. If it's the first time being read, just record
                    // the time.
                    // Otherwise, if we find a record of time spent on this page, and new time spent is
                    // smaller than recorded one, the page is likely being skipped, so we should ignore it
                    if (timeSpentOnPage > timeSpent.getOrDefault(position, -1)) {
                        timeSpent[position] = timeSpentOnPage
                    }
                }

                startReadingTime = SystemClock.elapsedRealtime()
                if (position == storyBookChapters.size - 1) {
                    startActivity(Intent(this@StoryBookActivity, BookCompletedActivity::class.java)
                        .apply {
                            putExtra(EXTRA_KEY_STORYBOOK_ID, storyBookId)
                            putIntegerArrayListExtra(
                                EXTRA_KEY_TIME_SPENT, timeSpent.toSortedMap().values.toCollection(ArrayList())
                            )
                        }
                    )
                    finish()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
    }

    companion object {
        const val EXTRA_KEY_STORYBOOK_ID: String = "EXTRA_KEY_STORYBOOK_ID"
        const val EXTRA_KEY_STORYBOOK_LEVEL: String = "EXTRA_KEY_STORYBOOK_LEVEL"
        const val EXTRA_KEY_STORYBOOK_DESCRIPTION: String = "EXTRA_KEY_STORYBOOK_DESCRIPTION"
        const val EXTRA_KEY_TIME_SPENT = "extra_key_time_spent"
    }
}
