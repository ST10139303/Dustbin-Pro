package com.example.dustbinpro;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private Button updateButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        loadUserData();
        setupUpdateButton();

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        updateButton = findViewById(R.id.updateButton);
        progressBar = findViewById(R.id.progressBar);

        // Email should not be editable
        emailInput.setEnabled(false);
    }

    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            emailInput.setText(user.getEmail());

            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            firstNameInput.setText(documentSnapshot.getString("firstName"));
                            lastNameInput.setText(documentSnapshot.getString("lastName"));
                            phoneInput.setText(documentSnapshot.getString("phone"));
                        }
                    });
        }
    }

    private void setupUpdateButton() {
        updateButton.setOnClickListener(v -> {
            if (validateForm()) {
                updateProfile();
            }
        });
    }

    private boolean validateForm() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            return false;
        }

        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            return false;
        }

        return true;
    }

    private void updateProfile() {
        progressBar.setVisibility(View.VISIBLE);
        updateButton.setEnabled(false);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", firstNameInput.getText().toString().trim());
            updates.put("lastName", lastNameInput.getText().toString().trim());
            updates.put("name", firstNameInput.getText().toString().trim() + " " + lastNameInput.getText().toString().trim());
            updates.put("phone", phoneInput.getText().toString().trim());

            db.collection("users").document(user.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        updateButton.setEnabled(true);
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        updateButton.setEnabled(true);
                        Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}