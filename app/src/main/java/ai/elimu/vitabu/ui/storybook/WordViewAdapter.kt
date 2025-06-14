package ai.elimu.vitabu.ui.storybook

import ai.elimu.content_provider.utils.ContentProviderUtil.getAllEmojiGsons
import ai.elimu.model.v2.gson.content.WordGson
import ai.elimu.vitabu.BuildConfig
import ai.elimu.vitabu.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import java.util.Collections

internal class WordViewAdapter(
    private val readingLevelPosition: Int,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val wordsInOriginalText: MutableList<String?> = ArrayList()
    private val words: MutableList<WordGson?> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(wordGson: WordGson?, text: String, view: View?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_layout, parent, false)

        return if (viewType == WORD_TYPE) {
            WordViewHolder(view)
        } else {
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == WORD_TYPE) {
            val wordInOriginalText = wordsInOriginalText[position] ?: ""
            (holder as WordViewHolder).paintWordLayout(
                wordInOriginalText, words[position],
                readingLevelPosition
            )

            holder.itemView.setOnClickListener { v: View? ->
                listener.onItemClick(
                    words[position], text = wordsInOriginalText[position] ?: "", v, position
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return wordsInOriginalText.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (wordsInOriginalText.getOrNull(position) == null) {
            NEW_PARAGRAPH_TYPE
        } else {
            WORD_TYPE
        }
    }

    fun addParagraph(wordsInOriginalText: List<String?>, wordGsons: List<WordGson?>?) {
        // Words in original plaintext
        this.wordsInOriginalText.addAll(wordsInOriginalText)
        this.wordsInOriginalText.add(null)

        // Words with GSON representation
        if (wordGsons == null) {
            words.addAll(Collections.nCopies<WordGson?>(wordsInOriginalText.size, null))
        } else {
            words.addAll(wordGsons)
        }
        words.add(null)

        notifyDataSetChanged()
    }


    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }


    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordTextView: TextView = itemView.findViewById(R.id.word_text)
        private val wordEmoji: TextView = itemView.findViewById(R.id.word_emoji)
        private val wordUnderline: View =
            itemView.findViewById(R.id.word_underline)

        fun paintWordLayout(wordText: String, wordGson: WordGson?, readingLevelPosition: Int) {
            var wordId: Long = -1
            if (wordGson != null) wordId = wordGson.id

            paintWord(wordId, wordText, readingLevelPosition)
            paintUnderline(wordGson)
        }

        private fun paintWord(wordId: Long, wordText: String, readingLevelPosition: Int) {
            wordTextView.text = wordText
            setTextSizeByLevel(wordTextView, readingLevelPosition)

            if (wordText.isEmpty()) {
                itemView.layoutParams.width = 0
            }

            if (wordId != -1L) {
                val emojiGsons = getAllEmojiGsons(
                    wordId,
                    itemView.context,
                    BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
                )
                if (emojiGsons.isNotEmpty()) {
                    wordEmoji.text = emojiGsons.random().glyph
                    setTextSizeByLevel(wordEmoji, readingLevelPosition)
                }
            }
        }

        private fun setTextSizeByLevel(textView: TextView, readingLevelPosition: Int) {
            val fontSize = itemView.context.resources.getIntArray(R.array.chapter_text_font_size)
            val letterSpacing =
                itemView.context.resources.getStringArray(R.array.chapter_text_letter_spacing)
            val lineSpacing =
                itemView.context.resources.getIntArray(R.array.chapter_text_line_spacing_recyclerview)
            val wordSpacing =
                itemView.context.resources.getIntArray(R.array.chapter_text_word_spacing_recyclerview)

            (itemView.layoutParams as FlexboxLayoutManager.LayoutParams).bottomMargin =
                lineSpacing[readingLevelPosition]

            textView.textSize = fontSize[readingLevelPosition].toFloat()
            textView.letterSpacing = letterSpacing[readingLevelPosition].toFloat()
            itemView.setPadding(
                wordSpacing[readingLevelPosition], 0,
                wordSpacing[readingLevelPosition], 0
            )
        }

        /**
         * Underline clickable words, i.e. words that also have a GSON representation.
         */
        private fun paintUnderline(wordGson: WordGson?) {
            if (wordGson == null) {
                wordUnderline.visibility = View.GONE
            } else {
                wordUnderline.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val WORD_TYPE: Int = 0
        const val NEW_PARAGRAPH_TYPE: Int = 1
    }
}
