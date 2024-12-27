package com.example.crowdbeat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Guest_Join_Event_Page extends AppCompatActivity {

    private EditText joinCodeEditText;
    private Button backButton;
    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_join_event_page);

        // Initialize views
        joinCodeEditText = findViewById(R.id.joinCodeEditText);
        // backButton = findViewById(R.id.backButton);
        joinButton = findViewById(R.id.joinButton);

        // Set click listeners
//        backButton.setOnClickListener(v -> finish()); // Finishes activity, going back to the previous page
        joinButton.setOnClickListener(v -> joinEvent());
    }

    private void joinEvent() {
        String joinCode = joinCodeEditText.getText().toString();

        if (joinCode.isEmpty() || joinCode.length() != 5) {
            Toast.makeText(this, "Please enter a valid 5-digit code", Toast.LENGTH_SHORT).show();
        } else {
            // Intent to proceed to the next activity
            Intent intent = new Intent(Guest_Join_Event_Page.this, Guest_Preference_Page.class);
            intent.putExtra("JOIN_CODE", joinCode);
            startActivity(intent);
        }
    }
}
