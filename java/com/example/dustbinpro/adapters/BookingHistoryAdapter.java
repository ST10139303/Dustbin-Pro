package com.example.dustbinpro.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.BookingHistoryActivity;
import com.example.dustbinpro.R;
import com.example.dustbinpro.SupportActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private List<BookingHistoryActivity.Booking> bookings;
    private SimpleDateFormat dateFormat;

    public BookingHistoryAdapter(List<BookingHistoryActivity.Booking> bookings) {
        this.bookings = bookings;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingHistoryActivity.Booking booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView bookingId, serviceType, status, address, timeSlot, bookingDate, createdAt;
        private View statusIndicator;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            bookingId = itemView.findViewById(R.id.bookingId);
            serviceType = itemView.findViewById(R.id.serviceType);
            status = itemView.findViewById(R.id.status);
            address = itemView.findViewById(R.id.address);
            timeSlot = itemView.findViewById(R.id.timeSlot);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            createdAt = itemView.findViewById(R.id.createdAt);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }

        public void bind(BookingHistoryActivity.Booking booking) {
            // Set booking data
            if (booking.getBookingId() != null && booking.getBookingId().length() > 8) {
                bookingId.setText("Booking #" + booking.getBookingId().substring(booking.getBookingId().length() - 8));
            } else {
                bookingId.setText("Booking #" + (booking.getBookingId() != null ? booking.getBookingId() : ""));
            }

            serviceType.setText(booking.getServiceType() != null ? booking.getServiceType() : "Unknown Service");
            status.setText(booking.getStatus() != null ? booking.getStatus() : "Pending");
            address.setText(booking.getAddress() != null ? booking.getAddress() : "Address not specified");
            timeSlot.setText(booking.getTimeSlot() != null ? booking.getTimeSlot() : "Time not specified");

            // Format dates
            if (booking.getBookingDate() != null) {
                bookingDate.setText(dateFormat.format(booking.getBookingDate()));
            } else {
                bookingDate.setText("Not scheduled");
            }

            if (booking.getCreatedAt() != null) {
                createdAt.setText("Booked: " + dateFormat.format(booking.getCreatedAt()));
            } else {
                createdAt.setText("Booked: Unknown");
            }

            // Set status color and indicator
            setStatusStyle(booking.getStatus() != null ? booking.getStatus() : "Pending");

            // Setup action buttons
            setupActionButtons(booking);
        }

        private void setStatusStyle(String statusText) {
            int backgroundColor;
            int textColor;

            switch (statusText) {
                case "Completed":
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.green);
                    textColor = ContextCompat.getColor(itemView.getContext(), android.R.color.white);
                    break;
                case "In Progress":
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.blue);
                    textColor = ContextCompat.getColor(itemView.getContext(), android.R.color.white);
                    break;
                case "Cancelled":
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.red);
                    textColor = ContextCompat.getColor(itemView.getContext(), android.R.color.white);
                    break;
                case "Pending":
                default:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.orange);
                    textColor = ContextCompat.getColor(itemView.getContext(), android.R.color.black);
                    break;
            }

            // Set status background and text color for TextView
            status.setBackgroundColor(backgroundColor);
            status.setTextColor(textColor);

            // Set status indicator color for View
            statusIndicator.setBackgroundColor(backgroundColor);

            // Make the status TextView have rounded corners
            GradientDrawable statusBackground = new GradientDrawable();
            statusBackground.setColor(backgroundColor);
            statusBackground.setCornerRadius(32f); // 32dp corner radius
            status.setBackground(statusBackground);
        }

        private void setupActionButtons(BookingHistoryActivity.Booking booking) {
            TextView viewDetailsBtn = itemView.findViewById(R.id.viewDetailsBtn);
            TextView contactSupportBtn = itemView.findViewById(R.id.contactSupportBtn);

            viewDetailsBtn.setOnClickListener(v -> {
                // Show booking details
                showBookingDetails(booking);
            });

            contactSupportBtn.setOnClickListener(v -> {
                // Open support activity
                openSupport(booking);
            });
        }

        private void showBookingDetails(BookingHistoryActivity.Booking booking) {
            // Create a dialog or start a new activity to show booking details
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Booking Details")
                    .setMessage(
                            "Booking ID: " + booking.getBookingId() + "\n\n" +
                                    "Service: " + booking.getServiceType() + "\n\n" +
                                    "Status: " + booking.getStatus() + "\n\n" +
                                    "Address: " + booking.getAddress() + "\n\n" +
                                    "Time Slot: " + booking.getTimeSlot() + "\n\n" +
                                    "Booking Date: " + (booking.getBookingDate() != null ?
                                    dateFormat.format(booking.getBookingDate()) : "Not scheduled") + "\n\n" +
                                    "Created: " + (booking.getCreatedAt() != null ?
                                    dateFormat.format(booking.getCreatedAt()) : "Unknown")
                    )
                    .setPositiveButton("OK", null)
                    .show();
        }

        private void openSupport(BookingHistoryActivity.Booking booking) {
            // Start support activity with booking reference
            android.content.Intent intent = new android.content.Intent(itemView.getContext(), SupportActivity.class);
            itemView.getContext().startActivity(intent);
        }
    }
}