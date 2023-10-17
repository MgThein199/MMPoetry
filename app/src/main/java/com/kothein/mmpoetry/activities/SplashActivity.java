package com.kothein.mmpoetry.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.kothein.mmpoetry.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
boolean isAndroidReady = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        View content = findViewById(android.R.id.content);

        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isAndroidReady) {
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                dismissSplashScreen();
                return false;
            }

        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
    private void dismissSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isAndroidReady = true;
                // Start the main activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class); // Replace with your main activity
                startActivity(intent);

                // Finish the splash activity so it's not in the back stack
                finish();
            }
        }, 3000);
    }
}
