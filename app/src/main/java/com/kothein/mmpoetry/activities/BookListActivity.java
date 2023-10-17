package com.kothein.mmpoetry.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kothein.mmpoetry.R;
import com.kothein.mmpoetry.ThemePreferenceHelper;
import com.kothein.mmpoetry.animation.Animatool;
import com.kothein.mmpoetry.model.Book;
import com.kothein.mmpoetry.model.Category;

public class BookListActivity extends AppCompatActivity {

    private List<Category> categories;
    private int selectedCategoryIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the saved theme
        int selectedTheme = ThemePreferenceHelper.getTheme(this);
        AppCompatDelegate.setDefaultNightMode(selectedTheme);

        setContentView(R.layout.activity_book_list);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        // Set the toolbar title to the selected category's name
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back navigation
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String jsonData = intent.getStringExtra("json_data");
        selectedCategoryIndex = intent.getIntExtra("category_index", -1);
        int categoryID = intent.getIntExtra("category_id", -1);

        if (jsonData == null || selectedCategoryIndex == -1) {
            finish(); // Invalid data or category index, close the activity
            return;
        }

        Type categoryListType = new TypeToken<List<Category>>(){}.getType();
        categories = new Gson().fromJson(jsonData, categoryListType);

        if (selectedCategoryIndex >= 0 && selectedCategoryIndex < categories.size()) {
            Category selectedCategory = categories.get(selectedCategoryIndex);
            if (selectedCategory != null) {
                String selectedCategoryName = selectedCategory.getName();
                getSupportActionBar().setTitle(selectedCategoryName);
            }
        } else {
            finish(); // Invalid category index, close the activity
        }

        Category selectedCategory = categories.get(selectedCategoryIndex);

        ListView bookListView = findViewById(R.id.bookListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_book, getBookTitles(selectedCategory));
        bookListView.setAdapter(adapter);

        // Load the animation resource
        Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.list_item_enter);

        // Create a LayoutAnimationController with the animation
        LayoutAnimationController controller = new LayoutAnimationController(enterAnimation);
        controller.setDelay(0.3f); // Delay between items (adjust as needed)

        // Set the controller to the ListView
        bookListView.setLayoutAnimation(controller);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Retrieve the selected book from the selected category
                Book selectedBook = selectedCategory.getBooks().get(position);
                // Pass the book details to the BookDetailsActivity
                Intent intent = new Intent(BookListActivity.this, BookDetailsActivity.class);
                intent.putExtra("json_data", jsonData); // Pass the JSON data again to BookDetailsActivity
                intent.putExtra("category_index", selectedCategoryIndex);
                intent.putExtra("book_index", position);
                startActivity(intent);
                Animatool.animateSlideLeft(BookListActivity.this);
            }
        });
    }
    private String[] getBookTitles(Category category) {
        String[] bookTitles = new String[category.getBooks().size()];
        for (int i = 0; i < category.getBooks().size(); i++) {
            bookTitles[i] = category.getBooks().get(i).getTitle();
        }
        return bookTitles;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back navigation when the navigation icon (up button) is clicked
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();
        Animatool.animateSwipeRight(BookListActivity.this);
    }
}