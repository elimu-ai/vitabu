package ai.elimu.vitabu.util;

import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;

import ai.elimu.model.v2.gson.content.WordGson;

public class CursorToWordGsonConverter {

    public static WordGson getWordGson(Cursor cursor) {
        Log.i(CursorToWordGsonConverter.class.getName(), "getWordGson");

        Log.i(CursorToWordGsonConverter.class.getName(), "Arrays.toString(cursor.getColumnNames()): " + Arrays.toString(cursor.getColumnNames()));

        int columnId = cursor.getColumnIndex("id");
        Long id = cursor.getLong(columnId);
        Log.i(CursorToWordGsonConverter.class.getName(), "id: " + id);

        int columnRevisionNumber = cursor.getColumnIndex("revisionNumber");
        Integer revisionNumber = cursor.getInt(columnRevisionNumber);
        Log.i(CursorToWordGsonConverter.class.getName(), "revisionNumber: " + revisionNumber);

        int columnText = cursor.getColumnIndex("text");
        String text = cursor.getString(columnText);
        Log.i(CursorToWordGsonConverter.class.getName(), "text: \"" + text + "\"");

        WordGson word = new WordGson();
        word.setId(id);
        word.setRevisionNumber(revisionNumber);
        word.setText(text);

        return word;
    }
}
