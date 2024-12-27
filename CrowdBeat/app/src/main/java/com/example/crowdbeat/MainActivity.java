package com.example.crowdbeat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button hostButton;
    private Button guestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostButton = findViewById(R.id.host_button);
        guestButton = findViewById(R.id.guest_button);

        hostButton.setOnClickListener(this);
        guestButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.host_button) {
            Intent hostIntent = new Intent(MainActivity.this, Host_Create_Event_Page.class);
            startActivity(hostIntent);
        } else if (v.getId() == R.id.guest_button) {
            Intent guestIntent = new Intent(MainActivity.this, Guest_Join_Event_Page.class);
            startActivity(guestIntent);
        }
    }
}