package com.example.dustbinpro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookingActivity extends AppCompatActivity {

    private EditText addressEditText, dateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        addressEditText = findViewById(R.id.addressEditText);
        dateEditText = findViewById(R.id.dateEditText);
        Button pickDateButton = findViewById(R.id.pickDateButton);
        Button confirmBookingButton = findViewById(R.id.confirmBookingButton);

        pickDateButton.setOnClickListener(v -> openDatePicker());

        confirmBookingButton.setOnClickListener(v -> confirmBooking());
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateEditText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void confirmBooking() {
        String address = addressEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        if (address.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please enter address and pick a date!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare booking data
        Map<String, Object> booking = new HashMap<>();
        booking.put("address", address);
        booking.put("date", date);
        booking.put("userId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        booking.put("timestamp", System.currentTimeMillis());


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Booking Confirmed Successfully!", Toast.LENGTH_LONG).show();
                    finish(); // Close this activity and go back
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to confirm booking: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}

