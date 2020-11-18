package ai.elimu.vitabu.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;

import ai.elimu.content_provider.utils.ContentProviderHelper;
import ai.elimu.model.enums.analytics.LearningEventType;
import ai.elimu.model.enums.content.ImageFormat;
import ai.elimu.model.v2.gson.content.ImageGson;
import ai.elimu.model.v2.gson.content.StoryBookGson;
import ai.elimu.vitabu.BaseApplication;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;
import ai.elimu.vitabu.ui.storybook.StoryBookActivity;
import ai.elimu.vitabu.util.SingleClickListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class StoryBooksActivity extends AppCompatActivity {

    private GridLayout storyBooksGridLayout;
    private ProgressBar storyBooksProgressBar;

    private List<StoryBookGson> storyBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_storybooks);

        storyBooksGridLayout = findViewById(R.id.storyBooksGridLayout);
        storyBooksProgressBar = findViewById(R.id.storybooks_progress_bar);

        // Fetch StoryBooks from the elimu.ai Content Provider (see https://github.com/elimu-ai/content-provider)
        storyBooks = ContentProviderHelper.getStoryBookGsons(getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
        Log.i(getClass().getName(), "storyBooks.size(): " + storyBooks.size());
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        // Reset the state of the GridLayout
        storyBooksProgressBar.setVisibility(View.VISIBLE);
        storyBooksGridLayout.setVisibility(View.GONE);
        storyBooksGridLayout.removeAllViews();

        ((BaseApplication) getApplication()).getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // Create a View for each StoryBook in the list
                for (final StoryBookGson storyBook : storyBooks) {
                    Log.i(getClass().getName(), "storyBook.getId(): " + storyBook.getId());
                    Log.i(getClass().getName(), "storyBook.getTitle(): \"" + storyBook.getTitle() + "\"");
                    Log.i(getClass().getName(), "storyBook.getDescription(): \"" + storyBook.getDescription() + "\"");

                    final View storyBookView = LayoutInflater.from(StoryBooksActivity.this).inflate(R.layout.activity_storybooks_cover_view, storyBooksGridLayout, false);

                    // Fetch Image from the elimu.ai Content Provider (see https://github.com/elimu-ai/content-provider)
                    Log.i(getClass().getName(), "storyBook.getCoverImage(): " + storyBook.getCoverImage());
                    ImageGson coverImageGson = ContentProviderHelper.getImageGson(storyBook.getCoverImage().getId(), getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
                    final GifImageView storyBookImageView = storyBookView.findViewById(R.id.storyBookCoverImageView);
                    byte[] imageBytes = coverImageGson.getBytes();
                    if (coverImageGson.getImageFormat() == ImageFormat.GIF) {
                        try {
                            final GifDrawable gifDrawable = new GifDrawable(imageBytes);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    storyBookImageView.setImageDrawable(gifDrawable);
                                }
                            });
                        } catch (IOException e) {
                            Log.e(getClass().getName(), null, e);
                        }
                    } else {
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                storyBookImageView.setImageBitmap(bitmap);
                            }
                        });
                    }

                    TextView storyBookCoverTitleTextView = storyBookView.findViewById(R.id.storyBookCoverTitleTextView);
                    storyBookCoverTitleTextView.setText(storyBook.getTitle());

                    storyBookView.setOnClickListener(new SingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                        Log.i(getClass().getName(), "onClick");

                        Log.i(getClass().getName(), "storyBook.getId(): " + storyBook.getId());
                        Log.i(getClass().getName(), "storyBook.getTitle(): " + storyBook.getTitle());

                            // Report StoryBookLearningEvent to the Analytics application
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setPackage(BuildConfig.ANALYTICS_APPLICATION_ID);
                            broadcastIntent.setAction("ai.elimu.intent.action.STORYBOOK_LEARNING_EVENT");
                            broadcastIntent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                            broadcastIntent.putExtra("storyBookId", storyBook.getId());
                            broadcastIntent.putExtra("learningEventType", LearningEventType.STORYBOOK_OPENED.toString());
                            getApplicationContext().sendBroadcast(broadcastIntent);

                            Intent intent = new Intent(getApplicationContext(), StoryBookActivity.class);
                            intent.putExtra(StoryBookActivity.EXTRA_KEY_STORYBOOK_ID, storyBook.getId());
                            startActivity(intent);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            storyBooksGridLayout.addView(storyBookView);
                            if (storyBooksGridLayout.getChildCount() == storyBooks.size()) {
                                storyBooksProgressBar.setVisibility(View.GONE);
                                storyBooksGridLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }
}