package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.chibde.visualizer.CircleBarVisualizer;
import com.chibde.visualizer.LineVisualizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends AppCompatActivity {

    private List<Audio> listAudio;

    private Audio audio;

    private int position;

    private TextView musicName, txtstart, txtstop, singerName;

    private ImageButton btnPre, btnPlayMusicCenter, btnNext;

    private CircleImageView circleImageView;

    SeekBar seekmusic;

    Thread updateseekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listAudio = getAllAudioFromDevice(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            position = bundle.getInt("position", 0);
            audio = listAudio.get(position);
        }
        initViews();
        loadMusic();
        btnPlayMusicCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MusicAdapter.mediaPlayer.isPlaying()){
                    MusicAdapter.mediaPlayer.pause();
                    btnPlayMusicCenter.setImageResource(R.drawable.newplay);
                    stopRotateAnimation();
                }else{
                    MusicAdapter.mediaPlayer.start();
                    btnPlayMusicCenter.setImageResource(R.drawable.newpause);
                    startRotateAnimation();
                }

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = position;
                position++;
                if(position>=listAudio.size()){
                    position = 0;
                }
                loadMusic();
                btnPlayMusicCenter.setImageResource(R.drawable.newpause);
                startRotateAnimation();
            }
        });
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = position;
                position--;
                if(position < 0){
                    position = listAudio.size() - 1;
                }
                loadMusic();
                btnPlayMusicCenter.setImageResource(R.drawable.newpause);
                startRotateAnimation();
            }
        });
    }

    public void loadMusic(){
        if(MusicAdapter.mediaPlayer.isPlaying()){
            MusicAdapter.mediaPlayer.stop();
        }
        MusicAdapter.mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(listAudio.get(position).getPath())));
        musicName.setText(listAudio.get(position).getName().split("-")[0]);
        singerName.setText(listAudio.get(position).getSinger());
        MusicAdapter.mediaPlayer.start();

        seekmusic.setMax(MusicAdapter.mediaPlayer.getDuration());

        updateseekbar = new Thread() {
            @Override
            public void run() {
                int totalDuration = MusicAdapter.mediaPlayer.getDuration();
                int currentposition = 0;

                while (currentposition < totalDuration) {
                    try {
                        sleep(500);
                        currentposition = MusicAdapter.mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentposition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        updateseekbar.start();

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    MusicAdapter.mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        String endTime = createTime(MusicAdapter.mediaPlayer.getDuration());
        txtstop.setText(endTime);

        final android.os.Handler handler = new android.os.Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(MusicAdapter.mediaPlayer.getCurrentPosition());
                txtstart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);
        startRotateAnimation();

        CircleBarVisualizer circleBarVisualizer = findViewById(R.id.visualizer);
        circleBarVisualizer.setColor(ContextCompat.getColor(this, R.color.purple_200));
        circleBarVisualizer.setPlayer(MusicAdapter.mediaPlayer.getAudioSessionId());
    }

    public void initViews(){
        musicName = findViewById(R.id.musicName);
        singerName = findViewById(R.id.singerName);
        txtstart = findViewById(R.id.txtstart);
        txtstop = findViewById(R.id.txtstop);
        btnPre = findViewById(R.id.btnPre);
        btnPlayMusicCenter = findViewById(R.id.btnPlayMusicCenter);
        btnNext = findViewById(R.id.btnNext);
        seekmusic = (SeekBar) findViewById(R.id.seekbar_id);
        circleImageView = findViewById(R.id.cd_image);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Kết thúc hoạt động PlayerActivity và quay lại hoạt động gọi trước đó
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

    public String createTime(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        time += min + ":";

        if (sec < 10) {
            time += "0";
        }
        time += sec;

        return time;
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