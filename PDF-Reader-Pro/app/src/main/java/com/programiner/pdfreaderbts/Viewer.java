package com.programiner.pdfreaderbts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class Viewer extends AppCompatActivity {

    PDFView pdfView;
    ImageView back,share,delete;
    TextView title;
    String name,path;
    boolean ishide=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        initvar();
    }

    private void initvar() {
        back = findViewById(R.id.back);
        share = findViewById(R.id.share);
        title = findViewById(R.id.file_name);
        pdfView = findViewById(R.id.pdfView);
        back();
        share();
        getintentdata();
        showpdf();
        fullscreen();
    }

    private void fullscreen() {
        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ishide){
                    findViewById(R.id.materialToolbar).setVisibility(View.VISIBLE);
                    ishide=false;
                }else{
                    findViewById(R.id.materialToolbar).setVisibility(View.GONE);
                    ishide=true;
                }
            }
        });
    }

    private void showpdf() {
        pdfView.fromFile(new File(path)).load();
    }

    private void getintentdata() {
        name = getIntent().getStringExtra("name");
        path = getIntent().getStringExtra("path");
        title.setText(name);
    }

    private void back() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void share() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ShareCompat.IntentBuilder.from(Viewer.this).setType("application/pdf")
                        .setStream(Uri.parse(path))
                        .setChooserTitle("Choose app")
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });
    }
}