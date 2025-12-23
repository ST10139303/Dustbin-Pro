package com.example.dustbinpro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.adapters.AdminBookingsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.text.SimpleDateFormat;
import com.google.firebase.firestore.PropertyName;
import java.util.*;

public class BookingListActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RecyclerView bookingsRecyclerView;
    private ProgressBar progressBar;
    private TextView noBookingsText;

    private List<Booking> bookings = new ArrayList<>();
    private AdminBookingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

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
        adapter = new AdminBookingsAdapter(bookings, this);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingsRecyclerView.setAdapter(adapter);
    }

    public void loadBookings() {
        progressBar.setVisibility(View.VISIBLE);

        // Debug: Check if user is authenticated
        if (auth.getCurrentUser() == null) {
            Log.d("FirestoreDebug", "User not authenticated");
            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Log.d("FirestoreDebug", "Starting Firestore query...");

        db.collection("bookings")
                .orderBy("CreatedAt", Query.Direction.DESCENDING) // Changed to match your Firestore field name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("FirestoreDebug", "Query successful. Found " + queryDocumentSnapshots.size() + " documents");

                    bookings.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("FirestoreDebug", "No documents in query result");
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("FirestoreDebug", "Processing document: " + document.getId());
                            Log.d("FirestoreDebug", "Document data: " + document.getData());

                            try {
                                Booking booking = document.toObject(Booking.class);
                                booking.setBookingId(document.getId());
                                bookings.add(booking);
                                Log.d("FirestoreDebug", "Successfully mapped booking: " + booking.getCustomerName());
                            } catch (Exception e) {
                                Log.e("FirestoreDebug", "Error mapping document " + document.getId() + ": " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    Log.d("FirestoreDebug", "Final bookings list size: " + bookings.size());

                    if (bookings.isEmpty()) {
                        noBookingsText.setVisibility(View.VISIBLE);
                        bookingsRecyclerView.setVisibility(View.GONE);
                        Log.d("FirestoreDebug", "Showing 'No bookings found' message");
                    } else {
                        noBookingsText.setVisibility(View.GONE);
                        bookingsRecyclerView.setVisibility(View.VISIBLE);
                        Log.d("FirestoreDebug", "Showing bookings list with " + bookings.size() + " items");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreDebug", "Query failed: " + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load bookings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class Booking {
        private String bookingId;

        @PropertyName("CustomerName")
        private String customerName;

        @PropertyName("BookingAddress")
        private String address;

        @PropertyName("BookingDate")
        private Date date;

        @PropertyName("PreferredTime")
        private String timeSlot;

        @PropertyName("CreatedAt")
        private Date createdAt;

        @PropertyName("CustomerId")
        private String customerId;

        @PropertyName("AssignedWorker")
        private String assignedWorker;

        @PropertyName("UpdatedAt")
        private Date updatedAt;

        @PropertyName("WorkerStatus")
        private String workerStatus;

        @PropertyName("Status")
        private String status;

        @PropertyName("ServiceType")
        private String serviceType;

        @PropertyName("EstimatedPrice")
        private Double estimatedPrice;

        @PropertyName("FinalPrice")
        private Double finalPrice;

        @PropertyName("IsPriceSet")
        private Boolean isPriceSet;

        @PropertyName("PaymentStatus")
        private String paymentStatus;

        private String specialRequest; // only if you use it later

        // Default constructor
        public Booking() {
        }

        // Getters and Setters
        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getTimeSlot() {
            return timeSlot;
        }

        public void setTimeSlot(String timeSlot) {
            this.timeSlot = timeSlot;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getAssignedWorker() {
            return assignedWorker;
        }

        public void setAssignedWorker(String assignedWorker) {
            this.assignedWorker = assignedWorker;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getWorkerStatus() {
            return workerStatus;
        }

        public void setWorkerStatus(String workerStatus) {
            this.workerStatus = workerStatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public Double getEstimatedPrice() {
            return estimatedPrice;
        }

        public void setEstimatedPrice(Double estimatedPrice) {
            this.estimatedPrice = estimatedPrice;
        }

        public Double getFinalPrice() {
            return finalPrice;
        }

        public void setFinalPrice(Double finalPrice) {
            this.finalPrice = finalPrice;
        }

        public Boolean getIsPriceSet() {
            return isPriceSet;
        }

        public void setIsPriceSet(Boolean isPriceSet) {
            this.isPriceSet = isPriceSet;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getSpecialRequest() {
            return specialRequest;
        }

        public void setSpecialRequest(String specialRequest) {
            this.specialRequest = specialRequest;
        }
    }
}