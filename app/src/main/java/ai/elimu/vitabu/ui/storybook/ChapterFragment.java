package ai.elimu.vitabu.ui.storybook;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ai.elimu.model.gson.content.StoryBookChapterGson;
import ai.elimu.model.gson.content.multimedia.ImageGson;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;
import ai.elimu.vitabu.util.CursorToImageGsonConverter;

public class ChapterFragment extends Fragment {

    private static final String ARG_CHAPTER_INDEX = "chapter_index";

    private StoryBookChapterGson storyBookChapter;

    public static ChapterFragment newInstance(int chapterIndex) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CHAPTER_INDEX, chapterIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        Integer chapterIndex = getArguments().getInt(ARG_CHAPTER_INDEX);
        Log.i(getClass().getName(), "chapterIndex: " + chapterIndex);

        storyBookChapter = ChapterPagerAdapter.storyBookChapters.get(chapterIndex);
        Log.i(getClass().getName(), "storyBookChapter: " + storyBookChapter);

        Log.i(getClass().getName(), "storyBookChapter.getImage(): " + storyBookChapter.getImage());
        if (storyBookChapter.getImage() != null) {
            Uri uri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.image_provider/images/" + storyBookChapter.getImage().getId());
            Log.i(getClass().getName(), "uri: " + uri);
            Cursor imageCursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (imageCursor == null) {
                Log.e(getClass().getName(), "imageCursor == null");
                Toast.makeText(getContext(), "imageCursor == null", Toast.LENGTH_LONG).show();
            } else {
                Log.i(getClass().getName(), "imageCursor.getCount(): " + imageCursor.getCount());
                if (imageCursor.getCount() == 0) {
                    Log.e(getClass().getName(), "imageCursor.getCount() == 0");
                } else {
                    Log.i(getClass().getName(), "imageCursor.getCount(): " + imageCursor.getCount());

                    imageCursor.moveToFirst();

                    // Convert from Room to Gson
                    ImageGson imageGson = CursorToImageGsonConverter.getImage(imageCursor);
                    storyBookChapter.setImage(imageGson);

                    imageCursor.close();
                    Log.i(getClass().getName(), "cursor.isClosed(): " + imageCursor.isClosed());
                }
            }
        }

        Log.i(getClass().getName(), "storyBookChapter.getStoryBookParagraphs(): " + storyBookChapter.getStoryBookParagraphs());
        // TODO: set paragraphs
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreateView");

        View root = inflater.inflate(R.layout.fragment_storybook, container, false);

        if (storyBookChapter.getImage() != null) {
            ImageView imageView = root.findViewById(R.id.chapter_image);
            byte[] bytes = storyBookChapter.getImage().getBytes();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);
        }

        // TODO
//        TextView textView = root.findViewById(R.id.chapter_text);
//        textView.setText(chapterText);

        return root;
    }
}
