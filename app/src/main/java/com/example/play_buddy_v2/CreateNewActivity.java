package com.example.play_buddy_v2;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class CreateNewActivity extends AppCompatActivity implements OnMapReadyCallback {

    Spinner sportSpinner;
    EditText datePicker, timePicker, venueInput, friendSearch;
    Button createActivityButton;

    MapView mapView;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationClient;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    RequestQueue requestQueue;

    LatLng currentLatLng = new LatLng(19.0760, 72.8777); // default to Mumbai
    String selectedVenue = "";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your actual API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);

        sportSpinner = findViewById(R.id.sport_spinner);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        venueInput = findViewById(R.id.venue_input);
        friendSearch = findViewById(R.id.friend_search);
        createActivityButton = findViewById(R.id.create_activity_button);
        mapView = findViewById(R.id.map_container);

        venueInput.setKeyListener(null); // make venueInput non-editable

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestQueue = Volley.newRequestQueue(this);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        setupSportSpinner();
        setupDatePicker();
        setupTimePicker();
        setupCreateButton();
    }

    private void setupSportSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.sports_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(adapter);

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap != null) {
                    String selectedSport = parent.getItemAtPosition(position).toString();
                    if (!selectedSport.equals("Select Sport")) {
                        fetchNearbyVenues(selectedSport);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDatePicker() {
        datePicker.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) ->
                            datePicker.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupTimePicker() {
        timePicker.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this,
                    (view, hourOfDay, minute) ->
                            timePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void setupCreateButton() {
        createActivityButton.setOnClickListener(view -> {
            String sport = sportSpinner.getSelectedItem().toString();
            String date = datePicker.getText().toString();
            String time = timePicker.getText().toString();
            String venue = selectedVenue;
            String friendUsername = friendSearch.getText().toString();

            if (sport.equals("Select Sport")) {
                Toast.makeText(this, "Please select a sport", Toast.LENGTH_SHORT).show();
                return;
            }
            if (date.isEmpty() || time.isEmpty() || venue.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> event = new HashMap<>();
            event.put("sport", sport);
            event.put("date", date);
            event.put("time", time);
            event.put("venue", venue);
            event.put("createdBy", currentUser != null ? currentUser.getUid() : "Unknown");
            event.put("friend", friendUsername);

            db.collection("events")
                    .add(event)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();

                        if (!friendUsername.isEmpty()) {
                            Map<String, Object> request = new HashMap<>();
                            request.put("from", currentUser != null ? currentUser.getUid() : "Unknown");
                            request.put("toUsername", friendUsername);
                            request.put("eventId", documentReference.getId());
                            request.put("status", "pending");

                            db.collection("requests").add(request);
                        }

                        Intent intent = new Intent(CreateNewActivity.this, BookingActivity.class);
                        intent.putExtra("sport", sport);
                        intent.putExtra("date", date);
                        intent.putExtra("time", time);
                        intent.putExtra("venue", venue);
                        intent.putExtra("friends", friendUsername);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(marker -> {
            selectedVenue = marker.getTitle();
            venueInput.setText(selectedVenue);
            return false;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    String sport = sportSpinner.getSelectedItem().toString();
                    if (!sport.equals("Select Sport")) {
                        fetchNearbyVenues(sport);
                    }
                }
            });
        }
    }

    private void fetchNearbyVenues(String sport) {
        mMap.clear();
        double lat = currentLatLng.latitude;
        double lng = currentLatLng.longitude;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + lat + "," + lng +
                "&radius=3000" +
                "&keyword=" + Uri.encode(sport + " court") +
                "&type=establishment" +
                "&key=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            String name = results.getJSONObject(i).getString("name");
                            JSONObject loc = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                            LatLng position = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));

                            mMap.addMarker(new MarkerOptions()
                                    .position(position)
                                    .title(name));
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to parse places: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching venues: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    // MapView lifecycle
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        } else {
            Toast.makeText(this, "Location permission is required to use map.", Toast.LENGTH_SHORT).show();
        }
    }
}
