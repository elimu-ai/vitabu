package ai.elimu.vitabu.util;

import android.database.Cursor;

import ai.elimu.model.gson.content.StoryBookGson;

public class CursorToStoryBookGsonConverter {

    public static StoryBookGson getStoryBook(Cursor cursor) {
        int columnId = cursor.getColumnIndex("_id");
        Long id = cursor.getLong(columnId);

        int columnTitle = cursor.getColumnIndex("TITLE");
        String title = cursor.getString(columnTitle);

        int columnDescription = cursor.getColumnIndex("DESCRIPTION");
        String description = cursor.getString(columnDescription);

        StoryBookGson storyBook = new StoryBookGson();
        storyBook.setId(id);
        storyBook.setTitle(title);
        storyBook.setDescription(description);

        return storyBook;
    }
}
