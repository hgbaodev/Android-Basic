package com.example.musicapp;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<File> folderList;
    private Context context;

    public FolderAdapter(List<File> folderList, Context context) {
        this.folderList = folderList;
        this.context = context;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        File selectedFile = folderList.get(position);
        holder.folderNameTextView.setText(selectedFile.getName());

        if (selectedFile.isDirectory()) {
            holder.iconImageView.setImageResource(R.drawable.baseline_folder_24);
            FileUtils.displayFolderInfo(selectedFile, holder);
        } else {
            if (FileUtils.isMusicFile(selectedFile)) {
                Bitmap thumbnail = getThumbnail(selectedFile.getPath());
                if(thumbnail != null) holder.iconImageView.setImageBitmap(thumbnail);
                else holder.iconImageView.setImageResource(R.drawable.file_mucic);
                holder.info.setText(FileUtils.getAlbumNameFromMetadata(selectedFile.getPath()));
            } else {
                holder.iconImageView.setImageResource(R.drawable.baseline_insert_drive_file_24);
                String fileDetails = FileUtils.getFileDetails(selectedFile);
                holder.info.setText(fileDetails);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(selectedFile.isDirectory()){
//                    Intent intent = new Intent(context, HomeActivity.class);
//                    String path = selectedFile.getAbsolutePath();
//                    intent.putExtra("path",path);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }else{
//                    try {
//                        if (FileUtils.isMusicFile(selectedFile)){
//                            File parentFolder = selectedFile.getParentFile();
//                            Intent intent = new Intent(context, MainActivity.class);
//                            intent.putExtra("pathFolder", parentFolder.toString());
//                            context.startActivity(intent);
//                        } else {
//                            Intent intent = new Intent();
//                            intent.setAction(android.content.Intent.ACTION_VIEW);
//                            String type = "image/*";
//                            intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);
//                        }
//                    }catch (Exception e){
//                        Toast.makeText(context.getApplicationContext(),"Cannot open the file", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenu().add("Up");
                popupMenu.getMenu().add("Select");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Up")){
                            if(selectedFile.isDirectory()){
                                Intent intent = new Intent(context, HomeActivity.class);
                                String path = selectedFile.getAbsolutePath();
                                intent.putExtra("path",path);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Đây không phải là thư mục!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(item.getTitle().equals("Select")){
                            if(selectedFile.isDirectory()){
                                if (FileUtils.hasMusicFiles(selectedFile)) {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    String path = selectedFile.getAbsolutePath();
                                    intent.putExtra("pathFolder", path);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                } else {
                                    Toast.makeText(context, "Thư mục không chứa file nhạc!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Đây không phải là thư mục!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView folderNameTextView;
        TextView info;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            folderNameTextView = itemView.findViewById(R.id.folderNameTextView);
            info = itemView.findViewById(R.id.info);
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


