package ai.elimu.vitabu.ui.storybook

import ai.elimu.analytics.utils.LearningEventUtil
import ai.elimu.common.utils.data.model.tts.QueueMode
import ai.elimu.common.utils.viewmodel.TextToSpeechViewModel
import ai.elimu.common.utils.viewmodel.TextToSpeechViewModelImpl
import ai.elimu.model.v2.enums.ReadingLevel
import ai.elimu.model.v2.enums.analytics.LearningEventType
import ai.elimu.model.v2.gson.content.StoryBookChapterGson
import ai.elimu.model.v2.gson.content.WordGson
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays

@AndroidEntryPoint
open class ChapterFragment : Fragment(), AudioListener {
    
    private val TAG = "ChapterFragment"
    
    private var storyBookChapter: StoryBookChapterGson? = null

    @JvmField
    protected var chapterParagraphs: Array<String?> = arrayOf()

    private var chapterRecyclerView: RecyclerView? = null
    var fabSpeak: FloatingActionButton? = null

    private lateinit var ttsViewModel: TextToSpeechViewModel

    @JvmField
    protected var readingLevelPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        initViewModels()

        val chapterIndex = requireArguments().getInt(ARG_CHAPTER_INDEX)
        Log.i(TAG, "chapterIndex: $chapterIndex")

        // Fetch the StoryBookChapter
        storyBookChapter = ChapterPagerAdapter.storyBookChapters?.get(chapterIndex)
        Log.i(TAG, "storyBookChapter: $storyBookChapter")
    }

    open val rootLayout: Int
        get() = R.layout.fragment_storybook

    private fun initViewModels() {
        ttsViewModel = ViewModelProvider(this)[TextToSpeechViewModelImpl::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")

        val root = inflater.inflate(rootLayout, container, false)

        fabSpeak = root.findViewById(R.id.fab)
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
        Log.i(TAG,
            "storyBookChapter.getStoryBookParagraphs(): $storyBookParagraphGsons")

        if (storyBookParagraphGsons != null) {
            val readingLevel = requireArguments()[ARG_READING_LEVEL] as ReadingLevel?
            readingLevelPosition = if ((readingLevel == null)) 0 else readingLevel.ordinal

            val wordViewAdapter =
                WordViewAdapter(readingLevelPosition, object : WordViewAdapter.OnItemClickListener {
                    override fun onItemClick(wordGson: WordGson?, view: View?, position: Int) {
                        wordGson ?: return
                        Log.i(TAG, "onClick")
                        Log.i(TAG, "wordGson.text: \"" + wordGson.text + "\"")

                        WordDialogFragment.newInstance(wordGson.id)
                            .show(activity!!.supportFragmentManager, "dialog")

                        ttsViewModel.speak(text = wordGson.text, queueMode = QueueMode.FLUSH,
                            utteranceId = "word_" + wordGson.id)

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
                Log.i(TAG,
                    "storyBookParagraphGson.getOriginalText(): \""
                            + storyBookParagraphGsons[paragraphIndex].originalText + "\"")

                val originalText = storyBookParagraphGsons[paragraphIndex].originalText
                val wordsInOriginalText = originalText.trim { it <= ' ' }.split(" ".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                Log.i(TAG, "wordsInOriginalText.length: " + wordsInOriginalText.size)
                Log.i(TAG,
                    "Arrays.toString(wordsInOriginalText): "
                            + wordsInOriginalText.contentToString())

                chapterParagraphs[paragraphIndex] = originalText

                val wordGsons = storyBookParagraphGsons[paragraphIndex].words
                Log.i(TAG, "wordGsons: $wordGsons")

                wordViewAdapter.addParagraph(Arrays.asList(*wordsInOriginalText), wordGsons)
            }
        } else {
            fabSpeak?.visibility = View.GONE
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
                val isSpeaking = ttsViewModel.isSpeaking()
                Log.i(TAG, "onClick. tts.isSpeaking: " + isSpeaking)

                if (isSpeaking) {
                    ttsViewModel.stop()
                    fab.setImageResource(R.drawable.ic_hearing)
                } else {
                    playAudio(chapterText, this@ChapterFragment)
                    fab.setImageResource(R.drawable.ic_stop_media)
                }
            }
        })
    }

    fun playAudio(chapterText: Array<String?>, audioListener: AudioListener?) {
        ttsViewModel.setOnUtteranceProgressListener(getUtteranceProgressListener(audioListener))

        Log.i(TAG, "chapterText: \"" + chapterText.contentToString() + "\"")
        Log.v(TAG, "playingAudio with: " + chapterText.size + " paragraphs. chapterParagraphs.size: " + chapterParagraphs.size)
        for ((paragraphIndex, paragraph) in chapterText.withIndex()) {
            Log.d(TAG, "Speaking paragraph: $paragraph")

            val utteranceId = paragraphIndex.toString()

            ttsViewModel.speak(
                paragraph?.replace("[-*]".toRegex(), "") ?: "",
                QueueMode.ADD,
                utteranceId
            )
            ttsViewModel.playSilentUtterance(PARAGRAPH_PAUSE, TextToSpeech.QUEUE_ADD, null)
        }
    }

    override fun onPause() {
        super.onPause()
        ttsViewModel.stop()
    }

    open fun getUtteranceProgressListener(audioListener: AudioListener?): UtteranceProgressListener {
        val wordPosition = intArrayOf(-1)

        return object : UtteranceProgressListener() {
            val layoutManager: FlexboxLayoutManager? =
                chapterRecyclerView!!.layoutManager as FlexboxLayoutManager?

            var highlightedTextView: View? = null

            override fun onStart(utteranceId: String) {
                Log.i(TAG, "onStart")
            }

            override fun onRangeStart(utteranceId: String, start: Int, end: Int, frame: Int) {
                Log.i(TAG, "onRangeStart")
                super.onRangeStart(utteranceId, start, end, frame)

                Log.i(
                    TAG,
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
                Log.v(TAG, "Chapter onDone. isSpeaking: " + ttsViewModel.isSpeaking() + ". utteranceId: " + utteranceId + "\nchapterParagraphs.size: " + chapterParagraphs.size)

                // Remove highlighting of the last spoken word
                val itemView = layoutManager!!.findViewByPosition(wordPosition[0])
                if (itemView != null) {
                    itemView.background =
                        ContextCompat.getDrawable(context!!, R.drawable.bg_word_selector)
                }

                val finalUtteranceId = (chapterParagraphs.size - 1).toString()
                if (utteranceId == finalUtteranceId) {
                    fabSpeak?.setImageResource(R.drawable.ic_hearing)
                }
            }

            override fun onError(utteranceId: String) {
                Log.i(TAG, "Chapter onError")
            }

            override fun onStop(utteranceId: String, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
                Log.v(TAG, "Chapter onStop")
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
        const val ARG_CHAPTER_INDEX: String = "chapter_index"
        const val ARG_READING_LEVEL: String = "reading_level"

        private val FILES_PATH: String = Environment.getExternalStorageDirectory().toString() +
                "/Android/data/" +
                BuildConfig.CONTENT_PROVIDER_APPLICATION_ID +
                "/files/"

        val PICTURES_PATH: String = FILES_PATH + Environment.DIRECTORY_PICTURES + "/"

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
