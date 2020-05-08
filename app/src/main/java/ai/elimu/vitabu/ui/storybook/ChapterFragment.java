package ai.elimu.vitabu.ui.storybook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ai.elimu.model.enums.analytics.LearningEventType;
import ai.elimu.model.enums.content.ImageFormat;
import ai.elimu.model.v2.gson.content.StoryBookChapterGson;
import ai.elimu.model.v2.gson.content.StoryBookParagraphGson;
import ai.elimu.model.v2.gson.content.WordGson;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ChapterFragment extends Fragment {

    private static final String ARG_CHAPTER_INDEX = "chapter_index";

    private StoryBookChapterGson storyBookChapter;

    private TextToSpeech tts;

    private String chapterText;

    public static ChapterFragment newInstance(int chapterIndex) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CHAPTER_INDEX, chapterIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        Integer chapterIndex = getArguments().getInt(ARG_CHAPTER_INDEX);
        Log.i(getClass().getName(), "chapterIndex: " + chapterIndex);

        // Fetch the StoryBookChapter
        storyBookChapter = ChapterPagerAdapter.storyBookChapters.get(chapterIndex);
        Log.i(getClass().getName(), "storyBookChapter: " + storyBookChapter);

        // Initialize Text-to-Speech
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i(getClass().getName(), "onInit");
//                tts.setLanguage(new Locale("hin"));
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreateView");

        final View root = inflater.inflate(R.layout.fragment_storybook, container, false);

        // Set cover image
        if (storyBookChapter.getImage() != null) {
            GifImageView gifImageView = root.findViewById(R.id.chapter_image);
            byte[] imageBytes = storyBookChapter.getImage().getBytes();
            if (storyBookChapter.getImage().getImageFormat() == ImageFormat.GIF) {
                try {
                    GifDrawable gifDrawable = new GifDrawable(imageBytes);
                    gifImageView.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    Log.e(getClass().getName(), null, e);
                }
            } else {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                gifImageView.setImageBitmap(bitmap);
            }
        }

        // Set paragraph(s)
        Log.i(getClass().getName(), "storyBookChapter.getStoryBookParagraphs(): " + storyBookChapter.getStoryBookParagraphs());
        if (storyBookChapter.getStoryBookParagraphs() != null) {
            chapterText = "";
            for (StoryBookParagraphGson storyBookParagraphGson : storyBookChapter.getStoryBookParagraphs()) {
                Log.i(getClass().getName(), "storyBookParagraphGson.getOriginalText(): \"" + storyBookParagraphGson.getOriginalText() + "\"");
                if (!TextUtils.isEmpty(chapterText)) {
                    chapterText += "\n\n";
                }
                chapterText += storyBookParagraphGson.getOriginalText();
            }

            chapterText = chapterText.toLowerCase();

            TextView textView = root.findViewById(R.id.chapter_text);
            textView.setText(chapterText);
            textView.setVisibility(View.VISIBLE);
        }

        // Underline clickable Words
        if (storyBookChapter.getStoryBookParagraphs() != null) {
            for (StoryBookParagraphGson storyBookParagraphGson : storyBookChapter.getStoryBookParagraphs()) {
                List<WordGson> wordGsons = storyBookParagraphGson.getWords();
                Log.i(getClass().getName(), "wordGsons: " + wordGsons);
                if (wordGsons != null) {
                    Log.i(getClass().getName(), "wordGsons.size(): " + wordGsons.size());
                    String[] wordsInOriginalText = storyBookParagraphGson.getOriginalText().trim().split(" ");
                    Log.i(getClass().getName(), "wordsInOriginalText.length: " + wordsInOriginalText.length);
                    Log.i(getClass().getName(), "Arrays.toString(wordsInOriginalText): " + Arrays.toString(wordsInOriginalText));

                    Spannable spannable = new SpannableString(chapterText);

                    // Add Spannables
                    int spannableStart = 0;
                    int spannableEnd = 0;
                    for (int i = 0; i < wordsInOriginalText.length; i++) {
                        String wordInOriginalText = wordsInOriginalText[i];
                        spannableEnd += wordInOriginalText.length();

                        final WordGson wordGson = wordGsons.get(i);
                        if (wordGson != null) {
                            Log.i(getClass().getName(), "Adding UnderlineSpan for \"" + wordGson.getText() + "\"");
                            Log.i(getClass().getName(), "chapterText.substring(spannableStart, spannableEnd): \"" + chapterText.substring(spannableStart, spannableEnd) + "\"");

                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View widget) {
                                    Log.i(getClass().getName(), "onClick");
                                    Log.i(getClass().getName(), "wordGson.getText(): \"" + wordGson.getText() + "\"");

                                    Toast.makeText(getContext(), wordGson.getText(), Toast.LENGTH_LONG).show();
                                    tts.speak(wordGson.getText(), TextToSpeech.QUEUE_FLUSH, null, "word_" + wordGson.getId());

                                    // Report WordBookLearningEvent to the Analytics application
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setPackage(BuildConfig.ANALYTICS_APPLICATION_ID);
                                    broadcastIntent.setAction("ai.elimu.intent.action.WORD_LEARNING_EVENT");
                                    broadcastIntent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                                    broadcastIntent.putExtra("wordId", wordGson.getId());
                                    broadcastIntent.putExtra("wordText", wordGson.getText());
                                    broadcastIntent.putExtra("learningEventType", LearningEventType.WORD_PRESSED.toString());
                                    getActivity().sendBroadcast(broadcastIntent);
                                }
                            };
                            spannable.setSpan(clickableSpan, spannableStart, spannableEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        spannableStart += wordInOriginalText.length() + 1; // +1 for the whitespace
                        spannableEnd += 1; // +1 for the whitespace
                    }

                    final TextView textView = root.findViewById(R.id.chapter_text);
                    textView.setText(spannable);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        }

        // Add button for initializing Text-to-Speech (TTS)
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "onClick");

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
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
                        Spannable spannable = new SpannableString(chapterText);
                        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(getResources().getColor(R.color.colorAccent));
                        spannable.setSpan(backgroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        final TextView textView = root.findViewById(R.id.chapter_text);
                        textView.setText(spannable);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.i(getClass().getName(), "onDone");

                        // Remove highlighting of the last spoken word
                        final TextView textView = root.findViewById(R.id.chapter_text);
                        textView.setText(chapterText);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.i(getClass().getName(), "onError");
                    }
                });

                Log.i(getClass().getName(), "chapterText: \"" + chapterText + "\"");
                tts.speak(chapterText, TextToSpeech.QUEUE_FLUSH, null, "0");
            }
        });

        return root;
    }
}
