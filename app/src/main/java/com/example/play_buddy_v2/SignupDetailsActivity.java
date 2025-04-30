package com.example.play_buddy_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupDetailsActivity extends AppCompatActivity {

    private EditText etUsername, etPhone, etCity, etLocality;
    private Button btnSaveDetails;
    private ProgressBar progressBarDetails;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String emailFromPreviousScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupdetails);

        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etLocation);
        etLocality = findViewById(R.id.etLocality);
        btnSaveDetails = findViewById(R.id.btnSignupComplete);
        progressBarDetails = findViewById(R.id.progressBarSignupDetails);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get email from previous screen
        emailFromPreviousScreen = getIntent().getStringExtra("email");

        btnSaveDetails.setOnClickListener(v -> saveUserDetails());
    }

    private void saveUserDetails() {
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String locality = etLocality.getText().toString().trim();

        progressBarDetails.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            progressBarDetails.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            progressBarDetails.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, "Please enter your city", Toast.LENGTH_SHORT).show();
            progressBarDetails.setVisibility(View.GONE);
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("name", username); // for welcome message in Login
        user.put("username", username);
        user.put("phone", phone);
        user.put("city", city);
        user.put("locality", locality);
        user.put("email", emailFromPreviousScreen);

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(task -> {
                    progressBarDetails.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(SignupDetailsActivity.this, "Details saved successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupDetailsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupDetailsActivity.this, "Error saving details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
