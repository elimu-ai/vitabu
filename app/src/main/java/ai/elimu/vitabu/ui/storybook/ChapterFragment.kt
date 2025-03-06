package ai.elimu.vitabu.ui.storybook

import ai.elimu.analytics.utils.LearningEventUtil
import ai.elimu.model.v2.enums.ReadingLevel
import ai.elimu.model.v2.enums.analytics.LearningEventType
import ai.elimu.model.v2.gson.content.StoryBookChapterGson
import ai.elimu.model.v2.gson.content.WordGson
import ai.elimu.vitabu.BaseApplication
import ai.elimu.vitabu.BuildConfig
import ai.elimu.vitabu.R
import ai.elimu.vitabu.util.readImageBytes
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays

open class ChapterFragment : Fragment(), AudioListener {
    private var storyBookChapter: StoryBookChapterGson? = null

    @JvmField
    protected var chapterParagraphs: Array<String?> = arrayOf()

    private var chapterRecyclerView: RecyclerView? = null

    private var tts: TextToSpeech? = null

    @JvmField
    protected var readingLevelPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(javaClass.name, "onCreate")
        super.onCreate(savedInstanceState)

        val chapterIndex = requireArguments().getInt(ARG_CHAPTER_INDEX)
        Log.i(javaClass.name, "chapterIndex: $chapterIndex")

        // Fetch the StoryBookChapter
        storyBookChapter = ChapterPagerAdapter.storyBookChapters[chapterIndex]
        Log.i(javaClass.name, "storyBookChapter: $storyBookChapter")

        // Fetch the Text-to-Speech (TTS) engine which has already been initialized
        val baseApplication = requireActivity().application as BaseApplication
        tts = baseApplication.tts
    }

    open val rootLayout: Int
        get() = R.layout.fragment_storybook

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(javaClass.name, "onCreateView")

        val root = inflater.inflate(rootLayout, container, false)

        val fab = root.findViewById<FloatingActionButton>(R.id.fab)
        chapterRecyclerView = root.findViewById(R.id.chapter_text)

        // Set chapter image
        val chapterImage = storyBookChapter!!.image
        if (chapterImage != null) {
            val imageView = root.findViewById<ImageView>(R.id.chapter_image)
            CoroutineScope(Dispatchers.IO).launch {
                val context = context ?: return@launch
                context.readImageBytes(chapterImage.id)?.let { bytes ->
                    withContext(Dispatchers.Main) {
                        Glide.with(context).load(bytes).into(imageView)
                    }
                }
            }
        }

        // Set paragraph(s)
        val storyBookParagraphGsons = storyBookChapter!!.storyBookParagraphs
        Log.i(javaClass.name,
            "storyBookChapter.getStoryBookParagraphs(): $storyBookParagraphGsons")

        if (storyBookParagraphGsons != null) {
            val readingLevel = requireArguments()[ARG_READING_LEVEL] as ReadingLevel?
            readingLevelPosition = if ((readingLevel == null)) 0 else readingLevel.ordinal

            val wordViewAdapter =
                WordViewAdapter(readingLevelPosition, object : WordViewAdapter.OnItemClickListener {
                    override fun onItemClick(wordGson: WordGson, view: View, position: Int) {
                        Log.i(javaClass.name, "onClick")
                        Log.i(javaClass.name, "wordGson.text: \"" + wordGson.text + "\"")

                        WordDialogFragment.newInstance(wordGson.id)
                            .show(activity!!.supportFragmentManager, "dialog")

                        tts!!.speak(
                            wordGson.text,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "word_" + wordGson.id
                        )

                        // Report learning event to the Analytics application (https://github.com/elimu-ai/analytics)
                        LearningEventUtil.reportWordLearningEvent(
                            wordGson, LearningEventType.WORD_PRESSED,
                            context, BuildConfig.ANALYTICS_APPLICATION_ID
                        )
                    }
                })

            if (chapterRecyclerView != null) {
                val layoutManager = FlexboxLayoutManager(context)
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.CENTER
                chapterRecyclerView!!.layoutManager = layoutManager
                chapterRecyclerView!!.adapter = wordViewAdapter
            }

            chapterParagraphs = arrayOfNulls(storyBookParagraphGsons.size)
            for (paragraphIndex in storyBookParagraphGsons.indices) {
                Log.i(javaClass.name,
                    "storyBookParagraphGson.getOriginalText(): \""
                            + storyBookParagraphGsons[paragraphIndex].originalText + "\"")

                val originalText = storyBookParagraphGsons[paragraphIndex].originalText
                val wordsInOriginalText = originalText.trim { it <= ' ' }.split(" ".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                Log.i(javaClass.name, "wordsInOriginalText.length: " + wordsInOriginalText.size)
                Log.i(javaClass.name,
                    "Arrays.toString(wordsInOriginalText): "
                            + wordsInOriginalText.contentToString())

                chapterParagraphs[paragraphIndex] = originalText

                val wordAudios = storyBookParagraphGsons[paragraphIndex].words
                Log.i(javaClass.name, "words: $wordAudios")

                wordViewAdapter.addParagraph(Arrays.asList(*wordsInOriginalText), wordAudios)
            }
        } else {
            fab.visibility = View.GONE
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add button for initializing Text-to-Speech (TTS)
        val chapterText = chapterParagraphs
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                Log.i(javaClass.name, "onClick")
                playAudio(chapterText, this@ChapterFragment)
            }
        })
    }

    fun playAudio(chapterText: Array<String?>, audioListener: AudioListener?) {
        tts!!.setOnUtteranceProgressListener(getUtteranceProgressListener(audioListener))

        Log.i(javaClass.name, "chapterText: \"" + chapterText.contentToString() + "\"")
        Log.v("tuancoltech", "playingAudio with: " + chapterText.size + " paragraphs ")
        for (paragraph in chapterText) {
            Log.d("tuancoltech", "Speaking paragraph: $paragraph")
            tts!!.speak(
                paragraph?.replace("[-*]".toRegex(), ""),
                TextToSpeech.QUEUE_ADD,
                null,
                "0"
            )
            tts!!.playSilentUtterance(PARAGRAPH_PAUSE, TextToSpeech.QUEUE_ADD, null)
        }
    }

    override fun onPause() {
        super.onPause()
        tts!!.stop()
    }

    open fun getUtteranceProgressListener(audioListener: AudioListener?): UtteranceProgressListener? {
        val wordPosition = intArrayOf(-1)

        return object : UtteranceProgressListener() {
            val layoutManager: FlexboxLayoutManager? =
                chapterRecyclerView!!.layoutManager as FlexboxLayoutManager?

            var highlightedTextView: View? = null

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
                var itemView: View?

                // Highlight the word being spoken
                if (wordPosition[0] > -1) {
                    itemView = layoutManager!!.findViewByPosition(wordPosition[0])
                    if (itemView != null) {
                        itemView.background =
                            ContextCompat.getDrawable(context!!, R.drawable.bg_word_selector)
                    }
                }

                wordPosition[0]++
                itemView = layoutManager!!.findViewByPosition(wordPosition[0])
                if (chapterRecyclerView!!.adapter!!.getItemViewType(wordPosition[0]) == WordViewAdapter.NEW_PARAGRAPH_TYPE) {
                    wordPosition[0]++
                } else if (itemView != null && (itemView.findViewById<View>(R.id.word_text) as TextView).text.length == 0) {
                    wordPosition[0]++
                }

                scrollToWordIfNotVisible(wordPosition[0])
                itemView = layoutManager.findViewByPosition(wordPosition[0])
                if (itemView != null) {
                    itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.colorAccent
                        )
                    )
                    highlightedTextView = itemView
                }
            }

            override fun onDone(utteranceId: String) {
                Log.i(javaClass.name, "onDone")

                // Remove highlighting of the last spoken word
                val itemView = layoutManager!!.findViewByPosition(wordPosition[0])
                if (itemView != null) {
                    itemView.background =
                        ContextCompat.getDrawable(context!!, R.drawable.bg_word_selector)
                }
            }

            override fun onError(utteranceId: String) {
                Log.i(javaClass.name, "onError")
            }

            override fun onStop(utteranceId: String, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
                if (highlightedTextView != null) {
                    highlightedTextView!!.background =
                        ContextCompat.getDrawable(context!!, R.drawable.bg_word_selector)
                }
            }

            fun scrollToWordIfNotVisible(position: Int) {
                val firstWordVisible = layoutManager!!.findFirstCompletelyVisibleItemPosition()
                val lastWordVisible = layoutManager.findLastCompletelyVisibleItemPosition()

                if ((position < firstWordVisible) || (position > lastWordVisible)) {
                    activity!!.runOnUiThread {
                        layoutManager.scrollToPosition(
                            position
                        )
                    }
                }
            }
        }
    }

    override fun onAudioDone() {
    }

    companion object {
        protected const val ARG_CHAPTER_INDEX: String = "chapter_index"
        protected const val ARG_READING_LEVEL: String = "reading_level"

        private val FILES_PATH: String = Environment.getExternalStorageDirectory().toString() +
                "/Android/data/" +
                BuildConfig.CONTENT_PROVIDER_APPLICATION_ID +
                "/files/"

        val PICTURES_PATH: String = FILES_PATH + Environment.DIRECTORY_PICTURES + "/"
        val MUSIC_PATH: String = FILES_PATH + Environment.DIRECTORY_MUSIC + "/"

        private const val PARAGRAPH_PAUSE: Long = 1000

        @JvmStatic
        fun newInstance(chapterIndex: Int, readingLevel: ReadingLevel?): ChapterFragment {
            val fragment = ChapterFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_CHAPTER_INDEX, chapterIndex)
            bundle.putSerializable(ARG_READING_LEVEL, readingLevel)
            fragment.arguments = bundle
            return fragment
        }
    }
}
