package ai.elimu.vitabu.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

import ai.elimu.model.gson.v2.content.ImageGson;
import ai.elimu.model.gson.v2.content.StoryBookChapterGson;
import ai.elimu.vitabu.BuildConfig;

public class CursorToStoryBookChapterGsonConverter {

    public static StoryBookChapterGson getStoryBookChapter(Cursor cursor, Context context) {
        Log.i(CursorToStoryBookChapterGsonConverter.class.getName(), "getStoryBookChapter");

        Log.i(CursorToStoryBookChapterGsonConverter.class.getName(), "Arrays.toString(cursor.getColumnNames()): " + Arrays.toString(cursor.getColumnNames()));

        int columnId = cursor.getColumnIndex("id");
        Long id = cursor.getLong(columnId);
        Log.i(CursorToStoryBookChapterGsonConverter.class.getName(), "id: " + id);

        int columnSortOrder = cursor.getColumnIndex("sortOrder");
        Integer sortOrder = cursor.getInt(columnSortOrder);
        Log.i(CursorToStoryBookChapterGsonConverter.class.getName(), "sortOrder: " + sortOrder);

        int columnImageId = cursor.getColumnIndex("imageId");
        Long imageId = cursor.getLong(columnImageId);
        Log.i(CursorToImageGsonConverter.class.getName(), "imageId: " + imageId);
        ImageGson image = null;
        if (imageId != null) {
            Uri uri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.image_provider/images/" + imageId);
            Log.i(CursorToImageGsonConverter.class.getName(), "uri: " + uri);
            Cursor imageCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (imageCursor == null) {
                Log.e(CursorToImageGsonConverter.class.getName(), "imageCursor == null");
                Toast.makeText(context, "imageCursor == null", Toast.LENGTH_LONG).show();
            } else {
                Log.i(CursorToImageGsonConverter.class.getName(), "imageCursor.getCount(): " + imageCursor.getCount());
                if (imageCursor.getCount() == 0) {
                    Log.e(CursorToImageGsonConverter.class.getName(), "imageCursor.getCount() == 0");
                } else {
                    Log.i(CursorToImageGsonConverter.class.getName(), "imageCursor.getCount(): " + imageCursor.getCount());

                    imageCursor.moveToFirst();

                    // Convert from Room to Gson
                    image = CursorToImageGsonConverter.getImage(imageCursor);

                    imageCursor.close();
                    Log.i(CursorToImageGsonConverter.class.getName(), "cursor.isClosed(): " + imageCursor.isClosed());
                }
            }
        }

        // TODO: paragraphs

        StoryBookChapterGson storyBookChapter = new StoryBookChapterGson();
        storyBookChapter.setId(id);
        storyBookChapter.setSortOrder(sortOrder);
        storyBookChapter.setImage(image);
//        storyBookChapter.setStoryBookParagraphs(TODO);

        return storyBookChapter;
    }
}
