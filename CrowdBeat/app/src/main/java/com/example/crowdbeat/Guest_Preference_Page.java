package com.example.crowdbeat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Guest_Preference_Page extends AppCompatActivity {

    private EditText etName;
    private Spinner spinnerMusicLanguage;
    private Spinner spinnerMusicGenre;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guest_preference_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        spinnerMusicLanguage = findViewById(R.id.spinnerMusicLanguage);
        spinnerMusicGenre = findViewById(R.id.spinnerMusicGenre);
        btnSubmit = findViewById(R.id.btnSubmit);


        // Set up data with default hint for music language and genre
        List<String> musicLanguages = new ArrayList<>(Arrays.asList("Select your preferred language", "English", "Spanish", "French", "Hindi"));
        List<String> musicGenres = new ArrayList<>(Arrays.asList("Select your preferred genre", "Pop", "Rock", "Jazz", "Classical"));

        // Set up adapters for the spinners with the first item as a hint
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicLanguages) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE); // Set color for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE); // Set color for dropdown items
                return view;
            }
        };
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMusicLanguage.setAdapter(languageAdapter);

        ArrayAdapter<String> genreAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicGenres) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE); // Set color for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE); // Set color for dropdown items
                return view;
            }
        };
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMusicGenre.setAdapter(genreAdapter);


        // Set listeners for spinners to check if a valid option is selected
        btnSubmit.setOnClickListener(v -> {
            // Retrieve user input
            String userName = etName.getText().toString().trim();
            String selectedLanguage = spinnerMusicLanguage.getSelectedItem().toString();
            String selectedGenre = spinnerMusicGenre.getSelectedItem().toString();

            // Basic validation
            if (userName.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            } else if (selectedLanguage.equals("Select your preferred language")) {
                Toast.makeText(this, "Please select a music language", Toast.LENGTH_SHORT).show();
                return;
            } else if (selectedGenre.equals("Select your preferred genre")) {
                Toast.makeText(this, "Please select a music genre", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to Guest Main Page with user preferences
            Intent intent = new Intent(Guest_Preference_Page.this, Guest_Main_Page.class);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("MUSIC_LANGUAGE", selectedLanguage);
            intent.putExtra("MUSIC_GENRE", selectedGenre);
            startActivity(intent);
        });
    }
}