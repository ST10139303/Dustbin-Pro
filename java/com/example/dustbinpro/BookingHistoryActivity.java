package com.example.dustbinpro;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.adapters.BookingHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class BookingHistoryActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RecyclerView bookingsRecyclerView;
    private ProgressBar progressBar;
    private TextView noBookingsText;

    private List<Booking> bookings = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupRecyclerView();
        loadBookings();

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noBookingsText = findViewById(R.id.noBookingsText);
    }

    private void setupRecyclerView() {
        BookingHistoryAdapter adapter = new BookingHistoryAdapter(bookings);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingsRecyclerView.setAdapter(adapter);
    }

    private void loadBookings() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = auth.getCurrentUser().getUid();

        db.collection("bookings")
                .whereEqualTo("customerId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookings.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Booking booking = document.toObject(Booking.class);
                        bookings.add(booking);
                    }

                    ArrayAdapter<Object> adapter = null;
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (bookings.isEmpty()) {
                        noBookingsText.setVisibility(View.VISIBLE);
                    } else {
                        noBookingsText.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                });
    }

    // Booking model class
    public static class Booking {
        private String bookingId;
        private String serviceType;
        private String status;
        private String address;
        private String timeSlot;
        private Date bookingDate;
        private Date createdAt;

        // Getters and setters
        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }

        public String getServiceType() { return serviceType; }
        public void setServiceType(String serviceType) { this.serviceType = serviceType; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

        public Date getBookingDate() { return bookingDate; }
        public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    }
}