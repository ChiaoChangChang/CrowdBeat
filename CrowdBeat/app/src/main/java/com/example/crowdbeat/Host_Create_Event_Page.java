package com.example.crowdbeat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Host_Create_Event_Page extends AppCompatActivity {

    private EditText eventNameEditText;
    private TextView randomCodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_create_event_page);

        // Initialize views
        eventNameEditText = findViewById(R.id.eventNameEditText);
        randomCodeTextView = findViewById(R.id.randomCodeTextView);
        // Button backButton = findViewById(R.id.backButton);
        Button regenerateCodeButton = findViewById(R.id.regenerateCodeButton);
        Button createRoomButton = findViewById(R.id.createRoomButton);

        // Set click listeners
        // backButton.setOnClickListener(v -> finish()); // Finishes activity, going back to the previous page
        regenerateCodeButton.setOnClickListener(v -> generateNewCode());
        createRoomButton.setOnClickListener(v -> createRoom());

        // Generate an initial random code
        generateNewCode();
    }

    private void generateNewCode() {
        // Generate a random 5-digit code
        Random random = new Random();
        int code = 10000 + random.nextInt(90000); // Ensures 5 digits
        randomCodeTextView.setText(String.valueOf(code));
    }

    private void createRoom() {
        String eventName = eventNameEditText.getText().toString().trim();

        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show();
        } else {
            // Intent to proceed to the next activity
            Intent intent = new Intent(Host_Create_Event_Page.this, Host_Main_Page.class);
            intent.putExtra("EVENT_NAME", eventName);
            intent.putExtra("EVENT_CODE", randomCodeTextView.getText().toString());
            startActivity(intent);
        }
    }
}
