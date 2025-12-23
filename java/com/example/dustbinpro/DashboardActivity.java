package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;



public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button bookCleaningButton, makePaymentButton, paymentHistoryButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Initialize customer buttons
        bookCleaningButton = findViewById(R.id.bookCleaningButton);
        makePaymentButton = findViewById(R.id.makePaymentButton);
        paymentHistoryButton = findViewById(R.id.paymentHistoryButton);
        logoutButton = findViewById(R.id.logoutButton);

        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("admin".equalsIgnoreCase(role)) {
                                // Redirect admins immediately to Admin Dashboard
                                startActivity(new Intent(DashboardActivity.this, AdminDashboardActivity.class));
                                finish();
                            } else {
                                // Normal customer: setup their buttons
                                setupCustomerFeatures();
                            }
                        } else {
                            Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            Toast.makeText(this, "No logged in user.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void setupCustomerFeatures() {
        // Book Cleaning
        bookCleaningButton.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, BookingActivity.class)));
        // Make a Payment
        makePaymentButton.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, PaymentActivity.class)));

        // View Payment History
        paymentHistoryButton.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, PaymentHistoryActivity.class)));

        // Logout
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
