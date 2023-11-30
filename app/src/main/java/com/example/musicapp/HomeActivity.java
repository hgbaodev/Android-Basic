package com.example.musicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int STORAGE_AND_RECORD_AUDIO_PERMISSION_CODE = 104;
    private boolean isPermissionsGranted = false;

    private List<File> folderList;
    private RecyclerView recyclerView;
    private FolderAdapter folderAdapter;
    private Button buttonExit;
    private BreadcrumbView breadcrumbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_activity);
        initViews();
        checkPermission();
        if(isPermissionsGranted){
            createAndDisplayFolderAdapter();
        }
    }

    private void initViews(){
        recyclerView = findViewById(R.id.recyclerView);
        buttonExit = findViewById(R.id.buttonExit);
        breadcrumbView = findViewById(R.id.breadcrumbView);
        folderList = new ArrayList<>();
    }

    private void checkPermission() {
        List<String> permissionsToRequest = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!permissionsToRequest.isEmpty()) {
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            requestPermissions(permissionsArray, STORAGE_AND_RECORD_AUDIO_PERMISSION_CODE);
        } else {
            isPermissionsGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_AND_RECORD_AUDIO_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(this, "Đã cấp đủ quyền truy cập", Toast.LENGTH_SHORT).show();
                isPermissionsGranted = true;
                createAndDisplayFolderAdapter();
            } else {
                Toast.makeText(this, "Một số quyền truy cập bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createAndDisplayFolderAdapter() {
        folderAdapter = new FolderAdapter(folderList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(folderAdapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("path")) {
            String path = intent.getStringExtra("path");
            if (path != null) {
                File selectedFolder = new File(path);
                updateDirectoryList(selectedFolder);
                updateBreadcrumb(selectedFolder);
            }
        } else {
            updateDirectoryList(Environment.getExternalStorageDirectory());
        }

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }

    private void updateDirectoryList(File directory) {
        folderList.clear();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                folderList.add(file);
            }
        }
        folderAdapter.notifyDataSetChanged();
    }

    private void updateBreadcrumb(File directory) {
        List<String> pathSegments = new ArrayList<>();
        while (directory != null) {
            pathSegments.add(0, directory.getName());
            directory = directory.getParentFile();
        }
        if (!pathSegments.isEmpty()) {
            pathSegments.set(0, "#");
        }

        breadcrumbView.setPath(pathSegments);
    }

}
