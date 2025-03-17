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

public class SignupDetailsActivity extends AppCompatActivity {

    private EditText etUsername, etPhone, etLocality, etLocation;
    private Button btnSignupComplete;
    private ProgressBar progressBarSignupDetails;

    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupdetails);

        // Get email and password from the previous screen
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etLocality = findViewById(R.id.etLocality);
        etLocation = findViewById(R.id.etLocation);
        btnSignupComplete = findViewById(R.id.btnSignupComplete);
        progressBarSignupDetails = findViewById(R.id.progressBarSignupDetails);

        // Complete Signup button listener
        btnSignupComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeSignup();
            }
        });
    }

    private void completeSignup() {
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String locality = etLocality.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Show the progress bar while validating the input
        progressBarSignupDetails.setVisibility(View.VISIBLE);

        // Validate fields
        if (TextUtils.isEmpty(username)) {
            showToast("Please enter a username");
            progressBarSignupDetails.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            showToast("Please enter a valid phone number");
            progressBarSignupDetails.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(locality)) {
            showToast("Please enter your locality");
            progressBarSignupDetails.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(location)) {
            showToast("Please enter your location");
            progressBarSignupDetails.setVisibility(View.GONE);
            return;
        }

        // Here you can save the data or send it to a server

        // After completing signup, go to login screen
        progressBarSignupDetails.setVisibility(View.GONE);
        Intent intent = new Intent(SignupDetailsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(SignupDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

