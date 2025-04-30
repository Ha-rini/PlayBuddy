package com.example.play_buddy_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class BookingConfirmationActivity extends AppCompatActivity {

    TextView sportText, dateText, timeText, venueText, peopleText, hoursText, totalRateText;
    Button backToHomeButton;

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookingconfirmation);

        db = FirebaseFirestore.getInstance();

        // Get booking details from intent
        Intent intent = getIntent();
        String bookingId = intent.getStringExtra("bookingId"); // Passed from PaymentActivity
        String sport = intent.getStringExtra("sport");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String venue = intent.getStringExtra("venue");
        int numPeople = intent.getIntExtra("numPeople", 0);
        int numHours = intent.getIntExtra("numHours", 0);
        int totalRate = intent.getIntExtra("totalRate", 0);

        // Initialize UI
        sportText = findViewById(R.id.booking_details);
        dateText = findViewById(R.id.booking_details_date);
        timeText = findViewById(R.id.booking_details_time);
        venueText = findViewById(R.id.booking_venue);
        peopleText = findViewById(R.id.booking_people);
        hoursText = findViewById(R.id.booking_hours);
        totalRateText = findViewById(R.id.booking_total_rate);
        backToHomeButton = findViewById(R.id.back_to_home_button);

        // Set the booking details
        sportText.setText("Sport: " + sport);
        dateText.setText("Date: " + date);
        timeText.setText("Time: " + time);
        venueText.setText("Venue: " + venue);
        peopleText.setText("People: " + numPeople);
        hoursText.setText("Hours: " + numHours);
        totalRateText.setText("Total Paid: ₹" + totalRate);

        //  Update booking status to "confirmed" in Firestore
        if (bookingId != null) {
            db.collection("bookings").document(bookingId)
                    .update("status", "confirmed")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to confirm booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        // Handle back to home
        backToHomeButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(BookingConfirmationActivity.this, DashboardActivity.class);
            backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backIntent);
            finish();
        });
    }
}
