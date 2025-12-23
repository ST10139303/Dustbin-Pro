package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView forgotPassword, resendVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if user is already logged in (like website behavior)
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Check if email is verified and redirect based on role
            checkUserRoleAndRedirect(currentUser.getUid(), currentUser.isEmailVerified());
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        loginEmail = findViewById(R.id.emailField);
        loginPassword = findViewById(R.id.passwordField);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        progressBar = findViewById(R.id.progressBar);
        forgotPassword = findViewById(R.id.forgotPassword);
        resendVerification = findViewById(R.id.resendVerification);
    }

    private void setupClickListeners() {
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerRedirect = findViewById(R.id.registerRedirect);

        loginButton.setOnClickListener(v -> validateAndLogin());

        registerRedirect.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        forgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        resendVerification.setOnClickListener(v -> showResendVerificationDialog());
    }

    private void validateAndLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        // Reset errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        }

        if (isValid) {
            performLogin(email, password);
        }
    }

    private void performLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // ALLOW LOGIN EVEN IF EMAIL NOT VERIFIED (like website)
                            checkUserRoleAndRedirect(user.getUid(), user.isEmailVerified());

                            if (!user.isEmailVerified()) {
                                // Show warning but allow access (like website)
                                Toast.makeText(LoginActivity.this,
                                        "Please verify your email to access all features",
                                        Toast.LENGTH_LONG).show();
                                resendVerification.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        // Enhanced error messages (matching website)
                        String errorMessage = "Login failed. Please try again.";
                        if (task.getException() != null) {
                            String errorCode = task.getException().getMessage();
                            if (errorCode.contains("invalid-credential") || errorCode.contains("wrong-password")) {
                                errorMessage = "Invalid email or password";
                            } else if (errorCode.contains("user-not-found")) {
                                errorMessage = "No account found with this email";
                            } else if (errorCode.contains("too-many-requests")) {
                                errorMessage = "Too many attempts. Please try again later";
                            }
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserRoleAndRedirect(String userId, boolean emailVerified) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        String status = documentSnapshot.getString("status");

                        // Check if account is disabled
                        if ("disabled".equals(status)) {
                            Toast.makeText(this, "Account has been disabled. Contact support.", Toast.LENGTH_LONG).show();
                            auth.signOut();
                            return;
                        }


                        Intent intent;
                        switch (role) {
                            case "admin":
                                intent = new Intent(this, AdminDashboardActivity.class);
                                break;
                            case "worker":
                                intent = new Intent(this, WorkerDashboardActivity.class);
                                break;
                            case "customer":
                            default:
                                intent = new Intent(this, CustomerDashboardActivity.class);
                                break;
                        }

                        // Add email verification status
                        intent.putExtra("emailVerified", emailVerified);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                });
    }
    private void showForgotPasswordDialog() {
        // Implementation remains the same...
    }

    private void showResendVerificationDialog() {
        // Implementation remains the same...
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide resend verification button when activity resumes
        if (resendVerification != null) {
            resendVerification.setVisibility(View.GONE);
        }
    }
}