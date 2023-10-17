package com.kothein.mmpoetry.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.widget.TextView;

import com.kothein.mmpoetry.R;
import com.kothein.mmpoetry.ThemePreferenceHelper;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the saved theme
        int selectedTheme = ThemePreferenceHelper.getTheme(this);
        AppCompatDelegate.setDefaultNightMode(selectedTheme);

        setContentView(R.layout.activity_about);

        // Find the TextView by its ID
        TextView textView = findViewById(R.id.about_text_view);

        // Set text to the TextView
        String aboutText = "This is some information about the app...";
        textView.setText(aboutText);
        textView.setPadding(10,10,10,10);
    }
}
