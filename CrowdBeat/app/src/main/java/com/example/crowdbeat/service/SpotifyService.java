package com.example.crowdbeat.service;

import com.example.crowdbeat.models.Song;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SpotifyService {

    private static final String BASE_URL = "https://api.spotify.com/v1";
    private static final OkHttpClient client = new OkHttpClient();

    private String CLIENT_ID = "93e7c2ce3d524cdb9bb67b3bc8c5cd5f";

    private String CLIENT_SECRET = "1bdaaefc7de84c8497af10ab966a309b";

    private String AUTHORIZATION_CODE = "AQCQMLn9jCWipCYwYleo6JSeVC97KvQB1tx21Nx9UU1ExgCOgsuuXVP5QNZTusfaRzR0810ahezxtrVLFMXza8Yx10nXAd4yp1Pu3xiWfAWlBcLur9MR-c-g-xSYAhkvMWgb7XFSvQ55w8Kcfc6HhV0Dc5s8mAEX19eU6r7mwkXbouE6sxy_ZBBAao9xyqVzrrtp-ANF916vdia1gMQ_L-FxrBTdzorTqtodJC5aF70oHME";

    private String REFRESH_TOKEN = "AQDrhoGgPsDcvBzVbo-AZAG08eEr6amJNsB-EoWtR3NZ0ejo710mFdTI-meDprSpOF5Q_0qq1AlS1BZG1j4KCS--U3M62vU7Hjdic7UKcNgy_HOP1lNM9_oNh_33DKL9jlE";

    public String getAccessToken() throws IOException, JSONException {
        String url = "https://accounts.spotify.com/api/token";
        String credentials = Credentials.basic(CLIENT_ID, CLIENT_SECRET);

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", REFRESH_TOKEN)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", credentials)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            String accessToken = json.getString("access_token");
            System.out.println("Access token is " + accessToken);
            return accessToken;
        }
    }

    public void addTracksToPlaylist(String accessToken, String playlistId, String[] trackUris) throws IOException, JSONException {
        String url = BASE_URL + "/playlists/" + playlistId + "/tracks";

        JSONObject jsonBody = new JSONObject();
        JSONArray tracksArray = new JSONArray();
        for(String trackUri: trackUris) {
            tracksArray.put(trackUri);
        }
        jsonBody.put("uris", tracksArray);

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        }
    }

    public List<Song> getSongs(String accessToken, String playlistId) throws IOException, JSONException {
        String url = BASE_URL + "/playlists/" + playlistId + "/tracks";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code() + ": " + response.body().string());
            }

            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONArray items = jsonResponse.getJSONArray("items");

            // Extract song details into Song objects
            List<Song> songs = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject trackObject = items.getJSONObject(i).getJSONObject("track");
                String songTitle = trackObject.getString("name");
                String artistName = trackObject.getJSONArray("artists").getJSONObject(0).getString("name");
                String trackUri = trackObject.getString("uri");

                // Create a Song object and add it to the list
                songs.add(new Song(songTitle, artistName, trackUri));
            }

            return songs;
        }
    }

    public List<Song> searchSongs(String accessToken, String query) throws IOException, JSONException {
        // Encode query string
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String url = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=track&limit=5";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code() + ": " + response.body().string());
            }

            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONArray items = jsonResponse.getJSONObject("tracks").getJSONArray("items");

            // Extract song details into Song objects
            List<Song> songs = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject trackObject = items.getJSONObject(i);
                String songTitle = trackObject.getString("name");
                String artistName = trackObject.getJSONArray("artists").getJSONObject(0).getString("name");
                String trackUri = trackObject.getString("uri");

                // Create a Song object and add it to the list
                songs.add(new Song(songTitle, artistName, trackUri));
            }

            return songs;
        }
    }

    public Song getTrackDetails(String accessToken, String trackId) throws IOException, JSONException {
        String url = "https://api.spotify.com/v1/tracks/" + trackId;

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code() + ": " + response.body().string());
            }

            // Parse the response
            JSONObject jsonResponse = new JSONObject(response.body().string());
            String name = jsonResponse.getString("name");
            String artist = jsonResponse.getJSONArray("artists").getJSONObject(0).getString("name");

            // Create and return a Song object
            String trackUri = "spotify:track:" + trackId;
            return new Song(name, artist, trackUri);
        }
    }

    public void deleteTrackFromPlaylist(String accessToken, String playlistId, String trackUri) throws IOException, JSONException {
        String url = BASE_URL + "/playlists/" + playlistId + "/tracks";

        JSONObject jsonBody = new JSONObject();
        JSONArray tracksArray = new JSONArray();

        JSONObject trackObject = new JSONObject();
        trackObject.put("uri", trackUri);
        tracksArray.put(trackObject);

        jsonBody.put("tracks", tracksArray);

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }


}
