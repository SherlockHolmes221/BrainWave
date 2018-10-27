package com.example.quxian.brainwave.model;

import java.io.Serializable;

public class MusicData implements Serializable {
    /*音乐资源id*/
    private int mMusicRes;
    /*专辑图片id*/
    private int mMusicPicRes;
    /*音乐名称*/
    private String mMusicName;
    /*作者*/
    private String mMusicAuthor;
    /*时长*/
    private String mTime;

    private boolean isPlaying = false;

    public MusicData(int mMusicRes, int mMusicPicRes, String mMusicName, String mMusicAuthor,String mTime) {
        this.mMusicRes = mMusicRes;
        this.mMusicPicRes = mMusicPicRes;
        this.mMusicName = mMusicName;
        this.mMusicAuthor = mMusicAuthor;
        this.mTime = mTime;
    }

    public MusicData(int mMusicRes, int mMusicPicRes, String mMusicName, String mMusicAuthor) {
        this.mMusicRes = mMusicRes;
        this.mMusicPicRes = mMusicPicRes;
        this.mMusicName = mMusicName;
        this.mMusicAuthor = mMusicAuthor;
    }

    public int getMusicRes() {
        return mMusicRes;
    }

    public int getMusicPicRes() {
        return mMusicPicRes;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public String getMusicAuthor() {
        return mMusicAuthor;
    }

    public String getmTime() {
        return mTime;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
