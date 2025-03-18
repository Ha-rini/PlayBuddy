package com.example.play_buddy_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnNext;
    private ProgressBar progressBarSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnNext = findViewById(R.id.btnNext);
        progressBarSignup = findViewById(R.id.progressBarSignup);

        // Next button click listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProceed();
            }
        });
    }

    private void validateAndProceed() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Show the progress bar while validating the input
        progressBarSignup.setVisibility(View.VISIBLE);

        // Validate fields
        if (TextUtils.isEmpty(email)) {
            showToast("Please enter an email");
            progressBarSignup.setVisibility(View.GONE);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email");
            progressBarSignup.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Please enter a password");
            progressBarSignup.setVisibility(View.GONE);
            return;
        }

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters");
            progressBarSignup.setVisibility(View.GONE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            progressBarSignup.setVisibility(View.GONE);
            return;
        }

        // Proceed to next screen for additional details
        progressBarSignup.setVisibility(View.GONE);
        Intent intent = new Intent(SignupActivity.this, SignupDetailsActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
