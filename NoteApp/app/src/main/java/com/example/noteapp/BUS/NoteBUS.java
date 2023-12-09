package com.example.noteapp.BUS;

import android.content.ContentResolver;

import com.example.noteapp.DAO.NoteDAO;
import com.example.noteapp.DTO.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class NoteBUS {
    public static ArrayList<Note> listNote;
    public static NoteDAO noteDAO;

    public NoteBUS(ContentResolver contentResolver) {
        noteDAO = new NoteDAO(contentResolver);
        listNote = noteDAO.getAll();
        Collections.sort(listNote, new Comparator<Note>() {
            @Override
            public int compare(Note note1, Note note2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date1 = dateFormat.parse(note1.getUpdatedAt());
                    Date date2 = dateFormat.parse(note2.getUpdatedAt());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    public NoteBUS(ArrayList<Note> listNote) {
        this.listNote = listNote;
    }

    public ArrayList<Note> getAll() {
        return listNote;
    }


    public static Note getNoteById(int id) {
        return listNote.stream()
                .filter(note -> note.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static int getIndexById(int id) {
        Optional<Note> optionalNote = listNote.stream()
                .filter(note -> note.getId() == id)
                .findFirst();
        if (optionalNote.isPresent()) {
            return listNote.indexOf(optionalNote.get());
        }
        return -1;
    }

    public static Note insert(Note note) {
        Note newNote = noteDAO.insert(note);
        listNote.add(0,newNote);
        return newNote;
    }

    public static boolean update(Note note) {
        if(noteDAO.update(note)) {
            listNote.set(getIndexById(note.getId()),note);
            return true;
        }
        return false;
    }

    public static boolean delete(int id) {
        if(noteDAO.delete(id)) {
            listNote.remove(getNoteById(id));
            return true;
        }
        return false;
    }

    public static ArrayList<Note> search(String text) {
        ArrayList<Note> filterlist = new ArrayList<>();
        for (Note item : listNote){
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())){
                filterlist.add(item);
            }
        }
        return filterlist;
    }
}
