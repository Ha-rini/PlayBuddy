package com.example.play_buddy_v2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CreateNewActivity extends AppCompatActivity {

    private EditText datePicker, timePicker, friendsInvite, venueSelect;
    private Spinner sportSpinner;
    private CheckBox filterByRate, filterByAmenities, filterByLocation;
    private Button createActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);

        // Initialize UI elements
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        friendsInvite = findViewById(R.id.friends_invite);
        venueSelect = findViewById(R.id.venue_select);
        sportSpinner = findViewById(R.id.sport_spinner);
        filterByRate = findViewById(R.id.filter_by_rate);
        filterByAmenities = findViewById(R.id.filter_by_amenities);
        filterByLocation = findViewById(R.id.filter_by_location);
        createActivityButton = findViewById(R.id.create_activity_button);

        // Set DatePicker dialog
        datePicker.setOnClickListener(v -> showDatePickerDialog());

        // Set TimePicker dialog
        timePicker.setOnClickListener(v -> showTimePickerDialog());

        // Set up the Create Event button
        createActivityButton.setOnClickListener(v -> {
            if (createNewActivity()) {
                // Navigate to the dummy payment page if activity creation is successful
                Intent intent = new Intent(CreateNewActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            datePicker.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = hourOfDay + ":" + String.format("%02d", minute1);
            timePicker.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private boolean createNewActivity() {
        String selectedSport = sportSpinner.getSelectedItem().toString();
        String selectedDate = datePicker.getText().toString();
        String selectedTime = timePicker.getText().toString();
        String friends = friendsInvite.getText().toString();
        String venue = venueSelect.getText().toString();

        boolean isFilterByRate = filterByRate.isChecked();
        boolean isFilterByAmenities = filterByAmenities.isChecked();
        boolean isFilterByLocation = filterByLocation.isChecked();

        if (selectedDate.isEmpty() || selectedTime.isEmpty() || venue.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // You can handle the logic to create the new activity here, for now, we are just displaying a message.
        String message = "Activity Created:\n" +
                "Sport: " + selectedSport + "\n" +
                "Date: " + selectedDate + "\n" +
                "Time: " + selectedTime + "\n" +
                "Venue: " + venue + "\n" +
                "Invite: " + friends + "\n" +
                "Filters Applied: " + (isFilterByRate ? "Rate " : "") +
                (isFilterByAmenities ? "Amenities " : "") +
                (isFilterByLocation ? "Location " : "");

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }
}
