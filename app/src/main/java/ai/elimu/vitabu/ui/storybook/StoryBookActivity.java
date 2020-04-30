package ai.elimu.vitabu.ui.storybook;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ai.elimu.model.v2.gson.content.StoryBookChapterGson;
import ai.elimu.model.v2.gson.content.StoryBookParagraphGson;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;
import ai.elimu.vitabu.util.CursorToStoryBookChapterGsonConverter;

public class StoryBookActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_STORYBOOK_ID = "EXTRA_KEY_STORYBOOK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_storybook);

        Long storyBookId = getIntent().getLongExtra(EXTRA_KEY_STORYBOOK_ID, 0);
        Log.i(getClass().getName(), "storyBookId: " + storyBookId);

        List<StoryBookChapterGson> storyBookChapters = new ArrayList<>();

        // Prepend cover image and book title as its own chapter
        StoryBookChapterGson coverChapterGson = new StoryBookChapterGson();
//        coverChapterGson.setImage(TODO);
        List<StoryBookParagraphGson> coverParagraphGsons = new ArrayList<>();
        StoryBookParagraphGson coverTitleParagraphGson = new StoryBookParagraphGson();
        coverTitleParagraphGson.setOriginalText("Book title..."); // TODO
        coverParagraphGsons.add(coverTitleParagraphGson);
        coverChapterGson.setStoryBookParagraphs(coverParagraphGsons);
        storyBookChapters.add(coverChapterGson);

        // Fetch the StoryBook's chapters from the content provider
        Uri uri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.storybook_provider/storybooks/" + storyBookId + "/chapters");
        Log.i(getClass().getName(), "uri: " + uri);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            Log.e(getClass().getName(), "cursor == null");
            Toast.makeText(getApplicationContext(), "cursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(getClass().getName(), "cursor.getCount(): " + cursor.getCount());
            if (cursor.getCount() == 0) {
                Log.e(getClass().getName(), "cursor.getCount() == 0");
            } else {
                boolean isLast = false;
                while (!isLast) {
                    cursor.moveToNext();

                    // Convert from Cursor to Gson
                    StoryBookChapterGson storyBookChapter = CursorToStoryBookChapterGsonConverter.getStoryBookChapterGson(cursor, getApplicationContext());

                    storyBookChapters.add(storyBookChapter);

                    isLast = cursor.isLast();
                }
                cursor.close();
                Log.i(getClass().getName(), "cursor.isClosed(): " + cursor.isClosed());
            }
        }
        Log.i(getClass().getName(), "storyBookChapters.size(): " + storyBookChapters.size());

        ChapterPagerAdapter chapterPagerAdapter = new ChapterPagerAdapter(getSupportFragmentManager(), this, storyBookChapters);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(chapterPagerAdapter);
    }
}
