package ai.elimu.vitabu.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ai.elimu.model.gson.content.StoryBookGson;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;
import ai.elimu.vitabu.ui.storybook.StoryBookActivity;
import ai.elimu.vitabu.util.CursorToStoryBookGsonConverter;

public class StoryBooksActivity extends AppCompatActivity {

    private GridLayout storyBooksGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_storybooks);

        storyBooksGridLayout = findViewById(R.id.storyBooksGridLayout);
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        // Fetch StoryBooks from the elimu.ai Content Provider (see https://github.com/elimu-ai/content-provider)
        List<StoryBookGson> storyBooks = new ArrayList<>();
        Uri uri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.storybook_provider/storybook");
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

                    // Convert from database row to StoryBookGson object
                    StoryBookGson storyBook = CursorToStoryBookGsonConverter.getStoryBook(cursor);

                    storyBooks.add(storyBook);

                    isLast = cursor.isLast();
                }
                Log.i(getClass().getName(), "cursor.isClosed(): " + cursor.isClosed());
                cursor.close();
            }
        }
        Log.i(getClass().getName(), "storyBooks.size(): " + storyBooks.size());

        for (final StoryBookGson storyBook : storyBooks) {
            Log.i(getClass().getName(), "storyBook.getTitle(): \"" + storyBook.getTitle() + "\"");
            Log.i(getClass().getName(), "storyBook.getDescription(): \"" + storyBook.getDescription() + "\"");

            View storyBookView = LayoutInflater.from(this).inflate(R.layout.activity_storybooks_cover_view, storyBooksGridLayout, false);

//            File storyBookCoverFile = MultimediaHelper.getFile(storyBook.getCoverImage());
//            Log.i(getClass().getName(), "storyBookCoverFile: " + storyBookCoverFile);
//            if (storyBookCoverFile.exists()) {
//                ImageView storyBookImageView = (ImageView) storyBookView.findViewById(R.id.storyBookCoverImageView);
//                Bitmap bitmap = BitmapFactory.decodeFile(storyBookCoverFile.getAbsolutePath());
//                storyBookImageView.setImageBitmap(bitmap);
//            }

            TextView storyBookCoverTitleTextView = storyBookView.findViewById(R.id.storyBookCoverTitleTextView);
            storyBookCoverTitleTextView.setText(storyBook.getTitle());

            storyBookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(getClass().getName(), "onClick");

                    Log.i(getClass().getName(), "storyBook.getId(): " + storyBook.getId());
                    Log.i(getClass().getName(), "storyBook.getTitle(): " + storyBook.getTitle());

                    // TODO: StoryBookLearningEvent

                    Intent intent = new Intent(getApplicationContext(), StoryBookActivity.class);
                    intent.putExtra(StoryBookActivity.EXTRA_KEY_STORYBOOK_ID, storyBook.getId());
                    startActivity(intent);
                }
            });

            storyBooksGridLayout.addView(storyBookView);
        }
    }
}
