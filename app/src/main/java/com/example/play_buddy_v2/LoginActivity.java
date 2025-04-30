package com.example.play_buddy_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignupLink;
    private ProgressBar progressBarLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignupLink = findViewById(R.id.tvSignupLink);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        progressBarLogin.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            progressBarLogin.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            progressBarLogin.setVisibility(View.GONE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBarLogin.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(userId)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document = task1.getResult();
                                            if (document.exists()) {
                                                String name = document.getString("name");
                                                Toast.makeText(LoginActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
