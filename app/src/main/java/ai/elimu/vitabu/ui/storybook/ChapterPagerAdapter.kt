package ai.elimu.vitabu.ui.storybook

import ai.elimu.model.v2.enums.ReadingLevel
import ai.elimu.model.v2.gson.content.StoryBookChapterGson
import ai.elimu.vitabu.ui.storybook.ChapterFragment.Companion.newInstance
import ai.elimu.vitabu.ui.storybook.CoverFragment.Companion.newInstance
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ChapterPagerAdapter(
    fm: FragmentManager,
    storyBookChapters: List<StoryBookChapterGson>,
    readingLevel: ReadingLevel,
    description: String
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val readingLevel: ReadingLevel

    private val description: String

    init {
        Companion.storyBookChapters = storyBookChapters
        this.readingLevel = readingLevel
        this.description = description
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            newInstance(readingLevel, description)
        } else {
            newInstance(position, readingLevel)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }

    override fun getCount(): Int {
        return storyBookChapters.size
    }

    companion object {
        var storyBookChapters: List<StoryBookChapterGson> = listOf()
    }
}
