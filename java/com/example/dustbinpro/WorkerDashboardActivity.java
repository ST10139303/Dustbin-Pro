package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.adapters.WorkerTasksAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import java.util.*;

public class WorkerDashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String workerId;

    private TextView welcomeText, activeTasksCount;
    private RecyclerView tasksRecyclerView;
    private ProgressBar progressBar;
    private LinearLayout statsLayout;

    // Stats TextViews
    private TextView pendingCount, attendingCount, completedCount, totalCount;

    private List<Booking> activeBookings = new ArrayList<>();
    private WorkerTasksAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_dashboard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        workerId = auth.getCurrentUser().getUid();

        initializeViews();
        setupRecyclerView();
        loadWorkerData();
        loadActiveTasks();

        // Auto-refresh every 2 minutes
        new android.os.Handler().postDelayed(() -> loadActiveTasks(), 120000);
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        activeTasksCount = findViewById(R.id.activeTasksCount);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        statsLayout = findViewById(R.id.statsLayout);

        pendingCount = findViewById(R.id.pendingCount);
        attendingCount = findViewById(R.id.attendingCount);
        completedCount = findViewById(R.id.completedCount);
        totalCount = findViewById(R.id.totalCount);

        // Setup logout button
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> logout());

        // Setup refresh button
        Button refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(v -> loadActiveTasks());

        // Setup completed tasks button
        Button completedTasksBtn = findViewById(R.id.completedTasksBtn);
        completedTasksBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, WorkerCompletedTasksActivity.class));
        });
    }

    private void setupRecyclerView() {
        tasksAdapter = new WorkerTasksAdapter(activeBookings, this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(tasksAdapter);
    }

    private void loadWorkerData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(workerId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String email = documentSnapshot.getString("email");
                            welcomeText.setText("Welcome back, " + (firstName != null ? firstName : "Worker"));
                        }
                    });
        }
    }

    private void loadActiveTasks() {
        progressBar.setVisibility(View.VISIBLE);
        statsLayout.setVisibility(View.GONE);

        db.collection("assignments")
                .whereEqualTo("workerId", workerId)
                .whereIn("workerStatus", Arrays.asList("Pending", "Attending", "Completed"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    activeBookings.clear();
                    int pending = 0, attending = 0, completed = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Booking booking = document.toObject(Booking.class);
                        booking.setAssignmentId(document.getId());
                        activeBookings.add(booking);

                        // Count by status
                        switch (booking.getWorkerStatus()) {
                            case "Pending":
                                pending++;
                                break;
                            case "Attending":
                                attending++;
                                break;
                            case "Completed":
                                completed++;
                                break;
                        }
                    }

                    updateStats(pending, attending, completed, activeBookings.size());
                    tasksAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    statsLayout.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateStats(int pending, int attending, int completed, int total) {
        pendingCount.setText(String.valueOf(pending));
        attendingCount.setText(String.valueOf(attending));
        completedCount.setText(String.valueOf(completed));
        totalCount.setText(String.valueOf(total));
        activeTasksCount.setText(total + " Active Tasks");
    }

    public void updateTaskStatus(String assignmentId, String newStatus) {
        db.collection("assignments").document(assignmentId)
                .update("workerStatus", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    loadActiveTasks(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // Booking model class
    public static class Booking {
        private String assignmentId;
        private String bookingId;
        private String customerName;
        private String address;
        private String serviceType;
        private String bookingDate;
        private String timeSlot;
        private String workerStatus;

        // Getters and setters
        public String getAssignmentId() { return assignmentId; }
        public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getServiceType() { return serviceType; }
        public void setServiceType(String serviceType) { this.serviceType = serviceType; }

        public String getBookingDate() { return bookingDate; }
        public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

        public String getWorkerStatus() { return workerStatus; }
        public void setWorkerStatus(String workerStatus) { this.workerStatus = workerStatus; }
    }
}