package com.kothein.mmpoetry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.kothein.mmpoetry.activities.FullScreenImageActivity;
import com.kothein.mmpoetry.model.Book;
import com.kothein.mmpoetry.model.ContentElement;
import com.kothein.mmpoetry.model.FormatElement;
import com.kothein.mmpoetry.model.TableData;
import com.kothein.mmpoetry.photoview.PhotoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookDetailsFragment extends Fragment {
    private Book book;
    private int defaultTextSize = 18;
    private LinearLayout contentLayout;
    private TextView headingTextView;
    private TextView subheadingTextView;
    private TextView paragraphTextView;
    private TextView listItemTextView;
    private TextView cellTextView;
    private TextView captionTextView;
    private int DEFAULT_TEXT_SIZE = 18;
    private MediaPlayer currentlyPlayingMediaPlayer;
    private int currentTextSize = defaultTextSize; // Initialize with the default text size
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("textSize", currentTextSize);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            currentTextSize = savedInstanceState.getInt("textSize", defaultTextSize);
            // Call your updateTextSize method here to apply the saved text size
            updateTextSize(TypedValue.COMPLEX_UNIT_SP,currentTextSize);
        }
    }
    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("book", book);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable("book");
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        // Load custom font from resources.
        // If you want to use different fonts for each case, add this line using different names and font (headingFont, subHeadingFont, etc.)
        Typeface defaultCustomFont = ResourcesCompat.getFont(requireContext(), R.font.pyidaungsu);

        int defaultTextSize = retrieveSavedTextSize();

        // Declare contentLayout as a class-level variable
        contentLayout = view.findViewById(R.id.contentLayout);
        contentLayout.removeAllViews();

        for (ContentElement element : book.getContent()) {
            switch (element.getType()) {
                case "heading":
                    headingTextView = new TextView(requireContext());
                    headingTextView.setText(element.getText());
                    //headingTextView.setTextSize(24);
                    headingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,defaultTextSize);
                    headingTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorHeading));
                    int paddingHeadingInPixels = 50; // You can adjust this value as needed
                    headingTextView.setPadding(0, paddingHeadingInPixels, 0, 0);
                    Typeface headingFont = ResourcesCompat.getFont(requireContext(), R.font.nksmart4);
                    headingTextView.setTypeface(headingFont);
                    headingTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    contentLayout.addView(headingTextView);
                    break;

                case "subheading":
                    subheadingTextView = new TextView(requireContext());
                    subheadingTextView.setText(element.getText());
                    subheadingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,defaultTextSize);
                    subheadingTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorSubHeading));
                    Typeface subheadingFont = ResourcesCompat.getFont(requireContext(), R.font.nksmart4);
                    subheadingTextView.setTypeface(subheadingFont);
                    int paddingSubheadingInPixels = 50; // You can adjust this value as needed
                    subheadingTextView.setPadding(10, paddingSubheadingInPixels, 0, 0);
                    contentLayout.addView(subheadingTextView);
                    break;


                case "paragraph":
                   paragraphTextView = new TextView(requireContext());
                    String paragraphText = element.getText();

                    // Apply inline formatting from the "format" method
                    if (element.getFormat() != null && !element.getFormat().isEmpty()) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(paragraphText);

                        for (FormatElement formatElement : element.getFormat()) {
                            String word = formatElement.getContent();
                            int startIndex = 0;

                            while (startIndex != -1) {
                                startIndex = paragraphText.indexOf(word, startIndex);

                                if (startIndex != -1) {
                                    int endIndex = startIndex + word.length();

                                    // Apply color if available
                                    if (formatElement.getColor() != null) {
                                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(formatElement.getColor()));
                                        spannableStringBuilder.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }

                                    // Apply style if available
                                    if (formatElement.getStyle() != null && !formatElement.getStyle().isEmpty()) {
                                        for (String style : formatElement.getStyle()) {
                                            switch (style) {
                                                case "bold": {
                                                    StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                                                    spannableStringBuilder.setSpan(styleSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    break;
                                                }
                                                case "italic": {
                                                    StyleSpan styleSpan = new StyleSpan(Typeface.ITALIC);
                                                    spannableStringBuilder.setSpan(styleSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    break;
                                                }
                                                case "underline":
                                                    UnderlineSpan underlineSpan = new UnderlineSpan();
                                                    spannableStringBuilder.setSpan(underlineSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    break;
                                            }
                                        }
                                    }

                                    // Apply font size if available
                                    if (formatElement.getSize() > 0) {
                                        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(formatElement.getSize(), true);
                                        spannableStringBuilder.setSpan(sizeSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }

                                    startIndex = endIndex; // Move to the next occurrence
                                }
                            }
                        }
                        paragraphTextView.setText(spannableStringBuilder);
                    } else {
                        // No formatting, plain text
                        paragraphTextView.setText(paragraphText);
                    }
                    // Set default text size and color
                    paragraphTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorText));
                    paragraphTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,defaultTextSize);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        paragraphTextView.setLineHeight(90);
                    }

                    // Set default padding
                    int paddingParagraphInPixels = 40; // You can adjust this value as needed
                    paragraphTextView.setPadding(10, paddingParagraphInPixels, 0, paddingParagraphInPixels);
                    // Apply custom font
                    paragraphTextView.setTypeface(defaultCustomFont);
                    contentLayout.addView(paragraphTextView);
                    break;

                case "list":
                    LinearLayout listLayout = new LinearLayout(requireContext());
                    listLayout.setOrientation(LinearLayout.VERTICAL);

                    List<String> items = element.getList();
                    if (items != null && !items.isEmpty()) {
                        for (String item : items) {
                           listItemTextView = new TextView(requireContext());
                            listItemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,defaultTextSize);
                            listItemTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorText));
                            Typeface listFont = ResourcesCompat.getFont(requireContext(), R.font.winuniinnwa);
                            listItemTextView.setTypeface(listFont);
                            listItemTextView.setText(item);
                            //listItemTextView.setText("\u2022 " + item); // Adding bullet point (\u2022) to each item
                            int paddingListItemInPixels = 20; // You can adjust this value as needed
                            listItemTextView.setPadding(15, paddingListItemInPixels, 0, paddingListItemInPixels);
                            listLayout.addView(listItemTextView);
                        }
                    }
                    listLayout.setPadding(0, 10, 0, 15);
                    contentLayout.addView(listLayout);
                    break;

                case "table":
                    TableLayout tableLayout = new TableLayout(requireContext());
                    tableLayout.setStretchAllColumns(true);
                    tableLayout.setShrinkAllColumns(true);

                    TableData tableData = element.getTableData(); // Use getTableData() method
                    if (tableData != null && tableData.getData() != null && !tableData.getData().isEmpty()) {
                        for (int rowIndex = 0; rowIndex < tableData.getData().size(); rowIndex++) {
                            List<String> row = tableData.getData().get(rowIndex);
                            TableRow tableRow = new TableRow(requireContext());

                            for (int cellIndex = 0; cellIndex < row.size(); cellIndex++) {
                                String cell = row.get(cellIndex);
                                cellTextView = new TextView(requireContext());
                                cellTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,defaultTextSize);
                                cellTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorText));
                                Typeface cellFont = ResourcesCompat.getFont(requireContext(), R.font.pyidaungsu);
                                cellTextView.setTypeface(cellFont);
                                cellTextView.setPadding(16, 20, 16, 30); // You can adjust this padding as needed

                                // Apply different styles for table headers (first row)
                                if (rowIndex == 0) {
                                    SpannableStringBuilder headerSpannable = new SpannableStringBuilder(cell);
                                    headerSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cell.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    //headerSpannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, cell.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    cellTextView.setText(headerSpannable);
                                } else {
                                    cellTextView.setText(cell);
                                }

                                tableRow.addView(cellTextView);
                            }

                            tableLayout.setPadding(5, 0, 0, 10);
                            tableLayout.addView(tableRow);
                        }
                    }
                    contentLayout.addView(tableLayout);
                    break;

                case "image":
                    // Create and configure the TextView for the caption
                    captionTextView = new TextView(requireContext());
                    captionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,defaultTextSize);
                    captionTextView.setTypeface(defaultCustomFont);
                    captionTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorCaption));
                    captionTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    int paddingCaptionInPixels = 10; // You can adjust this value as needed
                    captionTextView.setPadding(10, 0, 0, paddingCaptionInPixels);
                    captionTextView.setText(element.getCaption()); // Set the caption text
                    contentLayout.addView(captionTextView);
                    //GestureImageView photoView = new GestureImageView(requireContext());
                    // Create a PhotoView instead of a regular ImageView
                    PhotoView photoView = new PhotoView(requireContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, 0);
                    photoView.setLayoutParams(layoutParams);

                    String imageUrl = "file:///android_asset/" + element.getUrl();
                    // Load the image using Glide
                    Glide.with(this)
                            .load(imageUrl) // Use Uri to specify the asset path
                            .into(photoView);

                    // Add a click listener to open the image in a full view with zoom
                    photoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           String imageUrl = "file:///android_asset/" + element.getUrl();
                            // Open the image in a full-screen zoomable view
                            Intent intent = new Intent(requireContext(), FullScreenImageActivity.class);
                            intent.putExtra("image_url", imageUrl);
                            startActivity(intent);
                        }
                    });
                    // Add the PhotoView to the contentLayout
                    contentLayout.addView(photoView);
                    break;


                case "audio":
                        // Create a new MediaPlayer instance for each button
                        MediaPlayer mediaPlayer = new MediaPlayer();

                        try {
                            AssetFileDescriptor afd = requireContext().getAssets().openFd(element.getUrl());
                            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            afd.close();

                            mediaPlayer.setOnPreparedListener(mp -> {
                                // Media is prepared, but do not start playback yet
                            });
                            mediaPlayer.prepareAsync(); // Asynchronous preparation
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParamsBtn.setMargins(0, 10, 0, 10);
                        // Create a Button for play/pause
                        Button playPauseButton = new Button(requireContext());
                        playPauseButton.setLayoutParams(layoutParamsBtn);
                        playPauseButton.setTextSize(24);

                        // Set the button text from the JSON
                        String buttonText = element.getText();
                        playPauseButton.setText(buttonText);

                        playPauseButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorSubHeading));
                        playPauseButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorItemBackground));
                        playPauseButton.offsetTopAndBottom(50);
                    playPauseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (currentlyPlayingMediaPlayer != null) {
                                currentlyPlayingMediaPlayer.pause();
                            }

                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                            } else {
                                mediaPlayer.start();
                                currentlyPlayingMediaPlayer = mediaPlayer;
                            }
                        }
                    });
                    contentLayout.addView(playPauseButton);
                        break;
            }
        }
        return view;
    }
    public void updateTextSize(int unit, int textSize) {
        updateTextSizeRecursive(contentLayout, unit, textSize);
    }
    private void updateTextSizeRecursive(View view, int unit, int textSize) {
        if (view instanceof TextView) {
            // If the view is a TextView, update its text size
            TextView textView = (TextView) view;
            textView.setTextSize(unit, textSize);
        } else if (view instanceof ViewGroup) {
            // If the view is a ViewGroup (e.g., LinearLayout, TableLayout), recursively search its children
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View childView = viewGroup.getChildAt(i);
                updateTextSizeRecursive(childView, unit, textSize);
            }
        }
    }
    private int retrieveSavedTextSize() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("text_size", DEFAULT_TEXT_SIZE); // Set a default text size here
    }
    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    public void releaseMediaPlayer() {
        if (currentlyPlayingMediaPlayer != null) {
            if (currentlyPlayingMediaPlayer.isPlaying()) {
                currentlyPlayingMediaPlayer.stop();
            }
            currentlyPlayingMediaPlayer.release();
            currentlyPlayingMediaPlayer = null;
        }
    }
}
