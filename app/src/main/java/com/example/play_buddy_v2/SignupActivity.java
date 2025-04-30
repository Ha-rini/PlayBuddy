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

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSignup;
    private ProgressBar progressBarSignup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnNext);
        progressBarSignup = findViewById(R.id.progressBarSignup);

        mAuth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(v -> createUser());
    }

    private void createUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        progressBarSignup.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            progressBarSignup.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            progressBarSignup.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBarSignup.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Sign-up successful! Please provide additional details.", Toast.LENGTH_SHORT).show();

                        // Pass email to next screen
                        Intent intent = new Intent(SignupActivity.this, SignupDetailsActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
