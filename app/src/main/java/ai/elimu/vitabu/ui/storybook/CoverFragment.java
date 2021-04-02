package ai.elimu.vitabu.ui.storybook;

import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ai.elimu.model.enums.ReadingLevel;
import ai.elimu.vitabu.R;

public class CoverFragment extends ChapterFragment {

    private static final String ARG_DESCRIPTION = "description";

    private TextView audioTextView;
    private String audioText;

    protected TextView titleTextView;

    private TextView descriptionTextView;
    private String description;

    public static CoverFragment newInstance(ReadingLevel readingLevel, String description) {
        CoverFragment fragment = new CoverFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CHAPTER_INDEX, 0);
        bundle.putSerializable(ARG_READING_LEVEL, readingLevel);
        bundle.putSerializable(ARG_DESCRIPTION, description);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getRootLayout() {
        return R.layout.fragment_storybook_cover;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        int[] titleFontSize = getResources().getIntArray(R.array.cover_title_font_size);
        int[] descriptionFontSize = getResources().getIntArray(R.array.chapter_text_font_size);

        titleTextView = root.findViewById(R.id.storybook_title);
        titleTextView.setText(chapterText);
        setTextSizeByLevel(titleTextView, titleFontSize);

        // Initialize audio parameters with the storybook title
        audioTextView = titleTextView;
        audioText = chapterText;

        description = (String) getArguments().get(ARG_DESCRIPTION);
        descriptionTextView = root.findViewById(R.id.storybook_description);
        descriptionTextView.setText(description);

        setTextSizeByLevel(descriptionTextView, descriptionFontSize);

        return root;
    }

    private void setTextSizeByLevel(TextView textView, int[] fontSize) {
        String[] letterSpacing = getResources().getStringArray(R.array.chapter_text_letter_spacing);
        String[] lineSpacing = getResources().getStringArray(R.array.chapter_text_line_spacing);

        textView.setTextSize(fontSize[readingLevelPosition]);
        textView.setLetterSpacing(Float.parseFloat(letterSpacing[readingLevelPosition]));
        textView.setLineSpacing(0, Float.parseFloat(lineSpacing[readingLevelPosition]));
    }

    @Override
    public void onAudioDone() {
        // Update audio parameters with the storybook description
        audioTextView = descriptionTextView;
        audioText = description;

        playAudio(description,null);
    }

    @Override
    public UtteranceProgressListener getUtteranceProgressListener(final AudioListener audioListener) {
        return new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
                Log.i(getClass().getName(), "onStart");
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                Log.i(getClass().getName(), "onRangeStart");
                super.onRangeStart(utteranceId, start, end, frame);

                Log.i(getClass().getName(), "utteranceId: " + utteranceId + ", start: " + start + ", end: " + end);

                // Highlight the word being spoken
                Spannable spannable = new SpannableString(audioText);
                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(getResources().getColor(R.color.colorAccent));
                spannable.setSpan(backgroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                audioTextView.setText(spannable);
            }

            @Override
            public void onDone(String utteranceId) {
                Log.i(getClass().getName(), "onDone");

                // Remove highlighting of the last spoken word
                audioTextView.setText(audioText);

                if (audioListener != null) {
                    audioListener.onAudioDone();
                }
            }

            @Override
            public void onError(String utteranceId) {
                Log.i(getClass().getName(), "onError");
            }
        };
    }
}
