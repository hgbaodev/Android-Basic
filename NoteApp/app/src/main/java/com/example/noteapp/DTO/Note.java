package com.example.noteapp.DTO;

import java.util.Calendar;
import java.util.Objects;

public class Note {
    private int id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String pathImage;
    private String reminderTime;

    public Note() {
    }

    public Note(int id, String title, String content, String createdAt, String updatedAt, String pathImage, String reminderTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pathImage = pathImage;
        this.reminderTime = reminderTime;
    }

    public Note(String title, String content, String createdAt, String updatedAt, String pathImage, String reminderTime) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pathImage = pathImage;
        this.reminderTime = reminderTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id && Objects.equals(title, note.title) && Objects.equals(content, note.content) && Objects.equals(createdAt, note.createdAt) && Objects.equals(updatedAt, note.updatedAt) && Objects.equals(pathImage, note.pathImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, createdAt, updatedAt, pathImage);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", pathImage='" + pathImage + '\'' +
                '}';
    }
}
