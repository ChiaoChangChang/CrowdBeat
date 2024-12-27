package com.example.crowdbeat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crowdbeat.SongAdapter;
import com.example.crowdbeat.models.Song;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.crowdbeat.Add_Song_Page;
import com.example.crowdbeat.service.SpotifyService;

import androidx.annotation.Nullable;





public class Guest_Main_Page extends AppCompatActivity {

    private static final int REQUEST_ADD_SONG = 1;
    private TextView tvTitle;
    private RecyclerView recyclerViewSongs;
    private Button addSongButton;
    private TextView tvCurrentSong;
    private ImageButton btnPlayPause;
    private List<Song> songList;
    private SongAdapter songAdapter;
    private boolean isPlaying = false; // State for play/pause button
    private Song currentSong = null; // Currently playing song
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private SpotifyService spotifyService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main_page);

        // Initialize UI elements
        tvTitle = findViewById(R.id.tvTitle);
        recyclerViewSongs = findViewById(R.id.recyclerViewSongs);
        addSongButton = findViewById(R.id.addSongButton);
        tvCurrentSong = findViewById(R.id.tvCurrentSong);
        btnPlayPause = findViewById(R.id.btnPlayPause);

        spotifyService = new SpotifyService();
        songList = new ArrayList<>();

        // Set up RecyclerView with SongAdapter and a click listener
        songAdapter = new SongAdapter(songList, song -> playSong(song), null, false);

        executor.execute(() -> {
            try {
                // Fetch songs from Spotify
                String accessToken = spotifyService.getAccessToken();
                List<Song> fetchedSongs = spotifyService.getSongs(accessToken, "2z40H5YqP206Xam6J88Lta");

                runOnUiThread(() -> {
                    songList.clear();
                    songList.addAll(fetchedSongs);
                    //Once songs are fetched from the playlist, notify adaptor to update the UI
                    songAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to fetch songs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });

        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSongs.setAdapter(songAdapter);

        // Handle Add Song Button click
        addSongButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Add_Song_Page.class);
            startActivityForResult(intent, REQUEST_ADD_SONG); // Start Add_Song_Page for result
        });
    }

    // Method to play a song and update the play bar
    private void playSong(Song song) {
        currentSong = song;
        tvCurrentSong.setText(song.getTitle()); // Display the song title in the play bar
        isPlaying = true;
        Toast.makeText(this, "Playing: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }

    // Method to toggle play/pause state
    private void togglePlayPause() {
        if (currentSong == null) {
            Toast.makeText(this, "No song selected to play", Toast.LENGTH_SHORT).show();
            return;
        }
        isPlaying = !isPlaying;
        String message = isPlaying ? "Playing: " + currentSong.getTitle() : "Paused";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_SONG && resultCode == RESULT_OK && data != null) {
            // Retrieve the song details from the result data
            String songTitle = data.getStringExtra("songTitle");
            String songArtist = data.getStringExtra("songArtist");
            String songTrackUri = data.getStringExtra("songTrackUri");

            // Create a new Song object and add it to the list
            Song newSong = new Song(songTitle, songArtist, songTrackUri);
            songList.add(newSong);
            songAdapter.notifyDataSetChanged(); // Update RecyclerView
        }
    }
}

