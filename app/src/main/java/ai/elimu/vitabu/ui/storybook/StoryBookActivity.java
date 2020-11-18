package ai.elimu.vitabu.ui.storybook;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import ai.elimu.content_provider.utils.ContentProviderHelper;
import ai.elimu.model.v2.gson.content.StoryBookChapterGson;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;
import ai.elimu.vitabu.ui.viewpager.ZoomOutPageTransformer;

public class StoryBookActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_STORYBOOK_ID = "EXTRA_KEY_STORYBOOK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_storybook);

        Long storyBookId = getIntent().getLongExtra(EXTRA_KEY_STORYBOOK_ID, 0);
        Log.i(getClass().getName(), "storyBookId: " + storyBookId);

        // Fetch StoryBookChapters from the elimu.ai Content Provider (see https://github.com/elimu-ai/content-provider)
        List<StoryBookChapterGson> storyBookChapters = ContentProviderHelper.getStoryBookChapterGsons(storyBookId, getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);

        ViewPager viewPager = findViewById(R.id.view_pager);

        ChapterPagerAdapter chapterPagerAdapter = new ChapterPagerAdapter(getSupportFragmentManager(), this, storyBookChapters);
        viewPager.setAdapter(chapterPagerAdapter);

        ZoomOutPageTransformer zoomOutPageTransformer = new ZoomOutPageTransformer();
        viewPager.setPageTransformer(true, zoomOutPageTransformer);
    }
}
