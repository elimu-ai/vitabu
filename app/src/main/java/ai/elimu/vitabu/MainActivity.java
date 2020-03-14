package ai.elimu.vitabu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ai.elimu.vitabu.ui.StoryBooksActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, StoryBooksActivity.class);
        startActivity(intent);

        finish();
    }
}