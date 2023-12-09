package com.example.noteapp.GUI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.noteapp.DAO.NoteDAO;
import com.example.noteapp.DTO.Note;
import com.example.noteapp.BUS.NoteBUS;
import com.example.noteapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btnAdd;
    SearchView searchView;
    RecyclerView listNoteUI;

    NoteBUS listNote;

    NoteAdapter adapter;
    NoteBUS noteBUS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initView();
        addEvent();
        setupsearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if(listNote == null) {
            listNote = new NoteBUS(noteBUS.getAll());
        }
        adapter = new NoteAdapter(this, listNote.getAll());
        listNoteUI.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listNoteUI.setLayoutManager(linearLayoutManager);
    }

    public void initView() {
        btnAdd = findViewById(R.id.btnAdd);
        searchView = findViewById(R.id.searchView);
        listNoteUI = findViewById(R.id.rcvNotes);
        noteBUS = new NoteBUS(getContentResolver());
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
    }

    private void addEvent()
    {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewNote.class);
                intent.putExtra("action","create");
                startActivity(intent);
            }
        });
    }


    private void filter(String newText) {
        List<Note> filterlist = NoteBUS.search(newText);
        adapter.setList(filterlist);
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
}