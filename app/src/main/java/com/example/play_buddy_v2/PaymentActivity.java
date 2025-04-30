package com.example.play_buddy_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {

    TextView totalAmountTextView, bookingDetailsTextView;
    EditText cardNumberInput, expiryInput, cvvInput;
    Button confirmPaymentButton;

    String sport, date, time, venue, bookingId;
    int numPeople, numHours;
    int totalRate;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = FirebaseFirestore.getInstance();

        totalAmountTextView = findViewById(R.id.total_amount);
        bookingDetailsTextView = findViewById(R.id.booking_info);
        cardNumberInput = findViewById(R.id.card_number_input);
        expiryInput = findViewById(R.id.expiry_input);
        cvvInput = findViewById(R.id.cvv_input);
        confirmPaymentButton = findViewById(R.id.confirm_payment);

        // Get all data passed from BookingActivity
        Intent intent = getIntent();
        sport = intent.getStringExtra("sport");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        venue = intent.getStringExtra("venue");
        numPeople = intent.getIntExtra("numPeople", 0);
        numHours = intent.getIntExtra("numHours", 0);
        totalRate = intent.getIntExtra("totalRate", 0);
        bookingId = intent.getStringExtra("bookingId"); // Important for updating Firestore

        totalAmountTextView.setText("Total: ₹" + totalRate);

        String bookingSummary = "Sport: " + sport +
                "\nDate: " + date +
                "\nTime: " + time +
                "\nVenue: " + venue +
                "\nPeople: " + numPeople +
                "\nHours: " + numHours;
        bookingDetailsTextView.setText(bookingSummary);

        confirmPaymentButton.setOnClickListener(view -> {
            String cardNumber = cardNumberInput.getText().toString();
            String expiry = expiryInput.getText().toString();
            String cvv = cvvInput.getText().toString();

            if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
                cardNumberInput.setError("Enter a valid 16-digit card number");
                return;
            }

            if (TextUtils.isEmpty(expiry)) {
                expiryInput.setError("Enter expiry date");
                return;
            }

            if (TextUtils.isEmpty(cvv) || cvv.length() != 3) {
                cvvInput.setError("Enter 3-digit CVV");
                return;
            }

            if (bookingId != null) {
                // ✅ Update booking status in Firestore
                db.collection("bookings").document(bookingId)
                        .update("status", "confirmed")
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();

                            // Go to confirmation screen
                            Intent confirmIntent = new Intent(PaymentActivity.this, BookingConfirmationActivity.class);
                            confirmIntent.putExtra("sport", sport);
                            confirmIntent.putExtra("date", date);
                            confirmIntent.putExtra("time", time);
                            confirmIntent.putExtra("venue", venue);
                            confirmIntent.putExtra("numPeople", numPeople);
                            confirmIntent.putExtra("numHours", numHours);
                            confirmIntent.putExtra("totalRate", totalRate);
                            confirmIntent.putExtra("bookingId", bookingId);
                            startActivity(confirmIntent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Payment failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(this, "Error: Missing booking ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
