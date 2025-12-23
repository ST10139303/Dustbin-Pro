package com.example.dustbinpro;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class SupportActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private Spinner issueTypeSpinner;
    private EditText descriptionInput;
    private Button submitButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupSpinner();
        setupSubmitButton();

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        issueTypeSpinner = findViewById(R.id.issueTypeSpinner);
        descriptionInput = findViewById(R.id.descriptionInput);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.issue_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issueTypeSpinner.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                submitSupportRequest();
            }
        });
    }

    private boolean validateForm() {
        String description = descriptionInput.getText().toString().trim();

        if (description.isEmpty()) {
            descriptionInput.setError("Please describe your issue");
            descriptionInput.requestFocus();
            return false;
        }

        return true;
    }

    private void submitSupportRequest() {
        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        String userId = auth.getCurrentUser().getUid();
        String issueType = issueTypeSpinner.getSelectedItem().toString();
        String description = descriptionInput.getText().toString().trim();
        String ticketId = "TKT" + System.currentTimeMillis();

        Map<String, Object> supportTicket = new HashMap<>();
        supportTicket.put("ticketId", ticketId);
        supportTicket.put("userId", userId);
        supportTicket.put("issueType", issueType);
        supportTicket.put("description", description);
        supportTicket.put("status", "Open");
        supportTicket.put("createdAt", new Date());

        db.collection("supportTickets").document(ticketId)
                .set(supportTicket)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    submitButton.setEnabled(true);

                    Toast.makeText(this, "Support request submitted! We'll contact you soon.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    Toast.makeText(this, "Failed to submit request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}