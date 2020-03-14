package ai.elimu.vitabu.util;

import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;

import ai.elimu.model.gson.content.StoryBookGson;

public class CursorToStoryBookGsonConverter {

    public static StoryBookGson getStoryBook(Cursor cursor) {
        Log.i(CursorToStoryBookGsonConverter.class.getName(), "getStoryBook");

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

        StoryBookGson storyBook = new StoryBookGson();
        storyBook.setId(id);
        storyBook.setTitle(title);
        storyBook.setDescription(description);

        return storyBook;
    }
}
