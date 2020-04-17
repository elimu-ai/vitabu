package ai.elimu.vitabu.util;

import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;

import ai.elimu.model.gson.v2.content.StoryBookParagraphGson;

public class CursorToStoryBookParagraphGsonConverter {

    public static StoryBookParagraphGson getStoryBookParagraphGson(Cursor cursor) {
        Log.i(CursorToStoryBookParagraphGsonConverter.class.getName(), "getStoryBookParagraphGson");

        Log.i(CursorToStoryBookParagraphGsonConverter.class.getName(), "Arrays.toString(cursor.getColumnNames()): " + Arrays.toString(cursor.getColumnNames()));

        int columnId = cursor.getColumnIndex("id");
        Long id = cursor.getLong(columnId);
        Log.i(CursorToStoryBookParagraphGsonConverter.class.getName(), "id: " + id);

        int columnSortOrder = cursor.getColumnIndex("sortOrder");
        Integer sortOrder = cursor.getInt(columnSortOrder);
        Log.i(CursorToStoryBookParagraphGsonConverter.class.getName(), "sortOrder: " + sortOrder);

        int columnOriginalText = cursor.getColumnIndex("originalText");
        String originalText = cursor.getString(columnOriginalText);
        Log.i(CursorToStoryBookParagraphGsonConverter.class.getName(), "originalText: " + originalText);

        StoryBookParagraphGson storyBookParagraph = new StoryBookParagraphGson();
        storyBookParagraph.setId(id);
        storyBookParagraph.setSortOrder(sortOrder);
        storyBookParagraph.setOriginalText(originalText);

        return storyBookParagraph;
    }
}
