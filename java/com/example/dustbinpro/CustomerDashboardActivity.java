package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomerDashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView welcomeText, userEmail;
    private TextView scheduledCount, completedCount, pendingCount, progressCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupNavigation();
        loadUserData();
        loadStats();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        userEmail = findViewById(R.id.userEmail);

        // Stats TextViews - remove these if you removed them from layout
        scheduledCount = findViewById(R.id.scheduledCount);
        completedCount = findViewById(R.id.completedCount);
        pendingCount = findViewById(R.id.pendingCount);


        // Setup logout
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> logout());
    }

    private void setupNavigation() {
        CardView bookCleaningCard = findViewById(R.id.bookCleaningCard);
        CardView bookingHistoryCard = findViewById(R.id.bookingHistoryCard);
        CardView makePaymentCard = findViewById(R.id.makePaymentCard);
        CardView profileCard = findViewById(R.id.profileCard);

        bookCleaningCard.setOnClickListener(v -> {
            startActivity(new Intent(this, BookCleaningActivity.class));
        });

        bookingHistoryCard.setOnClickListener(v -> {
            startActivity(new Intent(this, BookingHistoryActivity.class));
        });

        makePaymentCard.setOnClickListener(v -> {
            startActivity(new Intent(this, MakePaymentActivity.class));
        });

        profileCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // If you removed support and notifications cards from layout, remove these listeners
        // CardView supportCard = findViewById(R.id.supportCard);
        // CardView notificationsCard = findViewById(R.id.notificationsCard);

        // supportCard.setOnClickListener(v -> {
        //     startActivity(new Intent(this, SupportActivity.class));
        // });
        //
        // notificationsCard.setOnClickListener(v -> {
        //     startActivity(new Intent(this, NotificationsActivity.class));
        // });
    }

    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();

            // Set basic user info
            userEmail.setText(email);

            // Try to get user name from Firestore
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            if (firstName != null && !firstName.isEmpty()) {
                                welcomeText.setText("Welcome, " + firstName + "!");
                            } else {
                                welcomeText.setText("Welcome to Your Dashboard");
                            }
                        } else {
                            welcomeText.setText("Welcome to Your Dashboard");
                        }
                    })
                    .addOnFailureListener(e -> {
                        welcomeText.setText("Welcome to Your Dashboard");
                        userEmail.setText(email != null ? email : "user@localhost");
                    });
        } else {
            // User not logged in, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void loadStats() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && scheduledCount != null) { // Check if stats views exist
            // Load booking stats from Firestore
            db.collection("bookings")
                    .whereEqualTo("customerId", user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int scheduled = 0, completed = 0, pending = 0, inProgress = 0;

                        for (var document : queryDocumentSnapshots) {
                            String status = document.getString("status");
                            if (status != null) {
                                switch (status) {
                                    case "Scheduled": scheduled++; break;
                                    case "Completed": completed++; break;
                                    case "Pending": pending++; break;
                                    case "In Progress": inProgress++; break;
                                }
                            }
                        }

                        // Update UI only if views exist
                        if (scheduledCount != null) scheduledCount.setText(String.valueOf(scheduled));
                        if (completedCount != null) completedCount.setText(String.valueOf(completed));
                        if (pendingCount != null) pendingCount.setText(String.valueOf(pending));
                        if (progressCount != null) progressCount.setText(String.valueOf(inProgress));

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load booking stats", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data and stats when returning to dashboard
        loadUserData();
        loadStats();
    }
}