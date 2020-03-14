package ai.elimu.vitabu.ui.storybook;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class ChapterViewModel extends ViewModel {

    private MutableLiveData<Integer> index = new MutableLiveData<>();

    private LiveData<String> text = Transformations.map(index, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "Hello world from chapter: " + input;
        }
    });

    public void setIndex(int index) {
        this.index.setValue(index);
    }

    public LiveData<String> getText() {
        return text;
    }
}