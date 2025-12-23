package com.example.dustbinpro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView amountDueText;
    private Button payNowButton;
    private double amountDue = 0.00; // Default to zero until fetched

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        amountDueText = findViewById(R.id.amountDueText);
        payNowButton = findViewById(R.id.payNowButton);

        loadServicePrice(); // Load the price dynamically
    }

    private void loadServicePrice() {
        // Example: assuming you have a "services" collection and document with ID "dustbin_cleaning"
        String serviceId = "dustbin_cleaning"; // or pass this dynamically if needed

        db.collection("services").document(serviceId)
                .get()
                .addOnSuccessListener(this::onServicePriceLoaded)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load service price.", Toast.LENGTH_SHORT).show();
                    amountDueText.setText("Amount Due: R 0.00"); // fallback
                });
    }

    private void onServicePriceLoaded(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists() && documentSnapshot.getDouble("price") != null) {
            amountDue = documentSnapshot.getDouble("price");
            amountDueText.setText(String.format("Amount Due: R %.2f", amountDue));

            payNowButton.setOnClickListener(v -> processPayment(amountDue));
        } else {
            Toast.makeText(this, "Service price not found.", Toast.LENGTH_SHORT).show();
            amountDueText.setText("Amount Due: R 0.00");
        }
    }

    private void processPayment(double amount) {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Map<String, Object> payment = new HashMap<>();
        payment.put("userId", userId);
        payment.put("amount", amount);
        payment.put("currency", "ZAR");
        payment.put("status", "Paid");
        payment.put("timestamp", System.currentTimeMillis());

        db.collection("payments").add(payment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Payment Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

