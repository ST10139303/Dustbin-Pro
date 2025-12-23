package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerFirstName, registerLastName, registerEmail, registerPassword, registerPhone;
    private Button registerButton;
    private ProgressBar registerProgress;
    private TextView loginRedirect;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        registerFirstName = findViewById(R.id.registerFirstName);
        registerLastName = findViewById(R.id.registerLastName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPhone = findViewById(R.id.registerPhone);
        registerPassword = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);
        registerProgress = findViewById(R.id.registerProgress);
        loginRedirect = findViewById(R.id.loginRedirect);
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> validateAndRegister());

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void validateAndRegister() {
        String firstName = registerFirstName.getText().toString().trim();
        String lastName = registerLastName.getText().toString().trim();
        String email = registerEmail.getText().toString().trim();
        String phone = registerPhone.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();

        // Reset errors
        registerFirstName.setError(null);
        registerLastName.setError(null);
        registerEmail.setError(null);
        registerPassword.setError(null);

        boolean isValid = true;

        if (firstName.isEmpty()) {
            registerFirstName.setError("First name is required");
            registerFirstName.requestFocus();
            isValid = false;
        }

        if (lastName.isEmpty()) {
            registerLastName.setError("Last name is required");
            registerLastName.requestFocus();
            isValid = false;
        }

        if (email.isEmpty()) {
            registerEmail.setError("Email is required");
            registerEmail.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerEmail.setError("Please enter a valid email");
            registerEmail.requestFocus();
            isValid = false;
        }

        if (password.isEmpty()) {
            registerPassword.setError("Password is required");
            registerPassword.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            registerPassword.setError("Password must be at least 6 characters");
            registerPassword.requestFocus();
            isValid = false;
        }

        if (isValid) {
            performRegistration(firstName, lastName, email, phone, password);
        }
    }

    private void performRegistration(String firstName, String lastName, String email, String phone, String password) {
        registerProgress.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Save user data to Firestore (MATCHING WEBSITE STRUCTURE)
                            saveUserToFirestore(user.getUid(), firstName, lastName, email, phone);
                        }
                    } else {
                        registerProgress.setVisibility(View.GONE);
                        registerButton.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String firstName, String lastName, String email, String phone) {
        // MATCHING WEBSITE DATA STRUCTURE EXACTLY
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("name", firstName + " " + lastName); // Both formats for compatibility
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("role", "customer");
        userData.put("status", "active");
        userData.put("emailVerified", false);
        userData.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Send email verification (like website)
                    sendVerificationEmail(firstName, email);
                })
                .addOnFailureListener(e -> {
                    registerProgress.setVisibility(View.GONE);
                    registerButton.setEnabled(true);
                    Toast.makeText(RegisterActivity.this,
                            "Registration complete! But failed to save user data: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    // Still try to send verification
                    sendVerificationEmail(firstName, email);
                });
    }

    private void sendVerificationEmail(String firstName, String email) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        registerProgress.setVisibility(View.GONE);
                        registerButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            showSuccessDialog(firstName, email);
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Registration successful but verification email failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            showSuccessDialog(firstName, email);
                        }
                    });
        }
    }

    private void showSuccessDialog(String firstName, String email) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Registration Successful!")
                .setMessage("Welcome to Rock Waste Management, " + firstName + "!\n\n" +
                        "A verification email has been sent to:\n" + email + "\n\n" +
                        "Please verify your email to access all features.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Sign out and redirect to login (like website behavior)
                    auth.signOut();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}