package ai.elimu.vitabu.ui.storybook

import ai.elimu.common.utils.ui.BaseBottomSheetDialogFragment
import ai.elimu.content_provider.utils.ContentProviderUtil.getAllEmojiGsons
import ai.elimu.content_provider.utils.ContentProviderUtil.getWordGson
import ai.elimu.vitabu.BuildConfig
import ai.elimu.vitabu.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 * WordDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
</pre> *
 */
class WordDialogFragment : BaseBottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(WordDialogFragment::class.java.name, "onCreateView")

        return inflater.inflate(R.layout.fragment_word_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(WordDialogFragment::class.java.name, "onViewCreated")

        val context = context ?: return

        val wordId = arguments?.getLong(ARG_WORD_ID) ?: 0L
        Log.i(WordDialogFragment::class.java.name, "wordId: $wordId")

        val wordGson = getWordGson(wordId, context, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID) ?: return
        Log.i(WordDialogFragment::class.java.name, "wordGson: $wordGson")

        val textView = view.findViewById<TextView>(R.id.wordTextView)
        textView.text = wordGson.text

        // Append Emojis (if any) below the Word
        val emojiGsons = getAllEmojiGsons(
            wordGson.id, context, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
        )
        if (emojiGsons.isNotEmpty()) {
            textView.text = textView.text.toString() + "\n"
            for (emojiGson in emojiGsons) {
                textView.text = textView.text.toString() + emojiGson.glyph
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    companion object {
        private const val ARG_WORD_ID = "word_id"

        fun newInstance(wordId: Long): WordDialogFragment {
            Log.i(WordDialogFragment::class.java.name, "newInstance")

            val fragment = WordDialogFragment()
            val args = Bundle()
            args.putLong(ARG_WORD_ID, wordId)
            fragment.arguments = args
            return fragment
        }
    }
}
