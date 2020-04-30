package ai.elimu.vitabu.util;

import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;

import ai.elimu.model.v2.gson.content.ImageGson;
import ai.elimu.model.v2.gson.content.StoryBookGson;

public class CursorToStoryBookGsonConverter {

    public static StoryBookGson getStoryBookGson(Cursor cursor) {
        Log.i(CursorToStoryBookGsonConverter.class.getName(), "getStoryBookGson");

        Log.i(CursorToStoryBookGsonConverter.class.getName(), "Arrays.toString(cursor.getColumnNames()): " + Arrays.toString(cursor.getColumnNames()));

        int columnId = cursor.getColumnIndex("id");
        Long id = cursor.getLong(columnId);
        Log.i(CursorToStoryBookGsonConverter.class.getName(), "id: " + id);

        int columnTitle = cursor.getColumnIndex("title");
        String title = cursor.getString(columnTitle);
        Log.i(CursorToStoryBookGsonConverter.class.getName(), "title: \"" + title + "\"");

        int columnDescription = cursor.getColumnIndex("description");
        String description = cursor.getString(columnDescription);
        Log.i(CursorToStoryBookGsonConverter.class.getName(), "description: \"" + description + "\"");

        int columnCoverImageId = cursor.getColumnIndex("coverImageId");
        Long coverImageId = cursor.getLong(columnCoverImageId);
        Log.i(CursorToImageGsonConverter.class.getName(), "coverImageId: " + coverImageId);
        ImageGson coverImage = new ImageGson();
        coverImage.setId(coverImageId);

        StoryBookGson storyBook = new StoryBookGson();
        storyBook.setId(id);
        storyBook.setTitle(title);
        storyBook.setDescription(description);
        storyBook.setCoverImage(coverImage);

        return storyBook;
    }
}
