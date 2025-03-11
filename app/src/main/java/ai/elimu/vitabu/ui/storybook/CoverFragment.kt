package ai.elimu.vitabu.ui.storybook;

import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import ai.elimu.model.v2.enums.ReadingLevel;
import ai.elimu.vitabu.R;

public class CoverFragment extends ChapterFragment {

    private static final String ARG_DESCRIPTION = "description";

    private TextView audioTextView;
    private String audioText;

    protected TextView titleTextView;

    private TextView descriptionTextView;
    private final String[] description = new String[1];

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

        for (int i = 0; i < chapterParagraphs.length; i++) {
            chapterParagraphs[i] = setWordSpacing(chapterParagraphs[i]);
        }

        titleTextView = root.findViewById(R.id.storybook_title);
        titleTextView.setText(chapterParagraphs[0]);

        setTextSizeByLevel(titleTextView, titleFontSize);

        // Initialize audio parameters with the storybook title
        audioTextView = titleTextView;

        audioText = TextUtils.join("", chapterParagraphs);
        description[0] = setWordSpacing((String) getArguments().get(ARG_DESCRIPTION));
        descriptionTextView = root.findViewById(R.id.storybook_description);
        descriptionTextView.setText(description[0]);

        setTextSizeByLevel(descriptionTextView, descriptionFontSize);

        return root;
    }

    private String setWordSpacing(String originalText) {
        int[] wordSpacing = getResources().getIntArray(R.array.chapter_text_word_spacing);
        List<String> spaces = Collections.nCopies(wordSpacing[readingLevelPosition], " ");
        return originalText.replace(" ", TextUtils.join("", spaces));
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
        audioText = description[0];

        playAudio(description, null);
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
                
                if (start >= 0) {
                    // Highlight the word being spoken
                    Spannable spannable = new SpannableString(audioText);
                    BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(getResources().getColor(R.color.colorAccent));
                    spannable.setSpan(backgroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    audioTextView.setText(spannable);
                }
            }

            @Override
            public void onDone(String utteranceId) {
                Log.i(getClass().getName(), "onDone");

                // Remove highlighting of the last spoken word
                requireActivity().runOnUiThread(() -> {
                    audioTextView.setText(audioText);
                });

                if (audioListener != null) {
                    audioListener.onAudioDone();
                } else {
                    onStop(utteranceId, false);
                }
            }

            @Override
            public void onError(String utteranceId) {
                Log.i(getClass().getName(), "onError");
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                super.onStop(utteranceId, interrupted);
                requireActivity().runOnUiThread(() -> {
                    titleTextView.setText(TextUtils.join("", chapterParagraphs));
                    descriptionTextView.setText(description[0]);
                });
                audioText = TextUtils.join("", chapterParagraphs);
                audioTextView = titleTextView;
            }
        };
    }
}
