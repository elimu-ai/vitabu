package ai.elimu.vitabu.util;

import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;

import ai.elimu.model.gson.v2.content.ImageGson;
import ai.elimu.model.gson.v2.content.StoryBookChapterGson;

public class CursorToStoryBookChapterGsonConverter {

    public static StoryBookChapterGson getStoryBookChapter(Cursor cursor) {
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
        ImageGson image = new ImageGson();
        image.setId(imageId);

        // TODO: paragraphs

        StoryBookChapterGson storyBookChapter = new StoryBookChapterGson();
        storyBookChapter.setId(id);
        storyBookChapter.setSortOrder(sortOrder);
        storyBookChapter.setImage(image);
//        storyBookChapter.setStoryBookParagraphs(TODO);

        return storyBookChapter;
    }
}
