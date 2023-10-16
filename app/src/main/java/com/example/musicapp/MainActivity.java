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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        checkPermission();

        listAudio = getAllAudioFromDevice(getApplicationContext());
        adapter = new MusicAdapter(listAudio, getApplicationContext(), listMusic, musicName, singerName, playAndPauseBtn, preBtn, nextBtn, circleImageView, smallControlLayout , 0, false );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listMusic.setAdapter(adapter);
        listMusic.setLayoutManager(linearLayoutManager);
        playerActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Xử lý kết quả từ PlayerActivity ở đây
                        if (result.getData() != null) {
                            int returnedValue = result.getData().getIntExtra("position", -1);

                            int storePosition = result.getData().getIntExtra("storePosition", -1);
                            // set lại vị  trí
                            adapter.setPositionPlaying(returnedValue);
                            Boolean playValue = result.getData().getBooleanExtra("play", false);
                            String returnedMusicName = result.getData().getStringExtra("musicname");
                            this.musicName.setText(returnedMusicName);
                            // xử lý vị trí audio cũ
                            RecyclerView.ViewHolder viewHolder = adapter.getRecyclerView().findViewHolderForAdapterPosition(storePosition);
                            if (viewHolder instanceof MusicAdapter.ViewHolder) {
                                MusicAdapter.ViewHolder yourViewHolder = (MusicAdapter.ViewHolder) viewHolder;
                                yourViewHolder.btnPlay.setImageResource(R.drawable.play);
                            }
                            viewHolder = adapter.getRecyclerView().findViewHolderForAdapterPosition(returnedValue);
                            if (viewHolder instanceof MusicAdapter.ViewHolder) {
                                MusicAdapter.ViewHolder yourViewHolder = (MusicAdapter.ViewHolder) viewHolder;

                                if(playValue){
                                    playAndPauseBtn.setImageResource(R.drawable.newpause);
                                    yourViewHolder.btnPlay.setImageResource(R.drawable.pause);
                                    startRotateAnimation();
                                }else{
                                    playAndPauseBtn.setImageResource(R.drawable.newplay);
                                    yourViewHolder.btnPlay.setImageResource(R.drawable.play);
                                    stopRotateAnimation();
                                }
                            }

                        }
                    }
                });
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
                    Log.i("MUSIC", audioModel.toString());
                    tempAudioList.add(audioModel);
                }
            }
            c.close();
        }

        return tempAudioList;
    }
    public void startRotateAnimation(){
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, // Xoay xung quanh trục X ở giữa view
                Animation.RELATIVE_TO_SELF, 0.5f); // Xoay xung quanh trục Y ở giữa view
        rotateAnimation.setDuration(1000); // Thời gian một vòng xoay (milliseconds)
        rotateAnimation.setRepeatCount(Animation.INFINITE); // Lặp vô hạn
        rotateAnimation.setInterpolator(new LinearInterpolator()); // Chuyển động đều
        circleImageView.startAnimation(rotateAnimation);
    }

    public void stopRotateAnimation(){
        circleImageView.clearAnimation();
    }
}