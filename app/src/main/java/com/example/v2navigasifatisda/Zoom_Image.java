package com.example.v2navigasifatisda;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Zoom_Image extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomimage);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        ImageView zoomImageView = findViewById(R.id.zoomImageView);

        int imageResId = getIntent().getIntExtra("image_res_id", -1);
        if (imageResId != -1) {
            zoomImageView.setImageResource(imageResId);
        }
    }
}
