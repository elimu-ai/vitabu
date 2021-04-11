package ai.elimu.vitabu.ui.storybook;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import ai.elimu.model.enums.ReadingLevel;
import ai.elimu.model.v2.gson.content.StoryBookChapterGson;

public class ChapterPagerAdapter extends FragmentStatePagerAdapter {

    public static List<StoryBookChapterGson> storyBookChapters;

    private ReadingLevel readingLevel;

    private String description;

    public ChapterPagerAdapter(FragmentManager fm, List<StoryBookChapterGson> storyBookChapters, ReadingLevel readingLevel, String description) {
        super(fm);
        ChapterPagerAdapter.storyBookChapters = storyBookChapters;
        this.readingLevel = readingLevel;
        this.description = description;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return CoverFragment.newInstance(readingLevel, description);
        } else {
            return ChapterFragment.newInstance(position, readingLevel);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return storyBookChapters.size();
    }
}
