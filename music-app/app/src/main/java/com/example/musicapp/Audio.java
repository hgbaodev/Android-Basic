package com.example.musicapp;

import java.util.Objects;

public class Audio {
    private String name;
    private String path;
    private String album;
    private String singer;

    public Audio() {
    }

    public Audio(String name, String path, String album, String singer) {
        this.name = name;
        this.path = path;
        this.album = album;
        this.singer = singer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    @Override
    public String toString() {
        return "Audio{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", album='" + album + '\'' +
                ", singer='" + singer + '\'' +
                '}';
    }
}
