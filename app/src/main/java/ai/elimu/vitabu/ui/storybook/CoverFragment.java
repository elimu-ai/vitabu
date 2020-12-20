package ai.elimu.vitabu.ui.storybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ai.elimu.model.enums.ReadingLevel;
import ai.elimu.vitabu.R;

public class CoverFragment extends ChapterFragment {

    private static final String ARG_DESCRIPTION = "description";

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

        TextView chapterTextView = root.findViewById(R.id.chapter_text);
        setTitleSizeByLevel(chapterTextView);

        description = (String) getArguments().get(ARG_DESCRIPTION);
        descriptionTextView = root.findViewById(R.id.storybook_description);
        descriptionTextView.setText(description);
        setTextSizeByLevel(descriptionTextView);

        return root;
    }

    public void setTitleSizeByLevel(TextView textView) {
        super.setTextSizeByLevel(textView);
        int[] fontSize = getResources().getIntArray(R.array.cover_title_font_size);
        textView.setTextSize(fontSize[readingLevelPosition]);
    }

    @Override
    public void onAudioDone() {
        playAudio(descriptionTextView, description,null);
    }
}
