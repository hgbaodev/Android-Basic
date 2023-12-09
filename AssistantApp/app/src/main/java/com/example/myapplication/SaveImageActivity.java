package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SaveImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);
        initViews();
        Intent intent = getIntent();
        Bitmap capturedImage = (Bitmap) intent.getExtras().get("data");
        imageView.setImageBitmap(capturedImage);
    }
    public void initViews(){
        imageView = findViewById(R.id.imageSaved);
        btnBack = findViewById(R.id.backtohome);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SaveImageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}