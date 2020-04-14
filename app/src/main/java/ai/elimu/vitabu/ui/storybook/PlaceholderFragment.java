package ai.elimu.vitabu.ui.storybook;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import ai.elimu.model.gson.content.multimedia.ImageGson;
import ai.elimu.vitabu.BuildConfig;
import ai.elimu.vitabu.R;
import ai.elimu.vitabu.util.CursorToImageGsonConverter;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_CHAPTER_NUMBER = "chapter_number";

    private ChapterViewModel chapterViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CHAPTER_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        chapterViewModel = ViewModelProviders.of(this).get(ChapterViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_CHAPTER_NUMBER);
        }
        chapterViewModel.setIndex(index);

        List<ImageGson> images = new ArrayList<>();
        Uri uri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID + ".provider.image_provider/image/1");
        Log.i(getClass().getName(), "uri: " + uri);
        Cursor imageCursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (imageCursor == null) {
            Log.e(getClass().getName(), "imageCursor == null");
            Toast.makeText(getContext(), "imageCursor == null", Toast.LENGTH_LONG).show();
        } else {
            Log.i(getClass().getName(), "imageCursor.getCount(): " + imageCursor.getCount());
            if (imageCursor.getCount() == 0) {
                Log.e(getClass().getName(), "imageCursor.getCount() == 0");
            } else {
                Log.i(getClass().getName(), "imageCursor.getCount(): " + imageCursor.getCount());

                imageCursor.moveToFirst();

                // Convert from database row to ImageGson object
                ImageGson imageGson = CursorToImageGsonConverter.getImage(imageCursor);

                imageCursor.close();
                Log.i(getClass().getName(), "cursor.isClosed(): " + imageCursor.isClosed());

                images.add(imageGson);
            }
        }
        chapterViewModel.setImages(images);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreateView");

        View root = inflater.inflate(R.layout.fragment_storybook, container, false);

        final ImageView imageView = root.findViewById(R.id.chapter_image);
        chapterViewModel.getImage().observe(getViewLifecycleOwner(), new Observer<ImageGson>() {
            @Override
            public void onChanged(ImageGson imageGson) {
                Log.i(getClass().getName(), "onChanged");
                byte[] bytes = imageGson.getBytes();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });

        final TextView textView = root.findViewById(R.id.chapter_text);
        chapterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String text) {
                Log.i(getClass().getName(), "onChanged");
                textView.setText(text);
            }
        });

        return root;
    }
}
