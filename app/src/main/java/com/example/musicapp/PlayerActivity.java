package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.Toast;
import android.widget.Toolbar;

import com.chibde.visualizer.CircleBarVisualizer;
import com.chibde.visualizer.LineVisualizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends AppCompatActivity {
    private int storePosition = -1;

    private Audio audio;

    private int position;

    private TextView musicName, txtstart, txtstop, singerName;

    private ImageButton btnPre, btnPlayMusicCenter, btnNext, btnBasePre, btnBaseNext;

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
        Bundle bundle = getIntent().getExtras();
        int currentTime = 0;
        if (bundle != null) {
            position = bundle.getInt("position", 0);
            storePosition = position;
            audio = MusicAdapter.listAudio.get(position);
            currentTime = bundle.getInt("currentTime", 0);
        }
        initViews();
        loadMusic();
        MusicAdapter.mediaPlayer.seekTo(currentTime);
        MusicAdapter.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Xử lý khi bài hát kết thúc
                playNextSong();
            }
        });
        btnPlayMusicCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MusicAdapter.mediaPlayer.isPlaying()){
                    MusicAdapter.mediaPlayer.pause();
                    btnPlayMusicCenter.setImageResource(R.drawable.newplay);
                    MusicAdapter.stopRotateAnimation(circleImageView);
                }else{
                    MusicAdapter.mediaPlayer.start();
                    btnPlayMusicCenter.setImageResource(R.drawable.newpause);
                    MusicAdapter.startRotateAnimation(circleImageView);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = position;
                position++;
                if(position>=MusicAdapter.listAudio.size()){
                    position = 0;
                }
                loadMusic();
                btnPlayMusicCenter.setImageResource(R.drawable.newpause);
                MusicAdapter.startRotateAnimation(circleImageView);
            }
        });
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = position;
                position--;
                if(position < 0){
                    position = MusicAdapter.listAudio.size() - 1;
                }
                loadMusic();
                btnPlayMusicCenter.setImageResource(R.drawable.newpause);
                MusicAdapter.startRotateAnimation(circleImageView);
            }
        });

        btnBasePre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentT = MusicAdapter.mediaPlayer.getCurrentPosition()-10000;
                if(currentT < 0) currentT = 0;
                MusicAdapter.mediaPlayer.seekTo(currentT);
            }
        });

        btnBaseNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentT = MusicAdapter.mediaPlayer.getCurrentPosition()+10000;
                if(currentT > MusicAdapter.mediaPlayer.getDuration()) currentT = MusicAdapter.mediaPlayer.getDuration();
                MusicAdapter.mediaPlayer.seekTo(currentT);
            }
        });
    }

    public void loadMusic(){
        if(MusicAdapter.mediaPlayer.isPlaying()){
            MusicAdapter.mediaPlayer.stop();
        }
        if(position >= MusicAdapter.listAudio.size()) position = 0;
        if(position <0) position = MusicAdapter.listAudio.size()-1;
        MusicAdapter.mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(MusicAdapter.listAudio.get(position).getPath())));
        musicName.setText(MusicAdapter.listAudio.get(position).getName().split("-")[0]);
        singerName.setText(MusicAdapter.listAudio.get(position).getSinger());
        try {
            Bitmap thumbnail = MusicAdapter.getThumbnail(MusicAdapter.listAudio.get(position).getPath());
            if(thumbnail != null) {
                circleImageView.setImageBitmap(thumbnail);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        MusicAdapter.startRotateAnimation(circleImageView);

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
        btnBasePre = findViewById(R.id.btnBasePre);
        btnBaseNext = findViewById(R.id.btnBaseNext);
        btnPlayMusicCenter = findViewById(R.id.btnPlayMusicCenter);
        btnNext = findViewById(R.id.btnNext);
        seekmusic = (SeekBar) findViewById(R.id.seekbar_id);
        circleImageView = findViewById(R.id.cd_image);
    }

    private void playNextSong() {
        position++;
        if (position >= MusicAdapter.listAudio.size()) {
            position = 0;
        }
        loadMusic();
        btnPlayMusicCenter.setImageResource(R.drawable.newpause);
        MusicAdapter.startRotateAnimation(circleImageView);
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("position", position);
        resultIntent.putExtra("musicname", musicName.getText());
        resultIntent.putExtra("play", MusicAdapter.mediaPlayer.isPlaying());
        resultIntent.putExtra("storePosition", storePosition);

        setResult(Activity.RESULT_OK, resultIntent);
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

}