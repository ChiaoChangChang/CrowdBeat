package com.example.crowdbeat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crowdbeat.models.Song;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.example.crowdbeat.service.SpotifyService;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;


public class Host_Main_Page extends AppCompatActivity {


    private static final int REQUEST_ADD_SONG = 1;

    private TextView tvEventName;
    private RecyclerView recyclerViewPlaylist;
    private RecyclerView recyclerViewWaitlist;
    private Button addSongButton;
    private Button startPlayerButton;

    private List<Song> playlist;
    private List<Song> waitlist;
    private SongAdapter playlistAdapter;
    private SongAdapter waitlistAdapter;
    private SpotifyService spotifyService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // ViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_host_main_page);

        tvEventName = findViewById(R.id.tvEventName);
        recyclerViewPlaylist = findViewById(R.id.recyclerViewPlaylist);
        recyclerViewWaitlist = findViewById(R.id.recyclerViewWaitlist);
        addSongButton = findViewById(R.id.addSongButton);
        startPlayerButton = findViewById(R.id.btnLetsParty);

        String eventName = getIntent().getStringExtra("EVENT_NAME");
        tvEventName.setText(eventName != null ? eventName : "Event");

        spotifyService = new SpotifyService();
        playlist = new ArrayList<>();
        waitlist = new ArrayList<>();

        playlistAdapter = new SongAdapter(
                playlist,
                song -> {
                    // Song clicked in the playlist (no special action needed here for now)
                },
                (song, moveToWaitlist) -> {
                    // Handle "Move to Waitlist"
                    if (moveToWaitlist) {
                        moveSongToWaitlist(song);
                    }
                },
                true
        );

        executor.execute(() -> {
            try {
                // Fetch songs from Spotify
                String accessToken = spotifyService.getAccessToken();
                List<Song> fetchedSongs = spotifyService.getSongs(accessToken, "2z40H5YqP206Xam6J88Lta");

                runOnUiThread(() -> {
                    playlist.clear();
                    playlist.addAll(fetchedSongs);
                    // Same as guest main page - once the songs have been fetched, notify the adaptor to update the UI
                    playlistAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to fetch songs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });


        waitlistAdapter = new SongAdapter(
                waitlist,
                song -> {
                    // Song clicked in the waitlist (no special action needed here for now)
                },
                (song, moveToWaitlist) -> {
                    // Handle "Move to Playlist"
                    if (!moveToWaitlist) {
                        moveSongToPlaylist(song);
                    }
                },
                true
        );

        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPlaylist.setAdapter(playlistAdapter);

        recyclerViewWaitlist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWaitlist.setAdapter(waitlistAdapter);

        addSongButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Add_Song_Page.class);
            startActivityForResult(intent, REQUEST_ADD_SONG);
        });

        // 設置按鈕的點擊事件
        startPlayerButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, Player_Page.class);
                startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_SONG && resultCode == RESULT_OK && data != null) {
            String songTitle = data.getStringExtra("songTitle");
            String songArtist = data.getStringExtra("songArtist");
            String songTrackUri = data.getStringExtra("songTrackUri");

            // Check if song already exists
            Song newSong = new Song(songTitle, songArtist, songTrackUri);
            if (!playlist.contains(newSong)) {
                playlist.add(newSong);
                playlistAdapter.notifyItemInserted(playlist.size() - 1);
            } else {
                // Handle duplicate song (you can show a Toast or notify the user)
                Toast.makeText(this, "Song already exists in the playlist", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveSongToWaitlist(Song song) {
        if (playlist.contains(song)) {
            playlist.remove(song);
            waitlist.add(song);
            playlistAdapter.notifyDataSetChanged();
            waitlistAdapter.notifyDataSetChanged();
            executor.execute(() -> {
                try {
                    // Fetch songs from Spotify
                    String accessToken = spotifyService.getAccessToken();
                    spotifyService.deleteTrackFromPlaylist(accessToken, "2z40H5YqP206Xam6J88Lta", song.getTrackUri());
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Failed to fetch songs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            });
        }
    }

    private void moveSongToPlaylist(Song song) {
        if (waitlist.contains(song)) {
            waitlist.remove(song);
            playlist.add(song);
            playlistAdapter.notifyDataSetChanged();
            waitlistAdapter.notifyDataSetChanged();
            executor.execute(() -> {
                try {
                    // Fetch songs from Spotify
                    String accessToken = spotifyService.getAccessToken();
                    spotifyService.addTracksToPlaylist(accessToken, "2z40H5YqP206Xam6J88Lta", new String[]{song.getTrackUri()});
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Failed to fetch songs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            });
        }
    }
}
