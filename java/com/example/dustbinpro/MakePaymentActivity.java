package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MakePaymentActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private TextView amountTextView;
    private EditText cardNumberInput, expiryInput, cvvInput;
    private Button payButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupPayButton();

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        amountTextView = findViewById(R.id.amountTextView);
        cardNumberInput = findViewById(R.id.cardNumberInput);
        expiryInput = findViewById(R.id.expiryInput);
        cvvInput = findViewById(R.id.cvvInput);
        payButton = findViewById(R.id.payButton);
        progressBar = findViewById(R.id.progressBar);

        // Set default amount
        amountTextView.setText("R 150.00");
    }

    private void setupPayButton() {
        payButton.setOnClickListener(v -> {
            if (validateForm()) {
                processPayment();
            }
        });
    }

    private boolean validateForm() {
        String cardNumber = cardNumberInput.getText().toString().trim();
        String expiry = expiryInput.getText().toString().trim();
        String cvv = cvvInput.getText().toString().trim();

        if (cardNumber.isEmpty() || cardNumber.length() != 16) {
            cardNumberInput.setError("Valid card number required (16 digits)");
            return false;
        }

        if (expiry.isEmpty() || !expiry.matches("\\d{2}/\\d{2}")) {
            expiryInput.setError("Valid expiry date required (MM/YY)");
            return false;
        }

        if (cvv.isEmpty() || cvv.length() != 3) {
            cvvInput.setError("Valid CVV required (3 digits)");
            return false;
        }

        return true;
    }

    private void processPayment() {
        progressBar.setVisibility(View.VISIBLE);
        payButton.setEnabled(false);

        // Simulate payment processing
        new android.os.Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            payButton.setEnabled(true);

            Toast.makeText(this, "Payment processed successfully!", Toast.LENGTH_LONG).show();

            // Redirect to dashboard
            Intent intent = new Intent(this, CustomerDashboardActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}