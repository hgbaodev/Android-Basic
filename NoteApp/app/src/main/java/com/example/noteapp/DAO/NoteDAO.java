package com.example.noteapp.DAO;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.example.noteapp.DTO.Note;
import com.example.noteapp.Provider.MyContentProvider;

import java.util.ArrayList;

public class NoteDAO {
    private static ContentResolver contentResolver;

    public NoteDAO(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Note insert(Note note) {
        ContentValues values = new ContentValues();
        Note newNote = null;
        values.put(MyContentProvider.title, note.getTitle());
        values.put(MyContentProvider.content, note.getContent());
        values.put(MyContentProvider.createdAt, note.getCreatedAt());
        values.put(MyContentProvider.updatedAt, note.getUpdatedAt());
        values.put(MyContentProvider.pathImage, note.getPathImage());
        values.put(MyContentProvider.reminderTime, note.getReminderTime());
        Uri uri = contentResolver.insert(MyContentProvider.CONTENT_URI, values);

        if (uri != null) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToLast()) {
                newNote = cursorToNote(cursor);
                cursor.close();
            }
        }
        return newNote;
    }

    public boolean update(Note note) {
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.title, note.getTitle());
        values.put(MyContentProvider.content, note.getContent());
        values.put(MyContentProvider.updatedAt, note.getUpdatedAt());
        values.put(MyContentProvider.pathImage, note.getPathImage());
        values.put(MyContentProvider.reminderTime, note.getReminderTime());

        Uri updateUri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + note.getId());

        String selection = MyContentProvider.id + " = ?";
        String[] selectionArgs = { String.valueOf(note.getId()) };

        int updatedRows = contentResolver.update(updateUri, values, selection, selectionArgs);
        return updatedRows > 0;
    }

    public boolean delete(int id) {
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + id);
        String selection = MyContentProvider.id + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int deletedRows = contentResolver.delete(uri, selection, selectionArgs);
        return deletedRows > 0;
    }

    @SuppressLint("Range")
    public ArrayList<Note> getAll() {
        ArrayList<Note> result = new ArrayList<>();

        Cursor cursor = contentResolver.query(Uri.parse("content://com.demo.note.provider/notes"), null, null, null, null);

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Note note = cursorToNote(cursor);
                result.add(note);
                cursor.moveToNext();
            }
        }
        return result;
    }

    private Note cursorToNote(Cursor cursor) {
        int id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.id)));
         String title = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.title));
         String content = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.content));
         String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.createdAt));
         String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.updatedAt));
         String pathImage = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.pathImage));
         String reminderTime = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.reminderTime));
         return new Note(id, title, content, createdAt, updatedAt, pathImage, reminderTime);
    }
}
