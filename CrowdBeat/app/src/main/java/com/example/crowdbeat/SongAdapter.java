package com.example.crowdbeat;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crowdbeat.models.Song;
import java.util.Collections;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songs;
    private OnSongClickListener songClickListener;
    private OnMoveClickListener moveClickListener;

    private boolean isHost;

    // Single-method listener for song clicks
    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public interface OnMoveClickListener {
        void onMoveClick(Song song, boolean moveToWaitlist);
    }

    // Constructor with separate listeners
    public SongAdapter(List<Song> songs, OnSongClickListener songClickListener, OnMoveClickListener moveClickListener, boolean isHost) {
        this.songs = songs;
        this.songClickListener = songClickListener;
        this.moveClickListener = moveClickListener;
        this.isHost = isHost;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void swapItems(int fromPosition, int toPosition) {
        Collections.swap(songs, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSongTitle;
        private ImageButton btnMenu;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            btnMenu = itemView.findViewById(R.id.btnMenu);  // Three dot menu button

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Song song = songs.get(position);
                    songClickListener.onSongClick(song);
                }
            });

            // Set menu button click listener
            btnMenu.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Song song = songs.get(position);

                    // Create popup menu
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), btnMenu);
                    popupMenu.inflate(R.menu.song_menu);

                    // Dynamically change the text of the like item
                    MenuItem likeItem = popupMenu.getMenu().findItem(R.id.action_like);
                    if (song.isLiked()) {
                        likeItem.setTitle("Unlike");
                    } else {
                        likeItem.setTitle("Like");
                    }

                    // Dynamically change the text of the move item
                    MenuItem moveItem = popupMenu.getMenu().findItem(R.id.action_move);
                    moveItem.setVisible(isHost);
                    if (song.isInWaitlist()) {
                        moveItem.setTitle("Move to Playlist");
                    } else {
                        moveItem.setTitle("Move to Waitlist");
                    }


                    // Handle menu item clicks
                    popupMenu.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.action_like) {
                            song.setLiked(!song.isLiked()); // Toggle like state
                            notifyItemChanged(position);
                            return true;
                        } else if (item.getItemId() == R.id.action_move) {
                            boolean moveToWaitlist = !song.isInWaitlist();
                            song.setInWaitlist(moveToWaitlist); // Update the song's state
                            moveClickListener.onMoveClick(song, moveToWaitlist); // Notify the listener
                            notifyItemChanged(position); // Refresh the UI
                            return true;
                        } else {
                            return false;
                        }
                    });

                    // Show the popup menu
                    popupMenu.show();
                }
            });
        }

        public void bind(Song song) {
            tvSongTitle.setText(song.getTitle());
        }
    }
}
