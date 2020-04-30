package ai.elimu.vitabu.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.elimu.model.v2.gson.content.StoryBookParagraphGson;
import ai.elimu.model.v2.gson.content.WordGson;
import ai.elimu.vitabu.BuildConfig;

public class CursorToStoryBookParagraphGsonConverter {

    public static StoryBookParagraphGson getStoryBookParagraphGson(Cursor cursor, Context context) {
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

        List<WordGson> wordGsons = null;
        Uri wordsUri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.word_provider/words/by-paragraph-id/" + id);
        Log.i(CursorToImageGsonConverter.class.getName(), "wordsUri: " + wordsUri);
        Cursor wordsCursor = context.getContentResolver().query(wordsUri, null, null, null, null);
        if (wordsCursor == null) {
            Log.e(CursorToImageGsonConverter.class.getName(), "wordsCursor == null");
            Toast.makeText(context, "wordsCursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(CursorToImageGsonConverter.class.getName(), "wordsCursor.getCount(): " + wordsCursor.getCount());
            if (wordsCursor.getCount() == 0) {
                Log.e(CursorToImageGsonConverter.class.getName(), "wordsCursor.getCount() == 0");
            } else {
                Log.i(CursorToImageGsonConverter.class.getName(), "wordsCursor.getCount(): " + wordsCursor.getCount());

                wordGsons = new ArrayList<>();

                boolean isLast = false;
                while (!isLast) {
                    wordsCursor.moveToNext();

                    // Convert from Room to Gson
                    WordGson wordGson = CursorToWordGsonConverter.getWordGson(wordsCursor);
                    wordGsons.add(wordGson);

                    isLast = wordsCursor.isLast();
                }

                wordsCursor.close();
                Log.i(CursorToImageGsonConverter.class.getName(), "wordsCursor.isClosed(): " + wordsCursor.isClosed());
            }
        }

        List<WordGson> wordGsonsWithNullObjects = null;
        if (wordGsons != null) {
            // Look for a Word match in the original text, and add null if none was found
            wordGsonsWithNullObjects = new ArrayList<>();
            String[] wordsInOriginalText = originalText.trim().split(" ");
            Log.i(CursorToImageGsonConverter.class.getName(), "wordsInOriginalText.length: " + wordsInOriginalText.length);
            Log.i(CursorToImageGsonConverter.class.getName(), "Arrays.toString(wordsInOriginalText): " + Arrays.toString(wordsInOriginalText));
            for (String wordInOriginalText : wordsInOriginalText) {
                Log.i(CursorToImageGsonConverter.class.getName(), "wordInOriginalText (before cleaning): \"" + wordInOriginalText + "\"");
                wordInOriginalText = wordInOriginalText
                        .replace(",", "")
                        .replace("\"", "")
                        .replace("“", "")
                        .replace("”", "")
                        .replace(".", "")
                        .replace("!", "")
                        .replace("?", "")
                        .replace(":", "")
                        .replace("(", "")
                        .replace(")", "");
                wordInOriginalText = wordInOriginalText.trim();
                wordInOriginalText = wordInOriginalText.toLowerCase();
                Log.i(CursorToImageGsonConverter.class.getName(), "wordInOriginalText (after cleaning): \"" + wordInOriginalText + "\"");

                WordGson wordGsonMatch = null;
                for (WordGson wordGson : wordGsons) {
                    Log.i(CursorToImageGsonConverter.class.getName(), "wordGson.getText(): \"" + wordGson.getText() + "\"");
                    if (wordGson.getText().equals(wordInOriginalText)) {
                        wordGsonMatch = wordGson;
                        break;
                    }
                }
                wordGsonsWithNullObjects.add(wordGsonMatch);
            }
            Log.i(CursorToImageGsonConverter.class.getName(), "wordGsonsWithNullObjects.size(): " + wordGsonsWithNullObjects.size());
        }

        StoryBookParagraphGson storyBookParagraph = new StoryBookParagraphGson();
        storyBookParagraph.setId(id);
        storyBookParagraph.setSortOrder(sortOrder);
        storyBookParagraph.setOriginalText(originalText);
        storyBookParagraph.setWords(wordGsonsWithNullObjects);

        return storyBookParagraph;
    }
}
