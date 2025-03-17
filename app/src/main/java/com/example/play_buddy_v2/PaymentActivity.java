package com.example.play_buddy_v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private Button confirmPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        confirmPaymentButton = findViewById(R.id.confirm_payment_button);

        confirmPaymentButton.setOnClickListener(v -> {
            Toast.makeText(PaymentActivity.this, "Payment Successful!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the previous screen
        });
    }
}

