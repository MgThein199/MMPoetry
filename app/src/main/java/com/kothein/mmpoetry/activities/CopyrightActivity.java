package com.kothein.mmpoetry.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kothein.mmpoetry.R;
import com.kothein.mmpoetry.ThemePreferenceHelper;

public class CopyrightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int selectedTheme = ThemePreferenceHelper.getTheme(this);
        AppCompatDelegate.setDefaultNightMode(selectedTheme);

        setContentView(R.layout.activity_copyright);
        TextView textView = findViewById(R.id.copyright_text_view);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
    }
}