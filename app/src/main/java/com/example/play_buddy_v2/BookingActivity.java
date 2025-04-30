package com.example.play_buddy_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    TextView bookingSummaryTextView;
    EditText numPeopleInput, numHoursInput;
    Button proceedToPaymentButton;

    String sport, date, time, venue;
    int ratePerHour = 200; // You can change this as needed

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        bookingSummaryTextView = findViewById(R.id.booking_summary);
        numPeopleInput = findViewById(R.id.num_people_input);
        numHoursInput = findViewById(R.id.num_hours_input);
        proceedToPaymentButton = findViewById(R.id.proceed_to_payment);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get data from CreateNewActivity
        Intent intent = getIntent();
        sport = intent.getStringExtra("sport");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        venue = intent.getStringExtra("venue");

        String summary = "Sport: " + sport + "\nDate: " + date + "\nTime: " + time + "\nVenue: " + venue;
        bookingSummaryTextView.setText(summary);

        proceedToPaymentButton.setOnClickListener(view -> {
            String numPeopleStr = numPeopleInput.getText().toString();
            String numHoursStr = numHoursInput.getText().toString();

            if (TextUtils.isEmpty(numPeopleStr) || TextUtils.isEmpty(numHoursStr)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int numPeople = Integer.parseInt(numPeopleStr);
            int numHours = Integer.parseInt(numHoursStr);
            int totalRate = numHours * ratePerHour;

            String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown_user";

            // Prepare booking data
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("userId", userId);
            bookingData.put("sport", sport);
            bookingData.put("date", date);
            bookingData.put("time", time);
            bookingData.put("venue", venue);
            bookingData.put("numPeople", numPeople);
            bookingData.put("numHours", numHours);
            bookingData.put("totalRate", totalRate);
            bookingData.put("status", "pending"); // Will be updated on payment

            // Add booking to Firestore
            db.collection("bookings")
                    .add(bookingData)
                    .addOnSuccessListener(documentReference -> {
                        String bookingId = documentReference.getId();
                        Toast.makeText(this, "Booking saved!", Toast.LENGTH_SHORT).show();

                        // Pass data to PaymentActivity
                        Intent paymentIntent = new Intent(BookingActivity.this, PaymentActivity.class);
                        paymentIntent.putExtra("sport", sport);
                        paymentIntent.putExtra("date", date);
                        paymentIntent.putExtra("time", time);
                        paymentIntent.putExtra("venue", venue);
                        paymentIntent.putExtra("numPeople", numPeople);
                        paymentIntent.putExtra("numHours", numHours);
                        paymentIntent.putExtra("totalRate", totalRate);
                        paymentIntent.putExtra("bookingId", bookingId);
                        startActivity(paymentIntent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}
