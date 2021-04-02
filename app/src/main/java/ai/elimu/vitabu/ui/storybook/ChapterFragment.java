package ai.elimu.vitabu.ui.storybook;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ai.elimu.analytics.utils.LearningEventUtil;
import ai.elimu.content_provider.utils.ContentProviderHelper;
import ai.elimu.model.enums.ReadingLevel;
import ai.elimu.model.enums.analytics.LearningEventType;
import ai.elimu.model.v2.gson.content.AudioGson;
import ai.elimu.model.v2.gson.content.ImageGson;
import ai.elimu.model.v2.gson.content.StoryBookChapterGson;
import ai.elimu.model.v2.gson.content.StoryBookParagraphGson;
import ai.elimu.model.v2.gson.content.WordGson;
import ai.elimu.vitabu.BaseApplication;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;

public class ChapterFragment extends Fragment implements AudioListener {

    protected static final String ARG_CHAPTER_INDEX = "chapter_index";
    protected static final String ARG_READING_LEVEL = "reading_level";

    final static String FILES_PATH = Environment.getExternalStorageDirectory() +
            "/Android/data/" +
            BuildConfig.CONTENT_PROVIDER_APPLICATION_ID +
            "/files/";

    final static String PICTURES_PATH = FILES_PATH + Environment.DIRECTORY_PICTURES + "/";
    final static String MUSIC_PATH = FILES_PATH + Environment.DIRECTORY_MUSIC + "/";

    private StoryBookChapterGson storyBookChapter;

    protected String chapterText = "";

    private RecyclerView chapterRecyclerView;

    private TextToSpeech tts;

    protected int readingLevelPosition;

    public static ChapterFragment newInstance(int chapterIndex, ReadingLevel readingLevel) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CHAPTER_INDEX, chapterIndex);
        bundle.putSerializable(ARG_READING_LEVEL, readingLevel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        int chapterIndex = getArguments().getInt(ARG_CHAPTER_INDEX);
        Log.i(getClass().getName(), "chapterIndex: " + chapterIndex);

        // Fetch the StoryBookChapter
        storyBookChapter = ChapterPagerAdapter.storyBookChapters.get(chapterIndex);
        Log.i(getClass().getName(), "storyBookChapter: " + storyBookChapter);

        // Fetch the Text-to-Speech (TTS) engine which has already been initialized
        BaseApplication baseApplication = (BaseApplication) getActivity().getApplication();
        tts = baseApplication.getTTS();
    }

    public int getRootLayout() {
        return R.layout.fragment_storybook;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreateView");

        final View root = inflater.inflate(getRootLayout(), container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        chapterRecyclerView = root.findViewById(R.id.chapter_text);

        // Set chapter image
        ImageGson chapterImage = storyBookChapter.getImage();
        if (chapterImage != null) {
            ImageView imageView = root.findViewById(R.id.chapter_image);
            File imageFile = new File(PICTURES_PATH +
                    chapterImage.getId() + "_r" + chapterImage.getRevisionNumber() + "." + chapterImage.getImageFormat().toString().toLowerCase());
            Uri imageFileUri = Uri.fromFile(imageFile);
            Log.i(getClass().getName(), "imageFileUri: " + imageFileUri);
            imageView.setImageURI(imageFileUri);
        }

        // Set paragraph(s)
        List<StoryBookParagraphGson> storyBookParagraphGsons = storyBookChapter.getStoryBookParagraphs();
        Log.i(getClass().getName(), "storyBookChapter.getStoryBookParagraphs(): " + storyBookParagraphGsons);

        if (storyBookParagraphGsons != null) {

            ReadingLevel readingLevel = (ReadingLevel) getArguments().get(ARG_READING_LEVEL);
            readingLevelPosition = (readingLevel == null) ? 0 : readingLevel.ordinal();

            WordAdapter wordAdapter = new WordAdapter(readingLevelPosition, new WordAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(WordGson wordWithAudio, View view, int position) {
                    Log.i(getClass().getName(), "onClick");
                    Log.i(getClass().getName(), "word.getText(): \"" + wordWithAudio.getText() + "\"");

                    WordDialogFragment.newInstance(wordWithAudio.getId()).show(getActivity().getSupportFragmentManager(), "dialog");

                    AudioGson audioGson = ContentProviderHelper.getAudioGsonByTranscription(wordWithAudio.getText().toLowerCase(), getContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
                    Log.i(getClass().getName(), "audioGson: " + audioGson);
                    if (audioGson != null) {
                        playAudioFile(audioGson);
                    } else {
                        // Fall back to TTS
                        tts.speak(wordWithAudio.getText(), TextToSpeech.QUEUE_FLUSH, null, "word_" + wordWithAudio.getId());
                    }

                    // Report learning event to the Analytics application (https://github.com/elimu-ai/analytics)
                    LearningEventUtil.reportWordLearningEvent(wordWithAudio, LearningEventType.WORD_PRESSED, getContext(), BuildConfig.ANALYTICS_APPLICATION_ID);
                }
            });

            if (chapterRecyclerView != null) {
                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
                layoutManager.setFlexDirection(FlexDirection.ROW);
                layoutManager.setJustifyContent(JustifyContent.CENTER);
                chapterRecyclerView.setLayoutManager(layoutManager);
                chapterRecyclerView.setAdapter(wordAdapter);
            }

            for (int paragraphIndex = 0; paragraphIndex < storyBookParagraphGsons.size(); paragraphIndex++) {
                Log.i(getClass().getName(), "storyBookParagraphGson.getOriginalText(): \"" + storyBookParagraphGsons.get(paragraphIndex).getOriginalText() + "\"");

                String originalText = storyBookParagraphGsons.get(paragraphIndex).getOriginalText();
                String[] wordsInOriginalText = originalText.trim().split(" ");
                Log.i(getClass().getName(), "wordsInOriginalText.length: " + wordsInOriginalText.length);
                Log.i(getClass().getName(), "Arrays.toString(wordsInOriginalText): " + Arrays.toString(wordsInOriginalText));

                if (!TextUtils.isEmpty(chapterText)) {
                    chapterText += "\n\n";
                }
                chapterText += originalText;

                List<WordGson> wordAudios = storyBookParagraphGsons.get(paragraphIndex).getWords();
                Log.i(getClass().getName(), "words: " + wordAudios);

                wordAdapter.addParagraph(Arrays.asList(wordsInOriginalText), wordAudios);
            }
        } else {
            fab.setVisibility(View.GONE);
        }

        // Add button for initializing Text-to-Speech (TTS)
        final String finalChapterText = chapterText;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "onClick");
                playAudio(finalChapterText, ChapterFragment.this);
            }
        });

        return root;
    }

    public void playAudio(final String chapterText, final AudioListener audioListener) {
        List<StoryBookParagraphGson> storyBookParagraphs = storyBookChapter.getStoryBookParagraphs();
        StoryBookParagraphGson storyBookParagraphGson = storyBookParagraphs.get(0);
        String transcription = storyBookParagraphGson.getOriginalText();
        Log.i(getClass().getName(), "transcription: \"" + transcription + "\"");
        AudioGson audioGson = ContentProviderHelper.getAudioGsonByTranscription(transcription, getContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
        Log.i(getClass().getName(), "audioGson: " + audioGson);
        if (audioGson != null) {
            playAudioFile(audioGson);
        } else {
            // Fall back to TTS
            tts.setOnUtteranceProgressListener(getUtteranceProgressListener(audioListener));

            Log.i(getClass().getName(), "chapterText: \"" + chapterText + "\"");
            tts.speak(chapterText, TextToSpeech.QUEUE_FLUSH, null, "0");
        }
    }

    public UtteranceProgressListener getUtteranceProgressListener(AudioListener _audioListener) {

        final int[] wordPosition = {-1};

        return new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
                Log.i(getClass().getName(), "onStart");
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                Log.i(getClass().getName(), "onRangeStart");
                super.onRangeStart(utteranceId, start, end, frame);

                Log.i(getClass().getName(), "utteranceId: " + utteranceId + ", start: " + start + ", end: " + end);

                // Highlight the word being spoken
                if (wordPosition[0] > -1) {
                    chapterRecyclerView.getLayoutManager().getChildAt(wordPosition[0]).setBackground(getResources().getDrawable(R.drawable.bg_word_selector));
                }

                wordPosition[0]++;
                if (chapterRecyclerView.getAdapter().getItemViewType(wordPosition[0]) == WordAdapter.NEW_PARAGRAPH_TYPE) {
                    wordPosition[0]++;
                }

                chapterRecyclerView.getLayoutManager().getChildAt(wordPosition[0]).setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void onDone(String utteranceId) {
                Log.i(getClass().getName(), "onDone");

                // Remove highlighting of the last spoken word
                chapterRecyclerView.getLayoutManager().getChildAt(wordPosition[0]).setBackground(getResources().getDrawable(R.drawable.bg_word_selector));
            }

            @Override
            public void onError(String utteranceId) {
                Log.i(getClass().getName(), "onError");
            }
        };
    }

    private void playAudioFile(AudioGson audioGson) {
        File audioFile = new File(MUSIC_PATH +
                audioGson.getId() + "_r" + audioGson.getRevisionNumber() + "." + audioGson.getAudioFormat().toString().toLowerCase());
        Log.i(getClass().getName(), "audioFile: " + audioFile);
        Log.i(getClass().getName(), "audioFile.exists(): " + audioFile.exists());
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFile.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(getClass().getName(), null, e);
        }
    }

    @Override
    public void onAudioDone() {
    }
}
