package com.example.crowdbeat;

import com.example.crowdbeat.models.Song;

public interface OnMoveClickListener {
    void onMoveClick(Song song, boolean moveToWaitlist);
}