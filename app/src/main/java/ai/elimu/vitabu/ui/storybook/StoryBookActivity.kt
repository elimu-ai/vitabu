package ai.elimu.vitabu.ui.storybook

import ai.elimu.content_provider.utils.ContentProviderUtil
import ai.elimu.model.v2.enums.ReadingLevel
import ai.elimu.vitabu.BuildConfig
import ai.elimu.vitabu.R
import ai.elimu.vitabu.ui.viewpager.ZoomOutPageTransformer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryBookActivity : AppCompatActivity() {

    private val TAG = javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_storybook)

        val storyBookId = intent.getLongExtra(EXTRA_KEY_STORYBOOK_ID, 0)
        Log.i(TAG, "storyBookId: $storyBookId")
        val readingLevel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_KEY_STORYBOOK_LEVEL, ReadingLevel::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_KEY_STORYBOOK_LEVEL)
        }
        val description = intent.getStringExtra(EXTRA_KEY_STORYBOOK_DESCRIPTION) ?: ""
        
        // Fetch StoryBookChapters from the elimu.ai Content Provider (see https://github.com/elimu-ai/content-provider)
        val storyBookChapters = ContentProviderUtil.getStoryBookChapterGsons(
            storyBookId,
            applicationContext, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
        )

        val viewPager = findViewById<ViewPager>(R.id.view_pager)

        val chapterPagerAdapter = ChapterPagerAdapter(
            supportFragmentManager,
            storyBookChapters,
            readingLevel ?: ReadingLevel.LEVEL1,
            description
        )
        viewPager.adapter = chapterPagerAdapter

        val zoomOutPageTransformer = ZoomOutPageTransformer()
        viewPager.setPageTransformer(true, zoomOutPageTransformer)
    }

    companion object {
        const val EXTRA_KEY_STORYBOOK_ID: String = "EXTRA_KEY_STORYBOOK_ID"
        const val EXTRA_KEY_STORYBOOK_LEVEL: String = "EXTRA_KEY_STORYBOOK_LEVEL"
        const val EXTRA_KEY_STORYBOOK_DESCRIPTION: String = "EXTRA_KEY_STORYBOOK_DESCRIPTION"
    }
}
