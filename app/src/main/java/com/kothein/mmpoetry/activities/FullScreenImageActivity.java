package com.kothein.mmpoetry.activities;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kothein.mmpoetry.R;
import com.kothein.mmpoetry.animation.Animatool;
import com.kothein.mmpoetry.photoview.PhotoView;

public class FullScreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // In your activity's onCreate method
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Get the image URL passed from the previous activity
        String imageUrl = getIntent().getStringExtra("image_url");

        //GestureImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        // Initialize a PhotoView and load the image
        PhotoView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        Glide.with(this).load(imageUrl).into(fullScreenImageView);

        // Add zoom and pan capabilities to the PhotoView as needed
        fullScreenImageView.setMaximumScale(5); // Example: Set maximum zoom level
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();
            Animatool.animateSwipeRight(FullScreenImageActivity.this);

    }
}
