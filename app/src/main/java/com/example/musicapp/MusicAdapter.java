package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    static List<Audio> listAudio;
    private Context mContext;
    static MediaPlayer mediaPlayer;
    RecyclerView recyclerView;
    private static int positionPlaying = -1;
    private TextView musicName;
    private TextView singerName;
    private ImageButton playAndPauseBtn, preBtn, nextBtn;
    private CircleImageView circleImageView;



    public MusicAdapter(List<Audio> list, Context context, RecyclerView recyclerView, TextView musicName, TextView singerName, ImageButton playAndPauseBtn, ImageButton  preBtn, ImageButton  nextBtn, CircleImageView circleImageView) {
        this.listAudio = list;
        this.mContext = context;
        this.recyclerView = recyclerView;
        MusicAdapter.mediaPlayer = new MediaPlayer();
        this.musicName = musicName;
        this.singerName = singerName;
        this.playAndPauseBtn = playAndPauseBtn;
        this.preBtn = preBtn;
        this.nextBtn = nextBtn;
        this.circleImageView = circleImageView;
        loadView();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblName;
        TextView lblPath;
        ImageButton btnPlay;
        ImageButton btnStop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    musicName.setText(listAudio.get(getAdapterPosition()).getName());
                    singerName.setText(listAudio.get(getAdapterPosition()).getSinger());
                    playAndPauseBtn.setImageResource(R.drawable.newpause);
                    startRotateAnimation();
                    playAudioHandler();
                    Intent intent = new Intent(mContext, PlayerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("position", getAdapterPosition());
                    mContext.startActivity(intent);
                }
            });
            playAndPauseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MusicAdapter.mediaPlayer.isPlaying()){
                        MusicAdapter.mediaPlayer.pause();
                        playAndPauseBtn.setImageResource(R.drawable.newplay);
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(positionPlaying);
                        if (viewHolder instanceof ViewHolder) {
                            ViewHolder yourViewHolder = (ViewHolder) viewHolder;
                            yourViewHolder.btnPlay.setImageResource(R.drawable.play);
                        }
                        stopRotateAnimation();
                    }else{
                        MusicAdapter.mediaPlayer.start();
                        playAndPauseBtn.setImageResource(R.drawable.newpause);
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(positionPlaying);
                        if (viewHolder instanceof ViewHolder) {
                            ViewHolder yourViewHolder = (ViewHolder) viewHolder;
                            yourViewHolder.btnPlay.setImageResource(R.drawable.pause);
                        }
                        startRotateAnimation();
                    }

                }
            });
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int temp = positionPlaying;
                    positionPlaying++;
                    if(positionPlaying>= listAudio.size()){
                        positionPlaying = 0;
                    }
                    nextAndPreAudioHandler(temp, positionPlaying);
                    playAndPauseBtn.setImageResource(R.drawable.newpause);
                    startRotateAnimation();
                }
            });
            preBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int temp = positionPlaying;
                    positionPlaying--;
                    if(positionPlaying < 0){
                        positionPlaying = listAudio.size() - 1;
                    }
                    nextAndPreAudioHandler(temp, positionPlaying);
                    playAndPauseBtn.setImageResource(R.drawable.newpause);
                    startRotateAnimation();

                }
            });

            // end
            btnPlay.setOnClickListener(view -> {

                playAudioHandler();
            });

            btnStop.setOnClickListener(view -> {
                stopAudioHandler();
            });
        }
        public void nextAndPreAudioHandler(int beforePositon, int afterPosition){
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(beforePositon);
            if (viewHolder instanceof ViewHolder) {
                ViewHolder yourViewHolder = (ViewHolder) viewHolder;
                yourViewHolder.btnPlay.setImageResource(R.drawable.play);
            }
            viewHolder = recyclerView.findViewHolderForAdapterPosition(afterPosition);
            if (viewHolder instanceof ViewHolder) {
                ViewHolder yourViewHolder = (ViewHolder) viewHolder;
                yourViewHolder.btnPlay.setImageResource(R.drawable.pause);
            }
            if(MusicAdapter.mediaPlayer.isPlaying()){
                MusicAdapter.mediaPlayer.stop();
            }
            MusicAdapter.mediaPlayer.reset();
            try {
                MusicAdapter.mediaPlayer.setDataSource(listAudio.get(positionPlaying).getPath());
                MusicAdapter.mediaPlayer.prepare();
                MusicAdapter.mediaPlayer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            musicName.setText(listAudio.get(positionPlaying).getName());
            singerName.setText(listAudio.get(positionPlaying).getSinger());
        }
        public void playAudioHandler(){
            int position = getAdapterPosition();
            musicName.setText(listAudio.get(getAdapterPosition()).getName());
            singerName.setText(listAudio.get(getAdapterPosition()).getSinger());
            if(position != positionPlaying) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(positionPlaying);
                if (viewHolder instanceof ViewHolder) {
                    ViewHolder yourViewHolder = (ViewHolder) viewHolder;
                    yourViewHolder.btnPlay.setImageResource(R.drawable.play);
                    playAndPauseBtn.setImageResource(R.drawable.newplay);
                }
                positionPlaying = position;
                MusicAdapter.mediaPlayer.stop();
                MusicAdapter.mediaPlayer.reset();
                Audio audio = listAudio.get(position);
                try {
                    btnPlay.setImageResource(R.drawable.pause);
                    playAndPauseBtn.setImageResource(R.drawable.newpause);
                    startRotateAnimation();
                    MusicAdapter.mediaPlayer.reset();
                    MusicAdapter.mediaPlayer.setDataSource(audio.getPath());
                    MusicAdapter.mediaPlayer.prepare();
                    MusicAdapter.mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Audio audio = listAudio.get(position);
                try {
                    if (MusicAdapter.mediaPlayer.isPlaying()) {
                        btnPlay.setImageResource(R.drawable.play);
                        playAndPauseBtn.setImageResource(R.drawable.newplay);
                        stopRotateAnimation();
                        MusicAdapter.mediaPlayer.pause();
                    } else if (MusicAdapter.mediaPlayer.getCurrentPosition() > 0) {
                        btnPlay.setImageResource(R.drawable.pause);
                        playAndPauseBtn.setImageResource(R.drawable.newpause);
                        startRotateAnimation();
                        MusicAdapter.mediaPlayer.start();
                    } else {
                        positionPlaying = position;
                        btnPlay.setImageResource(R.drawable.pause);
                        playAndPauseBtn.setImageResource(R.drawable.newpause);
                        startRotateAnimation();
                        MusicAdapter.mediaPlayer.reset();
                        MusicAdapter.mediaPlayer.setDataSource(audio.getPath());
                        MusicAdapter.mediaPlayer.prepare();
                        MusicAdapter.mediaPlayer.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void stopAudioHandler(){
            int position = getAdapterPosition();
            if(position == positionPlaying && MusicAdapter.mediaPlayer.isPlaying()) {
                positionPlaying = -1;
                btnPlay.setImageResource(R.drawable.play);
                playAndPauseBtn.setImageResource(R.drawable.newplay);
                stopRotateAnimation();
                MusicAdapter.mediaPlayer.stop();
                MusicAdapter.mediaPlayer.reset();
            }
        }

        public void initView() {
            lblName = itemView.findViewById(R.id.lblName);
            lblPath = itemView.findViewById(R.id.lblPath);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnStop = itemView.findViewById(R.id.btnStop);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View musicView = inflater.inflate(R.layout.music_item, parent, false);
        return new ViewHolder(musicView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Audio audio = listAudio.get(position);
        holder.lblName.setText(audio.getName());
        holder.lblPath.setText(audio.getSinger());
    }

    @Override
    public int getItemCount() {
        return listAudio.size();
    }

    public void releaseMediaPlayer() {
        if (MusicAdapter.mediaPlayer != null) {
            MusicAdapter.mediaPlayer.release();
            MusicAdapter.mediaPlayer = null;
        }
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
    public void loadView(){
        musicName.setText(listAudio.get(0).getName());
        singerName.setText(listAudio.get(0).getSinger());
        playAndPauseBtn.setImageResource(R.drawable.newplay);
        try {
            MusicAdapter.mediaPlayer.setDataSource(listAudio.get(0).getPath());
            MusicAdapter.mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopAudioToPlayer(int position){

    }
}
