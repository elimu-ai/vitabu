package ai.elimu.vitabu.ui.storybook

import ai.elimu.model.v2.enums.ReadingLevel
import ai.elimu.vitabu.R
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.Collections

class CoverFragment : ChapterFragment() {
    private var audioTextView: TextView? = null
    private var audioText: String? = null

    private var titleTextView: TextView? = null

    private var descriptionTextView: TextView? = null
    private val description = arrayOfNulls<String>(1)

    override val rootLayout: Int
        get() = R.layout.fragment_storybook_cover

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        val titleFontSize = resources.getIntArray(R.array.cover_title_font_size)
        val descriptionFontSize = resources.getIntArray(R.array.chapter_text_font_size)

        for (i in chapterParagraphs.indices) {
            chapterParagraphs[i] = setWordSpacing(chapterParagraphs[i]!!)
        }

        titleTextView = root?.findViewById(R.id.storybook_title)
        titleTextView?.text = chapterParagraphs[0]

        titleTextView?.let { setTextSizeByLevel(it, titleFontSize) }

        // Initialize audio parameters with the storybook title
        audioTextView = titleTextView

        audioText = TextUtils.join("", chapterParagraphs)
        description[0] = setWordSpacing((requireArguments()[ARG_DESCRIPTION] as String?)!!)
        descriptionTextView = root?.findViewById(R.id.storybook_description)
        descriptionTextView?.text = description[0]

        descriptionTextView?.let { setTextSizeByLevel(it, descriptionFontSize) }

        return root
    }

    private fun setWordSpacing(originalText: String): String {
        val wordSpacing = resources.getIntArray(R.array.chapter_text_word_spacing)
        val spaces = Collections.nCopies(wordSpacing[readingLevelPosition], " ")
        return originalText.replace(" ", TextUtils.join("", spaces))
    }

    private fun setTextSizeByLevel(textView: TextView, fontSize: IntArray) {
        val letterSpacing = resources.getStringArray(R.array.chapter_text_letter_spacing)
        val lineSpacing = resources.getStringArray(R.array.chapter_text_line_spacing)

        textView.textSize = fontSize[readingLevelPosition].toFloat()
        textView.letterSpacing = letterSpacing[readingLevelPosition].toFloat()
        textView.setLineSpacing(0f, lineSpacing[readingLevelPosition].toFloat())
    }

    override fun onAudioDone() {
        // Update audio parameters with the storybook description
        audioTextView = descriptionTextView
        audioText = description[0]

        playAudio(description, null)
    }

    override fun getUtteranceProgressListener(audioListener: AudioListener?): UtteranceProgressListener {
        return object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                Log.i(javaClass.name, "onStart")
            }

            override fun onRangeStart(utteranceId: String, start: Int, end: Int, frame: Int) {
                Log.i(javaClass.name, "onRangeStart")
                super.onRangeStart(utteranceId, start, end, frame)

                Log.i(
                    javaClass.name,
                    "utteranceId: $utteranceId, start: $start, end: $end"
                )

                if (start >= 0) {
                    // Highlight the word being spoken
                    val spannable: Spannable = SpannableString(audioText)
                    val context = audioTextView?.context ?: return
                    val backgroundColorSpan =
                        BackgroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent))
                    spannable.setSpan(
                        backgroundColorSpan,
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    audioTextView?.text = spannable
                }
            }

            override fun onDone(utteranceId: String) {
                Log.i(javaClass.name, "onDone")

                // Remove highlighting of the last spoken word
                requireActivity().runOnUiThread {
                    audioTextView!!.text = audioText
                }

                if (audioListener != null) {
                    audioListener.onAudioDone()
                } else {
                    onStop(utteranceId, false)
                }
            }

            override fun onError(utteranceId: String) {
                Log.i(javaClass.name, "onError")
            }

            override fun onStop(utteranceId: String, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
                requireActivity().runOnUiThread {
                    titleTextView!!.text = TextUtils.join("", chapterParagraphs)
                    descriptionTextView!!.text = description[0]
                }
                audioText = TextUtils.join("", chapterParagraphs)
                audioTextView = titleTextView
                fabSpeak?.setImageResource(R.drawable.ic_hearing)
            }
        }
    }

    companion object {
        private const val ARG_DESCRIPTION = "description"

        @JvmStatic
        fun newInstance(readingLevel: ReadingLevel?, description: String?): CoverFragment {
            val fragment = CoverFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_CHAPTER_INDEX, 0)
            bundle.putSerializable(ARG_READING_LEVEL, readingLevel)
            bundle.putSerializable(ARG_DESCRIPTION, description)
            fragment.arguments = bundle
            return fragment
        }
    }
}
