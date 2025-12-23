package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class BookCleaningActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private Spinner serviceTypeSpinner, timeSlotSpinner;
    private EditText addressInput, specialRequestsInput;
    private Button submitButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cleaning);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupSpinners();
        setupSubmitButton();
    }

    private void initializeViews() {
        serviceTypeSpinner = findViewById(R.id.serviceTypeSpinner);
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner);
        addressInput = findViewById(R.id.addressInput);
        specialRequestsInput = findViewById(R.id.specialRequestsInput);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        // Service types
        ArrayAdapter<CharSequence> serviceAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.service_types,
                android.R.layout.simple_spinner_item
        );
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceTypeSpinner.setAdapter(serviceAdapter);

        // Time slots
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_slots,
                android.R.layout.simple_spinner_item
        );
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(timeAdapter);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                submitBooking();
            }
        });
    }

    private boolean validateForm() {
        String address = addressInput.getText().toString().trim();

        if (address.isEmpty()) {
            addressInput.setError("Address is required");
            addressInput.requestFocus();
            return false;
        }

        return true;
    }

    private void submitBooking() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        submitButton.setEnabled(false);

        String userId = auth.getCurrentUser().getUid();
        String serviceType = serviceTypeSpinner.getSelectedItem().toString();
        String timeSlot = timeSlotSpinner.getSelectedItem().toString();
        String address = addressInput.getText().toString().trim();
        String specialRequests = specialRequestsInput.getText().toString().trim();
        String bookingId = "BK" + System.currentTimeMillis();

        Map<String, Object> booking = new HashMap<>();
        booking.put("bookingId", bookingId);
        booking.put("customerId", userId);
        booking.put("serviceType", serviceType);
        booking.put("timeSlot", timeSlot);
        booking.put("address", address);
        booking.put("specialRequests", specialRequests);
        booking.put("status", "Pending");
        booking.put("createdAt", new Date());
        booking.put("bookingDate", new Date()); // You can add date picker for this

        db.collection("bookings").document(bookingId)
                .set(booking)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    submitButton.setEnabled(true);

                    Toast.makeText(this, "Booking submitted successfully!", Toast.LENGTH_LONG).show();

                    // Redirect to booking history
                    Intent intent = new Intent(this, BookingHistoryActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    submitButton.setEnabled(true);
                    Toast.makeText(this, "Failed to submit booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}