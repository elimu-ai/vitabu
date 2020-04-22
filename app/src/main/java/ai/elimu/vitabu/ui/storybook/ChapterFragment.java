package ai.elimu.vitabu.ui.storybook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import ai.elimu.model.gson.v2.content.StoryBookChapterGson;
import ai.elimu.model.gson.v2.content.StoryBookParagraphGson;
import ai.elimu.vitabu.R;

public class ChapterFragment extends Fragment {

    private static final String ARG_CHAPTER_INDEX = "chapter_index";

    private StoryBookChapterGson storyBookChapter;

    private TextToSpeech tts;

    private String chapterText;

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

        // Fetch the StoryBookChapter
        storyBookChapter = ChapterPagerAdapter.storyBookChapters.get(chapterIndex);
        Log.i(getClass().getName(), "storyBookChapter: " + storyBookChapter);

        // Initialize Text-to-Speech
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i(getClass().getName(), "onInit");
                tts.setLanguage(new Locale("hin"));
            }
        });
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

        Log.i(getClass().getName(), "storyBookChapter.getStoryBookParagraphs(): " + storyBookChapter.getStoryBookParagraphs());
        if (storyBookChapter.getStoryBookParagraphs() != null) {
            chapterText = "";
            for (StoryBookParagraphGson storyBookParagraphGson : storyBookChapter.getStoryBookParagraphs()) {
                Log.i(getClass().getName(), "storyBookParagraphGson.getOriginalText(): \"" + storyBookParagraphGson.getOriginalText() + "\"");
                if (!TextUtils.isEmpty(chapterText)) {
                    chapterText += "\n\n";
                }
                chapterText += storyBookParagraphGson.getOriginalText();
            }

            chapterText = chapterText.toLowerCase();

            TextView textView = root.findViewById(R.id.chapter_text);
            textView.setText(chapterText);
            textView.setVisibility(View.VISIBLE);
        }

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "onClick");

                Log.i(getClass().getName(), "chapterText: \"" + chapterText + "\"");
                tts.speak(chapterText, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        return root;
    }
}
