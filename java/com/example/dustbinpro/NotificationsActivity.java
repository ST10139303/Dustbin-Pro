package com.example.dustbinpro;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.adapters.NotificationsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class NotificationsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RecyclerView notificationsRecyclerView;
    private ProgressBar progressBar;
    private TextView noNotificationsText;

    private List<Notification> notifications = new ArrayList<>();
    private NotificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupRecyclerView();
        loadNotifications();

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Setup clear all button
        Button clearAllBtn = findViewById(R.id.clearAllBtn);
        clearAllBtn.setOnClickListener(v -> clearAllNotifications());
    }

    private void initializeViews() {
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noNotificationsText = findViewById(R.id.noNotificationsText);
    }

    private void setupRecyclerView() {
        adapter = new NotificationsAdapter(notifications);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsRecyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = auth.getCurrentUser().getUid();



        createSampleNotifications();


        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                notifications.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Notification notification = document.toObject(Notification.class);
                    notifications.add(notification);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (notifications.isEmpty()) {
                    noNotificationsText.setVisibility(View.VISIBLE);
                    notificationsRecyclerView.setVisibility(View.GONE);
                } else {
                    noNotificationsText.setVisibility(View.GONE);
                    notificationsRecyclerView.setVisibility(View.VISIBLE);
                }
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            });

    }

    private void createSampleNotifications() {
        notifications.clear();

        // Add sample notifications
        notifications.add(new Notification(
                "Booking Confirmed",
                "Your dustbin cleaning service has been scheduled for December 25, 2023 at 08:00 AM.",
                "booking",
                new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000), // 2 hours ago
                false
        ));

        notifications.add(new Notification(
                "Payment Received",
                "Payment of R 120.00 for your recent service has been successfully processed.",
                "payment",
                new Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000), // 5 hours ago
                true
        ));

        notifications.add(new Notification(
                "Service Reminder",
                "Don't forget your carpet cleaning appointment tomorrow at 02:00 PM.",
                "service",
                new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), // 1 day ago
                false
        ));

        notifications.add(new Notification(
                "System Update",
                "Our app has been updated with new features. Update now for the best experience!",
                "system",
                new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000), // 2 days ago
                true
        ));

        notifications.add(new Notification(
                "Special Offer",
                "Get 20% off your next carpet cleaning when you book before the end of the month!",
                "promotion",
                new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000), // 3 days ago
                true
        ));

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

        if (notifications.isEmpty()) {
            noNotificationsText.setVisibility(View.VISIBLE);
            notificationsRecyclerView.setVisibility(View.GONE);
        } else {
            noNotificationsText.setVisibility(View.GONE);
            notificationsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void clearAllNotifications() {
        if (notifications.isEmpty()) {
            Toast.makeText(this, "No notifications to clear", Toast.LENGTH_SHORT).show();
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Clear All Notifications")
                .setMessage("Are you sure you want to clear all notifications?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    notifications.clear();
                    adapter.notifyDataSetChanged();
                    noNotificationsText.setVisibility(View.VISIBLE);
                    notificationsRecyclerView.setVisibility(View.GONE);
                    Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Notification model class
    public static class Notification {
        private String title;
        private String message;
        private String type;
        private Date createdAt;
        private boolean read;

        // Default constructor for Firestore
        public Notification() {}

        // Constructor for sample data
        public Notification(String title, String message, String type, Date createdAt, boolean read) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.createdAt = createdAt;
            this.read = read;
        }

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }
}