package com.example.musicapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    static List<Audio> listAudio;
    private Context mContext;
    static MediaPlayer mediaPlayer;
    private RecyclerView recyclerView;
    private static int positionPlaying = 0;
    private TextView musicName;
    private TextView singerName;
    private ImageButton playAndPauseBtn, preBtn, nextBtn;
    private CircleImageView circleImageView;
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
            playAndPauseBtn.setImageResource(R.drawable.newpause);
            startRotateAnimation(circleImageView);
        } else {
            stopRotateAnimation(circleImageView);
            playAndPauseBtn.setImageResource(R.drawable.newplay);
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
                    Audio audio = listAudio.get(getAdapterPosition());
                    musicName.setText(audio.getName());
                    singerName.setText(audio.getSinger());
                    try {
                        Bitmap thumbnail = getThumbnail(audio.getPath());
                        if(thumbnail != null) {
                            circleImageView.setImageBitmap(thumbnail);
                        } else {
                            circleImageView.setImageResource(R.drawable.mycd);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    playAndPauseBtn.setImageResource(R.drawable.newpause);
                    startRotateAnimation(circleImageView);
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
                    if(MusicAdapter.mediaPlayer.isPlaying()){
                        MusicAdapter.mediaPlayer.pause();
                        playAndPauseBtn.setImageResource(R.drawable.newplay);
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(positionPlaying);
                        if (viewHolder instanceof ViewHolder) {
                            ViewHolder yourViewHolder = (ViewHolder) viewHolder;
                            yourViewHolder.btnPlay.setImageResource(R.drawable.play);
                        }
                        stopRotateAnimation(circleImageView);
                    }else{
                        MusicAdapter.mediaPlayer.start();
                        playAndPauseBtn.setImageResource(R.drawable.newpause);
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(positionPlaying);
                        if (viewHolder instanceof ViewHolder) {
                            ViewHolder yourViewHolder = (ViewHolder) viewHolder;
                            yourViewHolder.btnPlay.setImageResource(R.drawable.pause);
                        }
                        startRotateAnimation(circleImageView);
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
                    startRotateAnimation(circleImageView);
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
                    startRotateAnimation(circleImageView);

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
                    startRotateAnimation(circleImageView);
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
                        stopRotateAnimation(circleImageView);
                        MusicAdapter.mediaPlayer.pause();
                    } else if (MusicAdapter.mediaPlayer.getCurrentPosition() > 0) {
                        btnPlay.setImageResource(R.drawable.pause);
                        playAndPauseBtn.setImageResource(R.drawable.newpause);
                        startRotateAnimation(circleImageView);
                        MusicAdapter.mediaPlayer.start();
                    } else {
                        positionPlaying = position;
                        btnPlay.setImageResource(R.drawable.pause);
                        playAndPauseBtn.setImageResource(R.drawable.newpause);
                        startRotateAnimation(circleImageView);
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
                stopRotateAnimation(circleImageView);
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

        try {
            Bitmap thumbnail = getThumbnail(audio.getPath());
            if(thumbnail != null) {
                holder.imgThumbnail.setImageBitmap(thumbnail);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



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

    public void releaseMediaPlayer() {
        if (MusicAdapter.mediaPlayer != null) {
            MusicAdapter.mediaPlayer.release();
            MusicAdapter.mediaPlayer = null;
        }
    }
    public static void startRotateAnimation(CircleImageView img){
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, // Xoay xung quanh trục X ở giữa view
                Animation.RELATIVE_TO_SELF, 0.5f); // Xoay xung quanh trục Y ở giữa view
        rotateAnimation.setDuration(20000); // Thời gian một vòng xoay (milliseconds)
        rotateAnimation.setRepeatCount(Animation.INFINITE); // Lặp vô hạn
        rotateAnimation.setInterpolator(new LinearInterpolator()); // Chuyển động đều
        img.startAnimation(rotateAnimation);
    }
    public static void stopRotateAnimation(CircleImageView img){
        img.clearAnimation();
    }
    public void loadView(int position){
        musicName.setText(listAudio.get(position).getName());
        singerName.setText(listAudio.get(position).getSinger());
        playAndPauseBtn.setImageResource(R.drawable.newplay);
        try {
            MusicAdapter.mediaPlayer.setDataSource(listAudio.get(0).getPath());
            MusicAdapter.mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap getThumbnail(String path) throws IOException {
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        byte[] byte1 = mr.getEmbeddedPicture();
        mr.release();
        if(byte1 != null) {
            return BitmapFactory.decodeByteArray(byte1, 0, byte1.length);
        }
        return  null;
    }
}
