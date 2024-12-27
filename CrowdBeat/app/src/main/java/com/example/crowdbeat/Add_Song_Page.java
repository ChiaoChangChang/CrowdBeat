package com.example.crowdbeat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crowdbeat.models.Song;
import com.example.crowdbeat.service.SpotifyService;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Add_Song_Page extends AppCompatActivity {

    private EditText etSpotifyLink;
    private EditText etSearchSong;
    private Button btnSubmitLink;
    private RecyclerView recyclerViewRecommendations;
    private SongAdapter songAdapter;
    private List<Song> recommendedSongs;
    private SpotifyService spotifyService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final long SEARCH_DEBOUNCE_DELAY = 300; // 300ms delay
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyService = new SpotifyService();

        setContentView(R.layout.activity_add_song_page);

        etSpotifyLink = findViewById(R.id.etSpotifyLink);
        etSearchSong = findViewById(R.id.etSearchSong);
        //Adding a listener that will automatically search spotify if the text in the TextView changes
        etSearchSong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Debounce the search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> performSongSearch(s.toString());
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        btnSubmitLink = findViewById(R.id.btnSubmitLink);
        recyclerViewRecommendations = findViewById(R.id.recyclerViewRecommendations);

        // Sample recommended songs list
        recommendedSongs = new ArrayList<>();
        recommendedSongs.add(new Song("APT.", "Rose", "spotify:track:5vNRhkKd0yEAg8suGBpjeY"));
        recommendedSongs.add(new Song("The Next Episode", "Dr. Dre", "spotify:track:3kufQv90ljbDvYwDr9MUvQ"));
        recommendedSongs.add(new Song("Love Story(Taylor's Version)", "Taylor Swift","spotify:track:3CeCwYWvdfXbZLXFhBrbnf"));
        recommendedSongs.add(new Song("Strangers By Nature", "Adele","spotify:track:13CVSGLSFl4UxpDVR6u3dq"));

        // Set up RecyclerView for recommended songs
        songAdapter = new SongAdapter(recommendedSongs, this::showConfirmationDialog, null, false);
        recyclerViewRecommendations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecommendations.setAdapter(songAdapter);

        // Handle submission of Spotify link
        btnSubmitLink.setOnClickListener(v -> submitLink());
    }

    // Method to handle Spotify link submission
    private void submitLink() {
        String link = etSpotifyLink.getText().toString().trim();
        if (TextUtils.isEmpty(link)) {
            Toast.makeText(this, "Please enter a Spotify link", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract trackId from the Spotify link
        String trackId = extractTrackIdFromLink(link);
        if (trackId == null) {
            Toast.makeText(this, "Invalid Spotify link", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            try {
                String accessToken = spotifyService.getAccessToken();
                Song song = spotifyService.getTrackDetails(accessToken, trackId);
                runOnUiThread(() ->
                        showConfirmationDialog(song)
                );
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to add track: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // Method to show a confirmation dialoag, and if confirmed, to add the required song to the playlist
    private void showConfirmationDialog(Song song) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Add Song")
                .setMessage("Do you want to add \"" + song.getTitle() + "\" by " + song.getArtist() + " to your playlist?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform the network call on a background thread
                    executor.execute(() -> {
                        try {
                            String accessToken = spotifyService.getAccessToken();
                            spotifyService.addTracksToPlaylist(accessToken,"2z40H5YqP206Xam6J88Lta", new String[]{song.getTrackUri()});

                            // Notify the user on the main thread
                            runOnUiThread(() -> {
                                        Toast.makeText(this, "Song added to playlist!", Toast.LENGTH_SHORT).show();
                                        returnSongToMainPage(song);
                                    }
                            );
                        } catch (Exception e) {
                            // Handle errors on the main thread
                            runOnUiThread(() ->
                                    Toast.makeText(this, "Failed to add song: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void returnSongToMainPage(Song song) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("songTitle", song.getTitle());
        resultIntent.putExtra("songArtist", song.getArtist());
        resultIntent.putExtra("songTrackUri", song.getTrackUri());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    //Method to perform the spotify song search
    private void performSongSearch(String query) {
        if (query.trim().isEmpty()) {
            return; // Do not perform search for empty queries
        }

        executor.execute(() -> {
            try {
                String accessToken = spotifyService.getAccessToken();
                List<Song> results = spotifyService.searchSongs(accessToken, query);

                // Update UI with results
                runOnUiThread(() -> {
                    recommendedSongs.clear();
                    recommendedSongs.addAll(results);
                    //Once there are results to the search, the recommended results will be replaced
                    songAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    //Helper method to extract track ID from link
    private String extractTrackIdFromLink(String link) {
        try {
            // Parse the Spotify link
            Uri uri = Uri.parse(link);

            // Check if the path is valid and contains "track"
            if ("open.spotify.com".equals(uri.getHost()) && uri.getPathSegments().contains("track")) {
                return uri.getLastPathSegment(); // Extract the track ID
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if the link is invalid
    }


}
