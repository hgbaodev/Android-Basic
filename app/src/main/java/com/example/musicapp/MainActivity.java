package com.example.musicapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 102;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 103;
    RecyclerView listMusic;
    List<Audio> listAudio;
    MusicAdapter adapter;
    public LinearLayout smallControlLayout;
    public TextView musicName, singerName;
    public ImageButton playAndPauseBtn, preBtn, nextBtn;
    public CircleImageView circleImageView;
    public static ActivityResultLauncher<Intent> playerActivityLauncher;
    public static ActivityResultLauncher<Intent> homeActivityLauncher;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        checkPermission();
//        setupRecyclerView();
        Intent intent = getIntent();

        if (intent.hasExtra("pathFolder")) {
            String pathFolder = intent.getStringExtra("pathFolder");
            List<Audio> audioList = getAllAudioFromFolder(this, pathFolder);
            if (!audioList.isEmpty()) {
                // Tạo adapter và đặt nó cho RecyclerView
                adapter = new MusicAdapter(audioList, getApplicationContext(), listMusic, musicName, singerName, playAndPauseBtn, preBtn, nextBtn, circleImageView, smallControlLayout, 0, false);
                listMusic.setAdapter(adapter);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                listMusic.setLayoutManager(linearLayoutManager);
            } else {
                Toast.makeText(this, "Không có file nhạc trong thư mục", Toast.LENGTH_SHORT);
            }
        }
        setupPlayerActivityLauncher();


    }

    private void setupRecyclerView() {
        listAudio = getAllAudioFromDevice(getApplicationContext());
        adapter = new MusicAdapter(listAudio, getApplicationContext(), listMusic, musicName, singerName, playAndPauseBtn, preBtn, nextBtn, circleImageView, smallControlLayout, 0, false);
        listMusic.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listMusic.setLayoutManager(linearLayoutManager);
    }

    private void setupPlayerActivityLauncher() {
        playerActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        handlePlayerActivityResult(result.getData());
                    }
                });
    }

    private void handlePlayerActivityResult(Intent data) {
        if (data != null) {
            int returnedValue = data.getIntExtra("position", -1);
            int storePosition = data.getIntExtra("storePosition", -1);
            adapter.setPositionPlaying(returnedValue);
            boolean playValue = data.getBooleanExtra("play", false);
            String returnedMusicName = data.getStringExtra("musicname");
            String returnSiger = data.getStringExtra("singer");
            String imgPath = data.getStringExtra("imgPath");
            musicName.setText(returnedMusicName);
            singerName.setText(returnSiger);
            Bitmap thumbnail = MusicAdapter.getThumbnail(imgPath);
            if(thumbnail != null) circleImageView.setImageBitmap(thumbnail);
            else circleImageView.setImageResource(R.drawable.mycd);
            handlePreviousItem(storePosition);
            handleCurrentItem(returnedValue, playValue);
        }
    }

    private void handlePreviousItem(int storePosition) {
        RecyclerView.ViewHolder viewHolder = adapter.getRecyclerView().findViewHolderForAdapterPosition(storePosition);
        if (viewHolder instanceof MusicAdapter.ViewHolder) {
            MusicAdapter.ViewHolder yourViewHolder = (MusicAdapter.ViewHolder) viewHolder;
            yourViewHolder.btnPlay.setImageResource(R.drawable.play);
        }
    }

    private void handleCurrentItem(int returnedValue, boolean playValue) {
        RecyclerView.ViewHolder viewHolder = adapter.getRecyclerView().findViewHolderForAdapterPosition(returnedValue);
        if (viewHolder instanceof MusicAdapter.ViewHolder) {
            MusicAdapter.ViewHolder yourViewHolder = (MusicAdapter.ViewHolder) viewHolder;

            if (playValue) {
                playAndPauseBtn.setImageResource(R.drawable.newpause);
                yourViewHolder.btnPlay.setImageResource(R.drawable.pause);
                RotateAnimation.start(circleImageView);
            } else {
                playAndPauseBtn.setImageResource(R.drawable.newplay);
                yourViewHolder.btnPlay.setImageResource(R.drawable.play);
                RotateAnimation.stop(circleImageView);
            }
        }
    }


    public void initViews() {
        listMusic = findViewById(R.id.listMusic);
        musicName =findViewById(R.id.musicName);
        singerName = findViewById(R.id.singerName);
        playAndPauseBtn = findViewById(R.id.btnPlayMusicCenter);
        preBtn = findViewById(R.id.btnPre);
        nextBtn = findViewById(R.id.btnNext);
        circleImageView = findViewById(R.id.cd_image);
        smallControlLayout = findViewById(R.id.main);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền truy cập bộ nhớ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public List<Audio> getAllAudioFromDevice(final Context context) {

        final List<Audio> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                Audio audioModel = new Audio();
                String path = c.getString(0);
                String album = c.getString(1);
                String artist = c.getString(2);
                String name = path.substring(path.lastIndexOf("/") + 1);
                if (path != null && path.toLowerCase().endsWith(".mp3")) {
                    audioModel.setName(name);
                    audioModel.setAlbum(album);
                    audioModel.setSinger(artist);
                    audioModel.setPath(path);
                    tempAudioList.add(audioModel);
                }
            }
            c.close();
        }
        return tempAudioList;
    }

    public List<Audio> getAllAudioFromFolder(final Context context, String folderPath) {
        List<Audio> tempAudioList = new ArrayList<>();

        // Xác định URI của thư mục cụ thể
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST
        };

        // Xây dựng điều kiện WHERE để lấy các bản ghi chỉ từ thư mục cụ thể
        String selection = MediaStore.Audio.Media.DATA + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + folderPath + "%"};

        Cursor c = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

        if (c != null) {
            while (c.moveToNext()) {
                Audio audioModel = new Audio();
                String path = c.getString(0);
                String album = c.getString(1);
                String artist = c.getString(2);
                String name = path.substring(path.lastIndexOf("/") + 1);

                // Kiểm tra xem file có phải là file nhạc không
                if (path != null && path.toLowerCase().endsWith(".mp3")) {
                    audioModel.setName(name);
                    audioModel.setAlbum(album);
                    audioModel.setSinger(artist);
                    audioModel.setPath(path);
                    tempAudioList.add(audioModel);
                }
            }
            c.close();
        } else {
            Log.e("AudioLoader", "Cursor is null");
        }
        Log.d("AudioLoader", "Number of records: " + tempAudioList.size());
        return tempAudioList;
    }


}