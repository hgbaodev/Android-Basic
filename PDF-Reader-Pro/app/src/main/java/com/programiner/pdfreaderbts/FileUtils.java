package com.programiner.pdfreaderbts;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class FileUtils {
    public static void deletePdfFile(Context context, String pdfFilePath) {
        ContentResolver contentResolver = context.getContentResolver();
        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        String[] selectionArgs = { pdfFilePath };
        Uri queryUri = MediaStore.Files.getContentUri("external");

        int deleted = contentResolver.delete(queryUri, selection, selectionArgs);

        if (deleted > 0) {
            Toast.makeText(context, "Tệp đã được xoá", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Không thể xoá tệp", Toast.LENGTH_SHORT).show();
        }
    }
}
