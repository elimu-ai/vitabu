package ai.elimu.vitabu.ui

import ai.elimu.analytics.utils.LearningEventUtil
import ai.elimu.content_provider.utils.ContentProviderUtil
import ai.elimu.model.v2.enums.ReadingLevel
import ai.elimu.model.v2.enums.analytics.LearningEventType
import ai.elimu.model.v2.gson.content.StoryBookGson
import ai.elimu.vitabu.BaseApplication
import ai.elimu.vitabu.BuildConfig
import ai.elimu.vitabu.R
import ai.elimu.vitabu.ui.storybook.StoryBookActivity
import ai.elimu.vitabu.util.SingleClickListener
import ai.elimu.vitabu.util.readImageBytes
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryBooksActivity : AppCompatActivity() {
    private var storyBooksGridLayout: GridLayout? = null
    private var storyBooksProgressBar: ProgressBar? = null

    private var storyBooks: List<StoryBookGson> = mutableListOf()

    var TAG: String = StoryBooksActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_storybooks)

        storyBooksGridLayout = findViewById(R.id.storyBooksGridLayout)
        storyBooksProgressBar = findViewById(R.id.storybooks_progress_bar)

        // Fetch StoryBooks from the elimu.ai Content Provider (see https://github.com/elimu-ai/content-provider)
        storyBooks = ContentProviderUtil.getAllStoryBookGsons(
            applicationContext,
            BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
        )
        Log.i(TAG, "storyBooks.size(): " + storyBooks.size)
    }

    override fun onStart() {
        Log.i(javaClass.name, "onStart")
        super.onStart()

        // Reset the state of the GridLayout
        storyBooksProgressBar!!.visibility = View.VISIBLE
        storyBooksGridLayout!!.visibility = View.GONE
        storyBooksGridLayout!!.removeAllViews()

        (application as BaseApplication).executor.execute(object : Runnable {
            override fun run() {
                var storyBook: StoryBookGson
                var readingLevel: ReadingLevel?

                // Create a View for each StoryBook in the list
                for (index in storyBooks!!.indices) {
                    storyBook = storyBooks!![index]
                    readingLevel = storyBook.readingLevel

                    Log.i(javaClass.name, "storyBook.getId(): " + storyBook.id)
                    Log.i(javaClass.name, "storyBook.getTitle(): \"" + storyBook.title + "\"")
                    Log.i(
                        javaClass.name,
                        "storyBook.getDescription(): \"" + storyBook.description + "\""
                    )

                    if (index == 0 || readingLevel != storyBooks!![index - 1].readingLevel) {
                        val levelLayout = LayoutInflater.from(this@StoryBooksActivity).inflate(
                            R.layout.activity_storybooks_level,
                            storyBooksGridLayout,
                            false
                        )

                        val layoutParams = GridLayout.LayoutParams()
                        layoutParams.columnSpec = GridLayout.spec(
                            0,
                            resources.getInteger(R.integer.gridlayout_column_count)
                        )
                        levelLayout.layoutParams = layoutParams

                        val levelTextView = levelLayout.findViewById<TextView>(R.id.levelName)
                        if (readingLevel == null) {
                            levelTextView.visibility = View.GONE
                        } else {
                            levelTextView.text =
                                String.format(
                                    resources.getString(R.string.level),
                                    readingLevel.ordinal + 1
                                )
                        }

                        runOnUiThread {
                            storyBooksGridLayout!!.addView(levelLayout)
                            if (storyBooksGridLayout!!.childCount == storyBooks!!.size) {
                                storyBooksProgressBar!!.visibility = View.GONE
                                storyBooksGridLayout!!.visibility = View.VISIBLE
                            }
                        }
                    }

                    val storyBookView = LayoutInflater.from(this@StoryBooksActivity).inflate(
                        R.layout.activity_storybooks_cover_view,
                        storyBooksGridLayout,
                        false
                    )

                    // Fetch Image from the elimu.ai Content Provider (see https://github.com/elimu-ai/content-provider)
                    Log.i(TAG,
                        "storyBook.getCoverImage(): " + storyBook.coverImage
                                + ". Id: " + storyBook.coverImage.id)
                    val coverImage = ContentProviderUtil.getImageGson(
                        storyBook.coverImage.id,
                        applicationContext, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
                    )

                    if (coverImage != null) {
                        val coverImageView =
                            storyBookView.findViewById<ImageView>(R.id.coverImageView)
                        CoroutineScope(Dispatchers.IO).launch {
                            readImageBytes(coverImage.id)?.let { bytes ->
                                withContext(Dispatchers.Main) {
                                    Glide.with(this@StoryBooksActivity).load(bytes)
                                        .into(coverImageView)
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "coverImage is null. Id: " + storyBook.coverImage.id)
                    }

                    val coverTitleTextView =
                        storyBookView.findViewById<TextView>(R.id.coverTitleTextView)
                    coverTitleTextView.text = storyBook.title

                    val finalStoryBook = storyBook
                    storyBookView.setOnClickListener(object : SingleClickListener() {
                        override fun onSingleClick(v: View) {
                            Log.i(javaClass.name, "onClick")

                            Log.i(javaClass.name, "storyBook.getId(): " + finalStoryBook.id)
                            Log.i(javaClass.name, "storyBook.getTitle(): " + finalStoryBook.title)
                            Log.i(javaClass.name,
                                "storyBook.getDescription(): " + finalStoryBook.description)

                            // Report learning event to the Analytics application (https://github.com/elimu-ai/analytics)
                            LearningEventUtil.reportStoryBookLearningEvent(
                                finalStoryBook, LearningEventType.STORYBOOK_OPENED,
                                applicationContext, BuildConfig.ANALYTICS_APPLICATION_ID
                            )

                            val intent = Intent(applicationContext, StoryBookActivity::class.java)
                            intent.putExtra(
                                StoryBookActivity.EXTRA_KEY_STORYBOOK_ID,
                                finalStoryBook.id
                            )
                            intent.putExtra(
                                StoryBookActivity.EXTRA_KEY_STORYBOOK_LEVEL,
                                finalStoryBook.readingLevel
                            )
                            intent.putExtra(
                                StoryBookActivity.EXTRA_KEY_STORYBOOK_DESCRIPTION,
                                finalStoryBook.description
                            )
                            startActivity(intent)
                        }
                    })

                    runOnUiThread {
                        storyBooksGridLayout!!.addView(storyBookView)
                        if (storyBooksGridLayout!!.childCount == storyBooks!!.size) {
                            storyBooksProgressBar!!.visibility = View.GONE
                            storyBooksGridLayout!!.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })
    }
}
