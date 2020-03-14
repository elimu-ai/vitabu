package ai.elimu.vitabu.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridLayout;

import ai.elimu.vitabu.R;

public class StoryBooksActivity extends AppCompatActivity {

    private GridLayout storyBooksGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storybooks);

        storyBooksGridLayout = findViewById(R.id.storyBooksGridLayout);


    }
}
