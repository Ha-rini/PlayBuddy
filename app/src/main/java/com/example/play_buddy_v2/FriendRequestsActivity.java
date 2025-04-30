package com.example.play_buddy_v2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FriendRequestsActivity extends AppCompatActivity {

    private LinearLayout container;
    private FirebaseFirestore db;
    private String currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendrequests);

        container = findViewById(R.id.friend_requests_container);
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Optional: Fetch current user's name (if needed)
        db.collection("users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
            currentUserName = documentSnapshot.getString("username");
            loadFriendRequests();
        });
    }

    private void loadFriendRequests() {
        db.collection("requests")
                .whereEqualTo("toUserId", currentUserId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Map<String, Object> request = doc.getData();

                        addRequestCard(
                                (String) request.get("fromUsername"),
                                (String) request.get("fromPhone"),
                                (String) request.get("fromLocality"),
                                (String) request.get("sport"),
                                (String) request.get("date"),
                                (String) request.get("time"),
                                (String) request.get("venue"),
                                doc.getId(),
                                (String) request.get("eventId")
                        );
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                });
    }

    private void addRequestCard(String name, String phone, String locality,
                                String sport, String date, String time, String venue,
                                String requestId, String eventId) {

        View cardView = LayoutInflater.from(this).inflate(R.layout.friend_request_card, container, false);

        TextView nameText = cardView.findViewById(R.id.requester_name);
        TextView contactDetails = cardView.findViewById(R.id.contact_details);
        TextView eventDetails = cardView.findViewById(R.id.event_details);
        Button acceptBtn = cardView.findViewById(R.id.accept_button);
        Button rejectBtn = cardView.findViewById(R.id.reject_button);

        nameText.setText(name);
        contactDetails.setText("Phone: " + phone + "\nLocality: " + locality);
        eventDetails.setText(
                "Sport: " + sport + "\n" +
                        "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Venue: " + venue
        );

        // Accept
        acceptBtn.setOnClickListener(v -> {
            acceptRequest(requestId, eventId);
            container.removeView(cardView);
        });

        // Reject
        rejectBtn.setOnClickListener(v -> {
            rejectRequest(requestId);
            container.removeView(cardView);
        });

        container.addView(cardView);
    }

    private void acceptRequest(String requestId, String eventId) {
        DocumentReference requestRef = db.collection("requests").document(requestId);
        requestRef.update("status", "accepted")
                .addOnSuccessListener(aVoid -> {
                    addUserToEvent(eventId);
                    Toast.makeText(this, "Request Accepted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to accept request", Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectRequest(String requestId) {
        DocumentReference requestRef = db.collection("requests").document(requestId);
        requestRef.update("status", "rejected")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Request Rejected", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to reject request", Toast.LENGTH_SHORT).show();
                });
    }

    private void addUserToEvent(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.update("participants", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "You have been added to the event!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add you to event", Toast.LENGTH_SHORT).show();
                });
    }
}
