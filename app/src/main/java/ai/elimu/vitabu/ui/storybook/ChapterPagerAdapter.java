package ai.elimu.vitabu.ui.storybook;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import ai.elimu.model.gson.content.StoryBookChapterGson;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ChapterPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    public static List<StoryBookChapterGson> storyBookChapters;

    public ChapterPagerAdapter(FragmentManager fm, Context context, List<StoryBookChapterGson> storyBookChapters) {
        super(fm);
        this.context = context;
        this.storyBookChapters = storyBookChapters;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a ChapterFragment (defined as a static inner class below).
        return ChapterFragment.newInstance(position + 1);
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
