package com.example.user.mediaplayerdemo.model;

/**
 * Created by satish on 28/1/16.
 */
public class SongsModel {
    String fileName,filePath;
    int song_number;

    public SongsModel(String fileName, String filePath, int song_number) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.song_number = song_number;
    }

    public SongsModel() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getSong_number() {
        return song_number;
    }

    public void setSong_number(int song_number) {
        this.song_number = song_number;
    }
}
