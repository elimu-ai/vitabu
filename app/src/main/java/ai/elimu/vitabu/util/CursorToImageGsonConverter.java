package ai.elimu.vitabu.util;

import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;

import ai.elimu.model.gson.content.multimedia.ImageGson;

public class CursorToImageGsonConverter {

    public static ImageGson getImage(Cursor cursor) {
        Log.i(CursorToImageGsonConverter.class.getName(), "getImage");

        Log.i(CursorToImageGsonConverter.class.getName(), "Arrays.toString(cursor.getColumnNames()): " + Arrays.toString(cursor.getColumnNames()));

        int columnId = cursor.getColumnIndex("id");
        Long id = cursor.getLong(columnId);
        Log.i(CursorToImageGsonConverter.class.getName(), "id: " + id);

        int columnRevisionNumber = cursor.getColumnIndex("revisionNumber");
        Integer revisionNumber = cursor.getInt(columnRevisionNumber);
        Log.i(CursorToImageGsonConverter.class.getName(), "revisionNumber: " + revisionNumber);

        int columnTitle = cursor.getColumnIndex("title");
        String title = cursor.getString(columnTitle);
        Log.i(CursorToImageGsonConverter.class.getName(), "title: \"" + title + "\"");

        ImageGson image = new ImageGson();
        image.setId(id);
        image.setRevisionNumber(revisionNumber);
        image.setTitle(title);

        return image;
    }
}
