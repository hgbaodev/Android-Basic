package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    static List<Audio> listAudio;
    private Context mContext;
    static MediaPlayer mediaPlayer;
    static RecyclerView recyclerView;
    static int positionPlaying = 0;
    static TextView musicName, singerName;
    private ImageButton playAndPauseBtn, preBtn, nextBtn;
    static CircleImageView circleImageView;
    private boolean initialPlayState;
    private LinearLayout smallControlLayout;

    public void setPositionPlaying(int pos){
        this.positionPlaying = pos;
    }

    public RecyclerView getRecyclerView(){
        return this.recyclerView;
    }

    public MusicAdapter(List<Audio> list, Context context, RecyclerView recyclerView, TextView musicName, TextView singerName, ImageButton playAndPauseBtn, ImageButton preBtn, ImageButton nextBtn, CircleImageView circleImageView, LinearLayout smallControlLayout, int position, Boolean play) {
        this.listAudio = list;
        this.mContext = context;
        this.recyclerView = recyclerView;
        this.musicName = musicName;
        this.singerName = singerName;
        this.playAndPauseBtn = playAndPauseBtn;
        this.preBtn = preBtn;
        this.nextBtn = nextBtn;
        this.circleImageView = circleImageView;
        this.smallControlLayout = smallControlLayout;
        MusicAdapter.mediaPlayer = new MediaPlayer();

        initialPlayState = play; // Lưu trạng thái ban đầu

        loadView(position);
        
        if (initialPlayState) {
            positionPlaying = position;
            renderStartBottomBar();
        } else {
            renderPauseBottomBar();
        }



    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblName;
        TextView lblPath;
        ImageButton btnPlay;
        ImageButton btnStop;
        ImageView imgThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playAudioHandler();
                    Intent intent = new Intent(mContext, PlayerActivity.class);
                    intent.putExtra("position", getAdapterPosition());
                    intent.putExtra("currentTime", MusicAdapter.mediaPlayer.getCurrentPosition());
                    MainActivity.playerActivityLauncher.launch(intent);
                }
            });
            smallControlLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PlayerActivity.class);
                    intent.putExtra("position", positionPlaying);
                    intent.putExtra("currentTime", MusicAdapter.mediaPlayer.getCurrentPosition());
                    MainActivity.playerActivityLauncher.launch(intent);
                }
            });
            playAndPauseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(positionPlaying);
                    ViewHolder yourViewHolder = (ViewHolder) viewHolder;

                    if(MusicAdapter.mediaPlayer.isPlaying()){
                        MusicAdapter.mediaPlayer.pause();
                        yourViewHolder.btnPlay.setImageResource(R.drawable.play);

                        renderPauseBottomBar();
                    } else {
                        MusicAdapter.mediaPlayer.start();
                        yourViewHolder.btnPlay.setImageResource(R.drawable.pause);
                        renderStartBottomBar();
                    }

                }
            });
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int temp = positionPlaying;
                    positionPlaying++;
                    if(positionPlaying >= listAudio.size()){
                        positionPlaying = 0;
                    }
                    nextAndPreAudioHandler(temp, positionPlaying);
                    renderStartBottomBar();
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
                    renderStartBottomBar();
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

            Audio audio = listAudio.get(positionPlaying);
            startMusic(audio.getPath());
            musicName.setText(audio.getName());
            singerName.setText(audio.getSinger());
            Bitmap thumbnail = getThumbnail(audio.getPath());
            if(thumbnail != null) circleImageView.setImageBitmap(thumbnail);
            else circleImageView.setImageResource(R.drawable.mycd);
        }

        public void playAudioHandler(){
            int position = getAdapterPosition();
            // Nếu nhạc khác nhạc hiện tại thì bật bài mới
            if(position != positionPlaying) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(positionPlaying);
                if (viewHolder instanceof ViewHolder) {
                    ViewHolder yourViewHolder = (ViewHolder) viewHolder;
                    yourViewHolder.btnPlay.setImageResource(R.drawable.play);
                    playAndPauseBtn.setImageResource(R.drawable.newplay);
                }
                positionPlaying = position;
                MusicAdapter.mediaPlayer.stop();
                renderStartMusic(listAudio.get(getAdapterPosition()));
                startMusic(listAudio.get(position).getPath());
            } else {
                // Nếu đang là bài nhạc đang bật
                Audio audio = listAudio.get(position);
                if (MusicAdapter.mediaPlayer.isPlaying()) {
                    renderPauseMusic();
                    MusicAdapter.mediaPlayer.pause();
                } else if (MusicAdapter.mediaPlayer.getCurrentPosition() > 0) {
                    // Nếu đang pause thì phát tiếp tục
                    renderStartMusic(audio);
                    MusicAdapter.mediaPlayer.start();
                } else {
                    // Nếu chưa phát thì phát từ đầu
                    positionPlaying = position;
                    renderStartMusic(audio);
                    startMusic(audio.getPath());
                }
            }
        }

        public void renderStartMusic(Audio audio) {
            // Set Bottom Bar
            musicName.setText(audio.getName());
            singerName.setText(audio.getSinger());
            Bitmap thumbnail = getThumbnail(audio.getPath());
            if(thumbnail != null) circleImageView.setImageBitmap(thumbnail);
            else circleImageView.setImageResource(R.drawable.mycd);

            renderStartBottomBar();
            //
            btnPlay.setImageResource(R.drawable.pause);
        }

        public void renderPauseMusic() {
            btnPlay.setImageResource(R.drawable.play);
            renderPauseBottomBar();
        }

        public void startMusic(String path) {
            try {
                MusicAdapter.mediaPlayer.reset();
                MusicAdapter.mediaPlayer.setDataSource(path);
                MusicAdapter.mediaPlayer.prepare();
                MusicAdapter.mediaPlayer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void stopAudioHandler(){
            int position = getAdapterPosition();
            if(position == positionPlaying && MusicAdapter.mediaPlayer.isPlaying()) {
                positionPlaying = -1;
                renderPauseMusic();
                MusicAdapter.mediaPlayer.stop();
                MusicAdapter.mediaPlayer.reset();
            }
        }

        public void initView() {
            lblName = itemView.findViewById(R.id.lblName);
            lblPath = itemView.findViewById(R.id.lblPath);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnStop = itemView.findViewById(R.id.btnStop);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }


    public void renderStartBottomBar() {
        playAndPauseBtn.setImageResource(R.drawable.newpause);
        RotateAnimation.start(circleImageView);
    }

    public void renderPauseBottomBar() {
        playAndPauseBtn.setImageResource(R.drawable.newplay);
        RotateAnimation.stop(circleImageView);
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
        holder.lblName.setText(audio.getName().split("-")[0]);
        holder.lblPath.setText(audio.getSinger());

        Bitmap thumbnail = getThumbnail(audio.getPath());
        if(thumbnail != null) holder.imgThumbnail.setImageBitmap(thumbnail);
        else holder.imgThumbnail.setImageResource(R.drawable.mycd);

        if (initialPlayState) {
            if (position == positionPlaying) {
                holder.btnPlay.setImageResource(R.drawable.pause);
            } else {
                holder.btnPlay.setImageResource(R.drawable.play);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listAudio.size();
    }

    public void loadView(int position){
        Audio audio = listAudio.get(position);
        musicName.setText(audio.getName());
        singerName.setText(audio.getSinger());
        Bitmap thumbnail = getThumbnail(audio.getPath());
        if(thumbnail != null) circleImageView.setImageBitmap(thumbnail);
        else circleImageView.setImageResource(R.drawable.mycd);
        try {
            MusicAdapter.mediaPlayer.setDataSource(audio.getPath());
            MusicAdapter.mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap getThumbnail(String path) {
        MediaMetadataRetriever m = new MediaMetadataRetriever();
        m.setDataSource(path);
        byte[] byte1 = m.getEmbeddedPicture();
        try {
            m.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(byte1 != null) {
            return BitmapFactory.decodeByteArray(byte1, 0, byte1.length);
        }
        return  null;
    }
}
