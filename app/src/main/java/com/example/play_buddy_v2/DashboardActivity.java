package com.example.play_buddy_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private ImageView logoImage;
    private ImageButton messagesButton, friendRequestsButton, profileButton;
    private Button createEventButton;
    private TextView eventName1, eventDetails1, eventName2, eventDetails2, eventName3, eventDetails3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI elements
        logoImage = findViewById(R.id.logo_image);
        messagesButton = findViewById(R.id.messages_button);
        friendRequestsButton = findViewById(R.id.friend_requests_button);
        profileButton = findViewById(R.id.profile_button);
        createEventButton = findViewById(R.id.create_event_button);

        // Event Names and Details
        eventName1 = findViewById(R.id.event_name_1);
        eventDetails1 = findViewById(R.id.event_details_1);
        eventName2 = findViewById(R.id.event_name_2);
        eventDetails2 = findViewById(R.id.event_details_2);
        eventName3 = findViewById(R.id.event_name_3);
        eventDetails3 = findViewById(R.id.event_details_3);

        // Set up click listeners for ImageButtons
        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Messages button clicked", Toast.LENGTH_SHORT).show();
                // Intent to navigate to Messages screen
                Intent intent = new Intent(DashboardActivity.this, MessagesActivity.class);
                startActivity(intent);
            }
        });

        friendRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Friend Requests button clicked", Toast.LENGTH_SHORT).show();
                // Intent to navigate to Friend Requests screen
                Intent intent = new Intent(DashboardActivity.this, FriendRequestsActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Profile button clicked", Toast.LENGTH_SHORT).show();
                // Intent to navigate to Profile screen
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set up click listener for Create Event button
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Create New Activity clicked", Toast.LENGTH_SHORT).show();
                // Navigate to Create Event Activity
                Intent intent = new Intent(DashboardActivity.this, CreateNewActivity.class);
                startActivity(intent);
            }
        });

        // Populate event details (can be dynamic based on API response)
        populateEventDetails();
    }

    private void populateEventDetails() {
        // For now, populate static text for each event (can be replaced by dynamic content)
        eventName1.setText("Foos Football");
        eventDetails1.setText("Place: Combat Arena\nSport: Wrestling | 1/2 Players\nDate: 24 Feb | Time: 3:00 PM");

        eventName2.setText("Power Soccer Club");
        eventDetails2.setText("Place: Soccer City\nSport: Soccer | 6/8 Players\nDate: 22 Feb | Time: 6:00 PM");

        eventName3.setText("Axe Wrestling Academy");
        eventDetails3.setText("Place: Combat Arena\nSport: Wrestling | 1/2 Players\nDate: 24 Feb | Time: 3:00 PM");
    }
}
