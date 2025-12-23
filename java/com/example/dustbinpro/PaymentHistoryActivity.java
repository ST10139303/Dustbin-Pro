package com.example.dustbinpro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import com.example.dustbinpro.adapters.PaymentAdapter;
import com.example.dustbinpro.models.PaymentModel;
import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPayments;
    private TextView tvNoPayments, tvTotalReceived, tvTotalTransactions;
    private Button btnRefresh, btnBack;

    private List<PaymentModel> payments = new ArrayList<>();
    private PaymentAdapter paymentAdapter;

    private FirebaseFirestore db;
    private CollectionReference paymentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        initializeViews();
        setupFirebase();
        setupRecyclerView();
        setupClickListeners();

        loadPayments();
    }

    private void initializeViews() {
        recyclerViewPayments = findViewById(R.id.recyclerViewPayments);
        tvNoPayments = findViewById(R.id.tvNoPayments);
        tvTotalReceived = findViewById(R.id.tvTotalReceived);
        tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        paymentsRef = db.collection("payments");
    }

    private void setupRecyclerView() {
        paymentAdapter = new PaymentAdapter(payments);
        recyclerViewPayments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPayments.setAdapter(paymentAdapter);
    }

    private void setupClickListeners() {
        btnRefresh.setOnClickListener(v -> loadPayments());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPayments() {
        Log.d("PAYMENT", "Loading payments from Firestore...");

        paymentsRef.orderBy("PaymentDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        payments.clear();
                        double totalReceived = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PaymentModel payment = new PaymentModel();
                            payment.setId(document.getId());

                            // Map fields exactly as they appear in your ASP.NET backend
                            if (document.contains("CustomerName")) {
                                payment.setCustomerName(document.getString("CustomerName"));
                            } else {
                                payment.setCustomerName("Unknown Customer");
                            }

                            if (document.contains("Description")) {
                                payment.setDescription(document.getString("Description"));
                            } else {
                                payment.setDescription("No description");
                            }

                            // Handle amount conversion (same as your ASP.NET code)
                            if (document.contains("Amount")) {
                                Object amountValue = document.get("Amount");
                                if (amountValue instanceof Double) {
                                    payment.setAmount((Double) amountValue);
                                } else if (amountValue instanceof Integer) {
                                    payment.setAmount(((Integer) amountValue).doubleValue());
                                } else if (amountValue instanceof Long) {
                                    payment.setAmount(((Long) amountValue).doubleValue());
                                } else if (amountValue instanceof String) {
                                    try {
                                        payment.setAmount(Double.parseDouble((String) amountValue));
                                    } catch (NumberFormatException e) {
                                        payment.setAmount(0.0);
                                    }
                                } else {
                                    payment.setAmount(0.0);
                                }
                            } else {
                                payment.setAmount(0.0);
                            }

                            if (document.contains("PaymentMethod")) {
                                payment.setPaymentMethod(document.getString("PaymentMethod"));
                            } else {
                                payment.setPaymentMethod("Unknown");
                            }

                            if (document.contains("Reference")) {
                                payment.setReference(document.getString("Reference"));
                            } else {
                                payment.setReference("");
                            }

                            if (document.getTimestamp("PaymentDate") != null) {
                                payment.setPaymentDate(document.getTimestamp("PaymentDate").toDate());
                            } else {
                                payment.setPaymentDate(new Date());
                            }

                            if (document.contains("Status")) {
                                payment.setStatus(document.getString("Status"));
                            } else {
                                payment.setStatus("pending");
                            }

                            if (document.contains("BookingId")) {
                                payment.setBookingId(document.getString("BookingId"));
                            }

                            if (document.contains("CustomerId")) {
                                payment.setCustomerId(document.getString("CustomerId"));
                            }

                            payments.add(payment);

                            // Calculate total received for completed payments
                            if ("completed".equals(payment.getStatus())) {
                                totalReceived += payment.getAmount();
                            }

                            Log.d("PAYMENT", "Loaded payment: " + payment.getCustomerName() +
                                    " - R" + payment.getAmount() + " - " + payment.getStatus());
                        }

                        updatePaymentUI(totalReceived);

                    } else {
                        Log.e("PAYMENT", "Error loading payments: " + task.getException().getMessage());
                        Toast.makeText(this, "Error loading payments", Toast.LENGTH_SHORT).show();
                        updatePaymentUI(0);
                    }
                });
    }

    private void updatePaymentUI(double totalReceived) {
        // Update statistics
        tvTotalReceived.setText(String.format("R%.2f", totalReceived));
        tvTotalTransactions.setText(String.valueOf(payments.size()));

        // Update list visibility
        if (payments.isEmpty()) {
            tvNoPayments.setVisibility(View.VISIBLE);
            recyclerViewPayments.setVisibility(View.GONE);
        } else {
            tvNoPayments.setVisibility(View.GONE);
            recyclerViewPayments.setVisibility(View.VISIBLE);
            paymentAdapter.notifyDataSetChanged();
        }

        Toast.makeText(this, "Payments loaded: " + payments.size(), Toast.LENGTH_SHORT).show();
    }
}