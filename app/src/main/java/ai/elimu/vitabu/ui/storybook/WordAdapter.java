package ai.elimu.vitabu.ui.storybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.elimu.model.v2.gson.content.WordGson;
import ai.elimu.vitabu.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

class WordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int WORD_TYPE = 0;
    public static final int NEW_PARAGRAPH_TYPE = 1;

    private final int readingLevelPosition;

    private final OnItemClickListener listener;
    private final List<String> wordsInOriginalText = new ArrayList<>();
    private final List<WordGson> wordAudios = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(WordGson wordGson, View view, int position);
    }

    public WordAdapter(int readingLevelPosition, OnItemClickListener listener) {
        this.readingLevelPosition = readingLevelPosition;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_layout, parent, false);

        if (viewType == WORD_TYPE) {
            return new WordViewHolder(view);
        } else {
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == WORD_TYPE) {
            ((WordViewHolder) holder).paintWordLayout(wordsInOriginalText.get(position), wordAudios.get(position), readingLevelPosition);

            if (wordAudios.get(position) != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(wordAudios.get(position), v, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return wordsInOriginalText.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (wordsInOriginalText.get(position) == null) {
            return NEW_PARAGRAPH_TYPE;
        } else {
            return WORD_TYPE;
        }
    }

    public void addParagraph(List<String> wordsInOriginalText, List<WordGson> wordAudios) {
        //Words
        this.wordsInOriginalText.addAll(wordsInOriginalText);
        this.wordsInOriginalText.add(null);

        //Audios
        if (wordAudios == null) {
            this.wordAudios.addAll(Collections.<WordGson>nCopies(wordsInOriginalText.size(), null));
        } else {
            this.wordAudios.addAll(wordAudios);
        }
        this.wordAudios.add(null);

        notifyDataSetChanged();
    }


    public static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.getLayoutParams().width = MATCH_PARENT;
        }
    }

    
    public static class WordViewHolder extends RecyclerView.ViewHolder {

        private final TextView wordText;
        private final View wordUnderline;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.word_text);
            wordUnderline = itemView.findViewById(R.id.word_underline);
        }

        public void paintWordLayout(String word, WordGson wordWithAudio, int readingLevelPosition) {
            paintWord(word, readingLevelPosition);
            paintUnderline(wordWithAudio);
        }

        private void paintWord(String word, int readingLevelPosition) {
            wordText.setText(word);
            setTextSizeByLevel(wordText, readingLevelPosition);

            if (word.isEmpty()) {
                itemView.getLayoutParams().width = 0;
            }
        }

        private void setTextSizeByLevel(TextView textView, int readingLevelPosition) {
            int[] fontSize = itemView.getContext().getResources().getIntArray(R.array.chapter_text_font_size);
            String[] letterSpacing = itemView.getContext().getResources().getStringArray(R.array.chapter_text_letter_spacing);
            int[] lineSpacing = itemView.getContext().getResources().getIntArray(R.array.chapter_text_line_spacing_recyclerview);

            ((FlexboxLayoutManager.LayoutParams) itemView.getLayoutParams()).bottomMargin = lineSpacing[readingLevelPosition];

            textView.setTextSize(fontSize[readingLevelPosition]);
            textView.setLetterSpacing(Float.parseFloat(letterSpacing[readingLevelPosition]));
        }

        private void paintUnderline(WordGson wordWithAudio) {
            // Underline clickable Words
            if (wordWithAudio == null) {
                wordUnderline.setVisibility(GONE);
            } else {
                wordUnderline.setVisibility(VISIBLE);
            }
        }
    }
}
