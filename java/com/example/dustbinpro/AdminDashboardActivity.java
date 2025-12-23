package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Button viewCustomersButton, viewPaymentsButton, assignWorkersButton, adminLogoutButton, viewBookingsButton;

    // Stats TextViews
    private TextView customersCount, bookingsCount, paymentsCount, workersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize buttons
        viewCustomersButton = findViewById(R.id.viewCustomersButton);
        viewPaymentsButton = findViewById(R.id.viewPaymentsButton);
        assignWorkersButton = findViewById(R.id.assignWorkersButton);
        adminLogoutButton = findViewById(R.id.adminLogoutButton);
        viewBookingsButton = findViewById(R.id.viewBookingsButton);

        // Initialize stats TextViews
        customersCount = findViewById(R.id.customersCount);
        bookingsCount = findViewById(R.id.bookingsCount);
        paymentsCount = findViewById(R.id.paymentsCount);
        workersCount = findViewById(R.id.workersCount);

        // Check if user is authenticated
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        // Get current user ID
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Check user role
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("admin".equals(role)) {
                            setupAdminFeatures();
                            loadDashboardStats(); // Load the actual counts
                        } else {
                            Toast.makeText(this, "Access denied. Admins only.", Toast.LENGTH_SHORT).show();
                            redirectToLogin();
                        }
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user data.", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                });
    }

    private void loadDashboardStats() {
        // Load customers count
        db.collection("users")
                .whereEqualTo("role", "customer")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int customerCount = queryDocumentSnapshots.size();
                    customersCount.setText(String.valueOf(customerCount));
                })
                .addOnFailureListener(e -> {
                    customersCount.setText("0");
                });

        // Load bookings count
        db.collection("bookings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int bookingCount = queryDocumentSnapshots.size();
                    bookingsCount.setText(String.valueOf(bookingCount));
                })
                .addOnFailureListener(e -> {
                    bookingsCount.setText("0");
                });

        // Load payments count and total amount
        db.collection("payments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int paymentCount = queryDocumentSnapshots.size();
                    double totalAmount = 0;

                    for (var document : queryDocumentSnapshots) {
                        Double amount = document.getDouble("amount");
                        if (amount != null) {
                            totalAmount += amount;
                        }
                    }

                    paymentsCount.setText("R " + String.format("%.2f", totalAmount));
                })
                .addOnFailureListener(e -> {
                    paymentsCount.setText("R 0");
                });

        // Load workers count
        db.collection("users")
                .whereEqualTo("role", "worker")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int workerCount = queryDocumentSnapshots.size();
                    workersCount.setText(String.valueOf(workerCount));
                })
                .addOnFailureListener(e -> {
                    workersCount.setText("0");
                });
    }

    private void redirectToLogin() {
        auth.signOut();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupAdminFeatures() {
        // Navigate to Customer List
        viewCustomersButton.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, CustomerListActivity.class)));

        // Navigate to Bookings List
        viewBookingsButton.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, BookingListActivity.class)));

        // Navigate to Payment History
        viewPaymentsButton.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, PaymentHistoryActivity.class)));

        // Navigate to Assign Workers
        assignWorkersButton.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, AssignWorkersActivity.class)));

        // Logout
        adminLogoutButton.setOnClickListener(v -> redirectToLogin());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh stats when returning to dashboard
        if (auth.getCurrentUser() != null) {
            loadDashboardStats();
        }
    }
}