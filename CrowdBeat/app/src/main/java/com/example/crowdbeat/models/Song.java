package com.example.crowdbeat.models;

public class Song {
    private String title;
    private String artist;
    private boolean liked;
    private boolean isInWaitlist;
    private String trackUri;

    public Song(String title, String artist, String trackUri) {
        this.title = title;
        this.artist = artist;
        this.trackUri = trackUri;
        this.liked = false;
        this.isInWaitlist =  false;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isInWaitlist() {
        return isInWaitlist;
    }

    public void setInWaitlist(boolean inWaitlist) {
        this.isInWaitlist = inWaitlist;
    }

    public String getTrackUri() {
        return trackUri;
    }
}

