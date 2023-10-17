package com.kothein.mmpoetry.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kothein.mmpoetry.R;
import com.kothein.mmpoetry.ThemePreferenceHelper;
import com.kothein.mmpoetry.animation.Animatool;
import com.kothein.mmpoetry.model.Category;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
public class MainActivityClone extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private List<Category> categories;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();

        int selectedTheme = ThemePreferenceHelper.getTheme(this);
        AppCompatDelegate.setDefaultNightMode(selectedTheme);

        setContentView(R.layout.activity_main);

        // Set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        String json = loadJSONData(); // Load JSON data from the assets folder

        // Parse JSON using Gson
        Type categoryListType = new TypeToken<List<Category>>() {
        }.getType();
        categories = new Gson().fromJson(json, categoryListType);
        ListView categoryListView = findViewById(R.id.categoryListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_category, getCategoryNames());
        categoryListView.setAdapter(adapter);

        // Load the animation resource
        Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.list_item_enter);

        // Create a LayoutAnimationController with the animation
        LayoutAnimationController controller = new LayoutAnimationController(enterAnimation);
        controller.setDelay(0.3f); // Delay between items (adjust as needed)

        // Set the controller to the ListView
        categoryListView.setLayoutAnimation(controller);


        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivityClone.this, BookListActivity.class);
                intent.putExtra("json_data", json); // Pass the JSON data as a string
                intent.putExtra("category_index", position);
                startActivity(intent);
                Animatool.animateSlideLeft(MainActivityClone.this);
            }
        });
    }

    private String loadJSONData() {
        try {
            InputStream inputStream = getAssets().open("poem.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] getCategoryNames() {
        String[] categoryNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            categoryNames[i] = categories.get(i).getName();
        }
        return categoryNames;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_use) {
            startActivity(new Intent(MainActivityClone.this, UseActivity.class));
            return true;
        } else if (itemId == R.id.nav_copyright) {
            startActivity(new Intent(MainActivityClone.this, CopyrightActivity.class));
            return true;
        } else if (itemId == R.id.nav_about) {
            startActivity(new Intent(MainActivityClone.this, AboutActivity.class));
            return true;
        }else if (itemId == R.id.nav_home){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.nav_language) {
            showChangeLanguageDialog();
        } else if (itemId == R.id.nav_theme_toggle) {
            int selectedTheme = ThemePreferenceHelper.getTheme(this);
            int newTheme;

            if (selectedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
                newTheme = AppCompatDelegate.MODE_NIGHT_NO;
            } else {
                newTheme = AppCompatDelegate.MODE_NIGHT_YES;
            }
            // Save the selected theme in SharedPreferences
            ThemePreferenceHelper.saveTheme(this, newTheme);

            // Set the new theme immediately for the current activity
            //AppCompatDelegate.setDefaultNightMode(newTheme);

            // Save the selected theme state in SharedPreferences
            ThemePreferenceHelper.saveThemeState(this, newTheme == AppCompatDelegate.MODE_NIGHT_YES);

            // Recreate the activity to apply the selected theme
            recreate();
            //startActivity(new Intent(MainActivityClone.this, MainActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void showChangeLanguageDialog() {
        final String[] listItems = {getResources().getString(R.string.english), getResources().getString(R.string.myanmar)};
        final String currentLanguage = getCurrentLanguage(); // Get the current language from SharedPreferences
        int selectedLanguageIndex = -1; // Initialize to no selection

        if ("en".equals(currentLanguage)) {
            selectedLanguageIndex = 0; // English is the current language
        } else if ("my".equals(currentLanguage)) {
            selectedLanguageIndex = 1; // Myanmar is the current language
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivityClone.this);
        mBuilder.setTitle(getResources().getString(R.string.choose_language));
        mBuilder.setSingleChoiceItems(listItems, selectedLanguageIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0 && !"en".equals(currentLanguage)) {
                    setLocale("en");
                    //recreate();
                    startActivity(new Intent(MainActivityClone.this, MainActivity.class));
                } else if (i == 1 && !"my".equals(currentLanguage)) {
                    setLocale("my");
                    //recreate();
                    startActivity(new Intent(MainActivityClone.this, MainActivity.class));
                } else {
                    // Language is the same as the current language; dismiss dialog only
                    dialogInterface.dismiss();
                }
            }
        });
        mBuilder.create();
        mBuilder.show();
    }
    private String getCurrentLanguage() {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        return preferences.getString("My_Language", "");
    }
    private  void  setLocale(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Language", language);
        editor.apply();
    }
    private void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = preferences.getString("My_Language","");
        setLocale(language);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        int itemId=item.getItemId();
        if (itemId==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isOpen()) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}