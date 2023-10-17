package com.kothein.mmpoetry.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kothein.mmpoetry.BookDetailsFragment;
import com.kothein.mmpoetry.BookDetailsPagerAdapter;
import com.kothein.mmpoetry.R;

import com.kothein.mmpoetry.ThemePreferenceHelper;
import com.kothein.mmpoetry.animation.Animatool;
import com.kothein.mmpoetry.model.Book;
import com.kothein.mmpoetry.model.Category;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class BookDetailsActivity extends AppCompatActivity{
    private float currentTextSize = 18.0f; // Default text size
    private ViewPager2 viewPager;
    private List<Book> books;
    private boolean isFullScreen = false;
    private MediaPlayer mediaPlayer;
    private float scaleFactor = 1.0f;
    private BookDetailsPagerAdapter pagerAdapter;
    private Book selectedBook;
    private static final int MICRO_TEXT_SIZE = 12;
    private static final int TINY_TEXT_SIZE = 16;
    private static final int SMALL_TEXT_SIZE = 18;
    private static final int NORMAL_TEXT_SIZE = 20;
    private static final int MEDIUM_TEXT_SIZE = 24;
    private static final int BIG_TEXT_SIZE = 28;
    private static final int LARGE_TEXT_SIZE = 32;
    private MediaPlayer currentlyPlayingMediaPlayer = null;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the saved theme
        int selectedTheme = ThemePreferenceHelper.getTheme(this);
        AppCompatDelegate.setDefaultNightMode(selectedTheme);

        setContentView(R.layout.activity_book_details);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp: :MyWakelockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));


        // Retrieve the data passed from BookListActivity
        Intent intent = getIntent();
        String json = intent.getStringExtra("json_data");
        int selectedBookIndex = intent.getIntExtra("book_index", -1);
        int selectedCategoryIndex = intent.getIntExtra("category_index", -1);
        // Parse JSON using Gson
        Type categoryListType = new TypeToken<List<Category>>(){}.getType();
        List<Category> categories = new Gson().fromJson(json, categoryListType);

        if (selectedBookIndex == -1 || selectedBookIndex >= categories.size()) {
            finish(); // Invalid book index, close the activity
            return;
        }

        Category selectedCategory = categories.get(selectedCategoryIndex);
        books = selectedCategory.getBooks();
        // Set the toolbar title to the selected book's name
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new BookDetailsPagerAdapter(this, books);
        viewPager.setAdapter(pagerAdapter);

        // Set the initial page to the selected book
        viewPager.setCurrentItem(selectedBookIndex);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Update the toolbar title when the page changes
                if (position >= 0 && position < books.size()) {
                    selectedBook = books.get(position);
                    Objects.requireNonNull(getSupportActionBar()).setTitle(selectedBook.getTitle());
                }
            }
        });
        // Set the initial toolbar title to the title of the first book
        if (!books.isEmpty()) {
            Book initialBook = books.get(selectedBookIndex);
            Objects.requireNonNull(getSupportActionBar()).setTitle(initialBook.getTitle());
        }
    }

    private BookDetailsFragment getCurrentFragment() {
        // Get the current fragment using ViewPager2
        int currentItem = viewPager.getCurrentItem();
        return pagerAdapter.getCurrentFragment(currentItem);
    }
    private boolean isManualOrientation = false;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_fullscreen) {
            toggleFullScreen();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed(); // Handle back navigation when the up button is clicked
            return true;
        } else if (id == R.id.menu_rotate_screen) {
            Toast.makeText(this, getResources().getString(R.string.manual_screen_orientation_enabled), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_auto) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            return true;
        } else if (id == R.id.menu_portrait) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
        } else if (id == R.id.menu_landscape) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                return true;
        } else if (id == R.id.menu_change_text_size) {
                Toast.makeText(this, getResources().getString(R.string.manual_text_size_change_enabled), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.text_size_micro) {
                // Update the text size of the current fragment
                BookDetailsFragment currentFragment = getCurrentFragment();
                if (currentFragment != null) {
                    currentFragment.updateTextSize(TypedValue.COMPLEX_UNIT_SP, MICRO_TEXT_SIZE);
                    saveTextSizePreference(MICRO_TEXT_SIZE);
                    return true;
                }
        } else if (id == R.id.text_size_tiny) {
                BookDetailsFragment currentFragment = getCurrentFragment();
                if (currentFragment != null) {
                    currentFragment.updateTextSize(TypedValue.COMPLEX_UNIT_SP, TINY_TEXT_SIZE);
                    saveTextSizePreference(TINY_TEXT_SIZE);
                    return true;
                }
        } else if (id == R.id.text_size_small) {
                BookDetailsFragment currentFragment = getCurrentFragment();
                if (currentFragment != null) {
                    currentFragment.updateTextSize(TypedValue.COMPLEX_UNIT_SP, SMALL_TEXT_SIZE);
                    saveTextSizePreference(SMALL_TEXT_SIZE);
                    return true;
                }
        } else if (id == R.id.text_size_normal) {
                // Update the text size of the current fragment
                BookDetailsFragment currentFragment = getCurrentFragment();
                if (currentFragment != null) {
                    currentFragment.updateTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_TEXT_SIZE);
                    saveTextSizePreference(NORMAL_TEXT_SIZE);
                    return true;
                }
        } else if (id == R.id.text_size_medium) {
                // Update the text size of the current fragment
                BookDetailsFragment currentFragment = getCurrentFragment();
                if (currentFragment != null) {
                    currentFragment.updateTextSize(TypedValue.COMPLEX_UNIT_SP, MEDIUM_TEXT_SIZE);
                    saveTextSizePreference(MEDIUM_TEXT_SIZE);
                    return true;
                }
        } else if (id == R.id.text_size_big) {
                // Update the text size of the current fragment
                BookDetailsFragment currentFragment = getCurrentFragment();
                if (currentFragment != null) {
                    currentFragment.updateTextSize(TypedValue.COMPLEX_UNIT_SP, BIG_TEXT_SIZE);
                    saveTextSizePreference(BIG_TEXT_SIZE);
                    return true;
                }
        } else if (id == R.id.text_size_large) {
                // Update the text size of the current fragment
                BookDetailsFragment currentFragment = getCurrentFragment();
                if (currentFragment != null) {
                    currentFragment.updateTextSize(TypedValue.COMPLEX_UNIT_SP, LARGE_TEXT_SIZE);
                    saveTextSizePreference(LARGE_TEXT_SIZE);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTextSizePreference(int textSize) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("text_size", textSize);
        editor.apply();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isManualOrientation) {
            // Handle automatic orientation changes here, if needed
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
    private void toggleFullScreen() {
        if (getSupportActionBar() != null) {
            if (isFullScreen) {
                // Exit fullscreen mode
                getSupportActionBar().show();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                isFullScreen = false;
            } else {
                // Enter fullscreen mode
                getSupportActionBar().hide();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                isFullScreen = true;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_details, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            // Handle full-screen exit
            toggleFullScreen();
        } else {
            if (currentlyPlayingMediaPlayer != null) {
            currentlyPlayingMediaPlayer.release();
            currentlyPlayingMediaPlayer = null;
        }
            wakeLock.release();
            super.onBackPressed();
            Animatool.animateSwipeRight(BookDetailsActivity.this);
        }
    }
}
