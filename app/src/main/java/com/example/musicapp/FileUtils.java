package com.example.musicapp;

import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    static boolean isMusicFile(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".mp3") || fileName.endsWith(".wav");
    }

    static String getFileDetails(File file) {
        if (file.isDirectory()) {
            return getFolderDetails(file);
        } else {
            return getMusicFileDetails(file);
        }
    }

    static String getFolderDetails(File folder) {
        return "Thư mục";
    }

    static String getMusicFileDetails(File musicFile) {
        return getLastModifiedDate(musicFile) + " Dung lượng: " + getFileSize(musicFile);
    }

    static String getLastModifiedDate(File file) {
        long lastModified = file.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(lastModified));
    }

    static String getFileSize(File file) {
        long fileSize = file.length();
        // Chuyển đổi dung lượng sang đơn vị KB hoặc MB nếu cần
        if (fileSize > 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.2f MB", (float) fileSize / (1024 * 1024));
        } else if (fileSize > 1024) {
            return String.format(Locale.getDefault(), "%.2f KB", (float) fileSize / 1024);
        } else {
            return fileSize + " bytes";
        }
    }

    static void displayFolderInfo(File folder, FolderAdapter.FolderViewHolder holder) {
        File[] subFolders = folder.listFiles();
        if (subFolders != null && subFolders.length > 0) {
            int subFolderCount = subFolders.length;
            holder.info.setText(subFolderCount + " mục");
        } else {
            holder.info.setText("Thư mục trống");
        }
    }

    public static boolean hasMusicFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                // Kiểm tra nếu là file nhạc (bạn có thể mở rộng điều kiện kiểm tra tên file, ví dụ: file.getName().endsWith(".mp3"))
                if (file.isFile() && isMusicFile(file)) {
                    return true;
                }
            }
        }
        return false;
    }

    static String getAlbumNameFromMetadata(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);

        String albumName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return albumName != null ? "Album: "+ albumName : "Không tồn tại album";
    }

}
