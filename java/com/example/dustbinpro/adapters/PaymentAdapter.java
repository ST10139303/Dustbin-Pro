package com.example.dustbinpro.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dustbinpro.R;
import com.example.dustbinpro.models.PaymentModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private final List<PaymentModel> payments;
    private final SimpleDateFormat dateFormat;

    public PaymentAdapter(List<PaymentModel> payments) {
        this.payments = payments;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        Log.d("PAYMENT_ADAPTER", "Adapter created with " + payments.size() + " payments");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_payment, parent, false);
            Log.d("PAYMENT_ADAPTER", "ViewHolder created successfully");
            return new ViewHolder(view);
        } catch (Exception e) {
            Log.e("PAYMENT_ADAPTER", "Error creating ViewHolder: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create ViewHolder", e);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            PaymentModel payment = payments.get(position);
            Log.d("PAYMENT_ADAPTER", "Binding payment at position " + position + ": " + payment.getCustomerName());
            holder.bind(payment);
        } catch (Exception e) {
            Log.e("PAYMENT_ADAPTER", "Error binding payment at position " + position + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvDescription, tvAmount, tvPaymentMethod, tvReference, tvDate, tvStatus;
        CardView paymentCard; // CHANGED FROM LinearLayout TO CardView

        ViewHolder(View itemView) {
            super(itemView);
            try {
                tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
                tvReference = itemView.findViewById(R.id.tvReference);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                paymentCard = itemView.findViewById(R.id.paymentCard); // This is a CardView

                Log.d("PAYMENT_ADAPTER", "ViewHolder views initialized");
            } catch (Exception e) {
                Log.e("PAYMENT_ADAPTER", "Error initializing ViewHolder views: " + e.getMessage(), e);
            }
        }

        void bind(PaymentModel payment) {
            try {
                if (payment == null) {
                    Log.e("PAYMENT_ADAPTER", "Payment is null in bind method");
                    return;
                }

                // Set basic payment info
                setTextSafe(tvCustomerName, payment.getCustomerName());
                setTextSafe(tvDescription, payment.getDescription());
                setTextSafe(tvAmount, String.format("R%.2f", payment.getAmount()));
                setTextSafe(tvPaymentMethod, payment.getPaymentMethod());
                setTextSafe(tvReference, payment.getReference());

                // Set date
                if (payment.getPaymentDate() != null) {
                    setTextSafe(tvDate, dateFormat.format(payment.getPaymentDate()));
                } else {
                    setTextSafe(tvDate, "Unknown date");
                }

                // Set status
                String status = payment.getStatus() != null ? payment.getStatus().toUpperCase() : "UNKNOWN";
                setTextSafe(tvStatus, status);

                // Set status color
                int statusColor = getStatusColor(payment.getStatus());
                if (tvStatus != null) {
                    tvStatus.setBackgroundColor(statusColor);
                }

                // Show booking ID if available
                if (payment.getBookingId() != null && !payment.getBookingId().isEmpty()) {
                    String description = payment.getDescription() + "\nBooking: " + payment.getBookingId();
                    setTextSafe(tvDescription, description);
                }

                Log.d("PAYMENT_ADAPTER", "Payment bound successfully: " + payment.getCustomerName());

            } catch (Exception e) {
                Log.e("PAYMENT_ADAPTER", "Error binding payment data: " + e.getMessage(), e);
            }
        }

        private void setTextSafe(TextView textView, String text) {
            if (textView != null) {
                textView.setText(text != null ? text : "");
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return 0xFF6C757D; // Gray

            switch (status.toLowerCase()) {
                case "completed":
                    return 0xFF28A745; // Green
                case "pending":
                    return 0xFFFFC107; // Amber
                case "failed":
                    return 0xFFDC3545; // Red
                default:
                    return 0xFF6C757D; // Gray
            }
        }
    }
}