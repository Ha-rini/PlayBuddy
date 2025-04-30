package com.example.play_buddy_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {

    TextView userNameHeader, name, phone, email, locality;
    Button logoutButton;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        userNameHeader = findViewById(R.id.user_name_header);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        locality = findViewById(R.id.locality);
        logoutButton = findViewById(R.id.logout_button);

        // Fetch the current user's UID
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Fetch user data from Firestore
        fetchUserData(userId);

        // Logout button click listener
        logoutButton.setOnClickListener(view -> {
            // Sign out the user
            firebaseAuth.signOut();
            // Navigate back to LoginActivity
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUserData(String userId) {
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the user data from the Firestore document
                        String userName = documentSnapshot.getString("username");
                        String userPhone = documentSnapshot.getString("phone");
                        String userEmail = documentSnapshot.getString("email");
                        String userLocality = documentSnapshot.getString("locality");

                        // Set the fetched data to the TextViews
                        userNameHeader.setText(userName);
                        name.setText(userName);
                        phone.setText(userPhone);
                        email.setText(userEmail);
                        locality.setText(userLocality);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur while fetching data
                    userNameHeader.setText("Error fetching user data");
                });
    }
}
