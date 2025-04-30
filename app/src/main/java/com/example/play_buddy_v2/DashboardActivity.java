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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardActivity extends AppCompatActivity {

    private ImageView logoImage;
    private ImageButton friendRequestsButton, profileButton;
    private Button createEventButton;
    private TextView eventName1, eventDetails1, eventName2, eventDetails2, eventName3, eventDetails3;

    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logoImage = findViewById(R.id.logo_image);
        friendRequestsButton = findViewById(R.id.friend_requests_button);
        profileButton = findViewById(R.id.profile_button);
        createEventButton = findViewById(R.id.create_event_button);

        eventName1 = findViewById(R.id.event_name_1);
        eventDetails1 = findViewById(R.id.event_details_1);
        eventName2 = findViewById(R.id.event_name_2);
        eventDetails2 = findViewById(R.id.event_details_2);
        eventName3 = findViewById(R.id.event_name_3);
        eventDetails3 = findViewById(R.id.event_details_3);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        friendRequestsButton.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Friend Requests button clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DashboardActivity.this, FriendRequestsActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Profile button clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
        });

        createEventButton.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Create New Activity clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DashboardActivity.this, CreateNewActivity.class));
        });

        fetchUserEvents();
    }

    private void fetchUserEvents() {
        Set<String> eventIdsToShow = new HashSet<>();
        List<DocumentSnapshot> finalEvents = new ArrayList<>();

        // Step 1: Get accepted requests where user is to_user_id
        db.collection("requests")
                .whereEqualTo("to_user_id", userId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(requestSnapshots -> {
                    for (QueryDocumentSnapshot request : requestSnapshots) {
                        String eventId = request.getString("event_id");
                        if (eventId != null) {
                            eventIdsToShow.add(eventId);
                        }
                    }

                    // Step 2: Get events created by the user
                    db.collection("events")
                            .whereEqualTo("createdBy", userId)
                            .get()
                            .addOnSuccessListener(eventSnapshots -> {
                                for (QueryDocumentSnapshot doc : eventSnapshots) {
                                    if (!eventIdsToShow.contains(doc.getId())) {
                                        eventIdsToShow.add(doc.getId());
                                    }
                                }

                                // Step 3: Fetch all unique event details
                                if (eventIdsToShow.isEmpty()) {
                                    updateDashboardUI(finalEvents);
                                } else {
                                    fetchEventDetails(new ArrayList<>(eventIdsToShow), finalEvents);
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(DashboardActivity.this, "Error fetching created events: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(DashboardActivity.this, "Error fetching accepted requests: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void fetchEventDetails(List<String> eventIds, List<DocumentSnapshot> finalEvents) {
        for (String eventId : eventIds) {
            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            finalEvents.add(doc);
                            if (finalEvents.size() == eventIds.size()) {
                                updateDashboardUI(finalEvents);
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(DashboardActivity.this, "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void updateDashboardUI(List<DocumentSnapshot> events) {
        int index = 0;
        for (DocumentSnapshot doc : events) {
            String eventName = doc.getString("eventName");
            String eventDetails = "Place: " + doc.getString("place") +
                    "\nSport: " + doc.getString("sportType") +
                    " | " + doc.getString("maxPlayers") + " Players" +
                    "\nDate: " + doc.getString("date") +
                    " | Time: " + doc.getString("time");

            switch (index) {
                case 0:
                    eventName1.setText(eventName);
                    eventDetails1.setText(eventDetails);
                    break;
                case 1:
                    eventName2.setText(eventName);
                    eventDetails2.setText(eventDetails);
                    break;
                case 2:
                    eventName3.setText(eventName);
                    eventDetails3.setText(eventDetails);
                    break;
            }

            index++;
            if (index >= 3) break;
        }

        if (events.isEmpty()) {
            eventName1.setText("No upcoming events");
            eventDetails1.setText("");
            eventName2.setText("");
            eventDetails2.setText("");
            eventName3.setText("");
            eventDetails3.setText("");
        }
    }
}
