package com.programiner.pdfreaderbts;

import android.Manifest;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.programiner.Adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Adapter adapter;
    List<File> list;
    ProgressBar progressBar;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();


    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        setuprv();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void setupsearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText!=null){
                    filter(newText);
                }else{
                    Toast.makeText(MainActivity.this, "No File Found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void filter(String newText) {
        List<File> filterlist = new ArrayList<>();
        for (File item : list){
            if (item.getName().toLowerCase().contains(newText)){
                filterlist.add(item);
            }
        }
        adapter.filterlist(filterlist);
    }

    private void setuprv() {
        recyclerView = findViewById(R.id.rv_files);
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView);
        list = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        setupsearch();

        new Thread(() -> {
            List<File> files = getallFiles();
            //show latest
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
                }
            });
            list.addAll(files);

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new Adapter(MainActivity.this, list);
                    recyclerView.setAdapter(adapter);
                    handleUiRendering();
                }
            });
        }).start();
    }

    private void handleUiRendering() {
        progressBar.setVisibility(View.GONE);
        if (adapter.getItemCount() == 0) {
            Toast.makeText(this, "No Pdf File In Phone", Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private List<File> getallFiles() {
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {"application/pdf"};
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        ArrayList<File> list = new ArrayList<>();
        int pdfPathIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        while (cursor.moveToNext()) {
            if (pdfPathIndex != -1) {
                String pdfPath = cursor.getString(pdfPathIndex);
                File pdfFile = new File(pdfPath);
                if (pdfFile.exists() && pdfFile.isFile()) {
                    list.add(pdfFile);
                }
            }
        }
        cursor.close();
        return list;
    }
}