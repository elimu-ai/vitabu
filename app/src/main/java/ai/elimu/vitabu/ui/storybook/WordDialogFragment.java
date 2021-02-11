package ai.elimu.vitabu.ui.storybook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import ai.elimu.content_provider.utils.ContentProviderHelper;
import ai.elimu.model.v2.gson.content.EmojiGson;
import ai.elimu.model.v2.gson.content.WordGson;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     WordDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class WordDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_WORD_ID = "word_id";

    public static WordDialogFragment newInstance(Long wordId) {
        Log.i(WordDialogFragment.class.getName(), "newInstance");

        final WordDialogFragment fragment = new WordDialogFragment();
        final Bundle args = new Bundle();
        args.putLong(ARG_WORD_ID, wordId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(WordDialogFragment.class.getName(), "onCreateView");

        return inflater.inflate(R.layout.fragment_word_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(WordDialogFragment.class.getName(), "onViewCreated");

        Long wordId = getArguments().getLong(ARG_WORD_ID);
        Log.i(WordDialogFragment.class.getName(), "wordId: " + wordId);

        WordGson wordGson = ContentProviderHelper.getWordGson(wordId, getContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
        Log.i(WordDialogFragment.class.getName(), "wordGson: " + wordGson);

        TextView textView = view.findViewById(R.id.wordTextView);
        textView.setText(wordGson.getText());

        // Append Emojis (if any) below the Word
        List<EmojiGson> emojiGsons = ContentProviderHelper.getEmojiGsons(wordGson.getId(), getContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
        if (!emojiGsons.isEmpty()) {
            textView.setText(textView.getText() + "\n");
            for (EmojiGson emojiGson : emojiGsons) {
                textView.setText(textView.getText() + emojiGson.getGlyph());
            }
        }
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}