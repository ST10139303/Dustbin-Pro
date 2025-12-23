package com.example.dustbinpro.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.BookingListActivity;
import com.example.dustbinpro.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminBookingsAdapter extends RecyclerView.Adapter<AdminBookingsAdapter.BookingViewHolder> {

    private List<BookingListActivity.Booking> bookings;
    private BookingListActivity activity;
    private SimpleDateFormat dateFormat;
    private FirebaseFirestore db;

    public AdminBookingsAdapter(List<BookingListActivity.Booking> bookings, BookingListActivity activity) {
        this.bookings = bookings;
        this.activity = activity;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_admin, parent, false);
        return new BookingViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingListActivity.Booking booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {

        private TextView customerName, bookingId, serviceTypeBadge;
        private TextView address, bookingDate, timeSlot, estimatedPrice, finalPrice;
        private TextView paymentStatus, bookingStatus;
        private LinearLayout actionButtons, priceInputForm;
        private Button approveButton, rejectButton, setPriceButton, deleteButton, savePriceButton;
        private EditText priceInput;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customerName);
            bookingId = itemView.findViewById(R.id.bookingId);
            serviceTypeBadge = itemView.findViewById(R.id.serviceTypeBadge);
            address = itemView.findViewById(R.id.address);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            timeSlot = itemView.findViewById(R.id.timeSlot);
            estimatedPrice = itemView.findViewById(R.id.estimatedPrice);
            finalPrice = itemView.findViewById(R.id.finalPrice);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);

            actionButtons = itemView.findViewById(R.id.actionButtons);
            priceInputForm = itemView.findViewById(R.id.priceInputForm);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            setPriceButton = itemView.findViewById(R.id.setPriceButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            savePriceButton = itemView.findViewById(R.id.savePriceButton);
            priceInput = itemView.findViewById(R.id.priceInput);
        }

        public void bind(BookingListActivity.Booking booking) {

            // ✅ Customer Name
            customerName.setText(booking.getCustomerName() != null
                    ? booking.getCustomerName()
                    : "Unknown Customer");

            // ✅ Booking ID (shortened)
            if (booking.getBookingId() != null && booking.getBookingId().length() > 8) {
                bookingId.setText("#" + booking.getBookingId().substring(booking.getBookingId().length() - 8));
            } else {
                bookingId.setText("#" + (booking.getBookingId() != null ? booking.getBookingId() : ""));
            }

            // ✅ Address
            if (booking.getAddress() != null && !booking.getAddress().isEmpty()) {
                address.setText(booking.getAddress());
            } else {
                address.setText("No address");
            }

            // ✅ Date
            if (booking.getDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                bookingDate.setText(sdf.format(booking.getDate()));
            } else {
                bookingDate.setText("Date not set");
            }

            // ✅ Time Slot
            if (booking.getTimeSlot() != null && !booking.getTimeSlot().isEmpty()) {
                timeSlot.setText(booking.getTimeSlot());
            } else {
                timeSlot.setText("-");
            }

            // ✅ Prices
            estimatedPrice.setText("R " + String.format("%.2f",
                    booking.getEstimatedPrice() != null ? booking.getEstimatedPrice() : 0.0));

            if (booking.getIsPriceSet() != null && booking.getIsPriceSet()
                    && booking.getFinalPrice() != null) {
                finalPrice.setText("R " + String.format("%.2f", booking.getFinalPrice()));
                finalPrice.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
            } else {
                finalPrice.setText("R 0.00");
                finalPrice.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
            }

            // ✅ Service Type
            setServiceTypeStyle(booking.getServiceType());

            // ✅ Status Styles
            String bookingStatusText = booking.getStatus() != null ? booking.getStatus() : "pending";
            String paymentStatusText = booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "pending";
            setStatusStyle(bookingStatusText, paymentStatusText);

            // ✅ Admin Actions
            setupActionButtons(booking);
        }


        private void setServiceTypeStyle(String serviceType) {
            int backgroundColor;
            String displayName;

            switch (serviceType != null ? serviceType.toLowerCase() : "") {
                case "dustbin":
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.orange);
                    displayName = "Dustbin Cleaning";
                    break;
                case "carpet":
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.blue);
                    displayName = "Carpet Cleaning";
                    break;
                case "special":
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.purple);
                    displayName = "Special Request";
                    break;
                default:
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.gray);
                    displayName = "General Service";
                    break;
            }

            GradientDrawable badgeBackground = new GradientDrawable();
            badgeBackground.setColor(backgroundColor);
            badgeBackground.setCornerRadius(16f);
            serviceTypeBadge.setBackground(badgeBackground);
            serviceTypeBadge.setText(displayName);
        }

        private void setStatusStyle(String bookingStatusText, String paymentStatusText) {
            // Booking Status Badge
            int bookingColor = getStatusColor(bookingStatusText);
            GradientDrawable bookingBackground = new GradientDrawable();
            bookingBackground.setColor(bookingColor);
            bookingBackground.setCornerRadius(12f);
            bookingStatus.setBackground(bookingBackground);
            bookingStatus.setText(bookingStatusText.toUpperCase());

            // Payment Status Badge
            int paymentColor = getPaymentStatusColor(paymentStatusText);
            GradientDrawable paymentBackground = new GradientDrawable();
            paymentBackground.setColor(paymentColor);
            paymentBackground.setCornerRadius(12f);
            paymentStatus.setBackground(paymentBackground);
            paymentStatus.setText(paymentStatusText.toUpperCase());
        }

        private int getStatusColor(String status) {
            switch (status != null ? status.toLowerCase() : "") {
                case "approved":
                    return ContextCompat.getColor(itemView.getContext(), R.color.green);
                case "completed":
                    return ContextCompat.getColor(itemView.getContext(), R.color.teal_700);
                case "rejected":
                case "cancelled":
                    return ContextCompat.getColor(itemView.getContext(), R.color.red);
                case "pending":
                    return ContextCompat.getColor(itemView.getContext(), R.color.orange);
                default:
                    return ContextCompat.getColor(itemView.getContext(), R.color.gray);
            }
        }

        private int getPaymentStatusColor(String paymentStatus) {
            switch (paymentStatus != null ? paymentStatus.toLowerCase() : "") {
                case "paid":
                    return ContextCompat.getColor(itemView.getContext(), R.color.green);
                case "failed":
                    return ContextCompat.getColor(itemView.getContext(), R.color.red);
                case "pending":
                    return ContextCompat.getColor(itemView.getContext(), R.color.orange);
                default:
                    return ContextCompat.getColor(itemView.getContext(), R.color.gray);
            }
        }

        private void setupActionButtons(BookingListActivity.Booking booking) {
            // Hide all buttons first
            approveButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
            setPriceButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            priceInputForm.setVisibility(View.GONE);

            String status = booking.getStatus() != null ? booking.getStatus().toLowerCase() : "pending";
            Boolean isPriceSet = booking.getIsPriceSet() != null ? booking.getIsPriceSet() : false;

            if (status.equals("pending")) {
                approveButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);

                approveButton.setOnClickListener(v -> updateBookingStatus(booking.getBookingId(), "approved"));
                rejectButton.setOnClickListener(v -> updateBookingStatus(booking.getBookingId(), "rejected"));

            } else if (status.equals("approved") && !isPriceSet) {
                setPriceButton.setVisibility(View.VISIBLE);
                setPriceButton.setOnClickListener(v -> {
                    priceInputForm.setVisibility(View.VISIBLE);
                    setPriceButton.setVisibility(View.GONE);
                });

                savePriceButton.setOnClickListener(v -> {
                    String priceText = priceInput.getText().toString();
                    if (!priceText.isEmpty()) {
                        try {
                            double price = Double.parseDouble(priceText);
                            setBookingPrice(booking.getBookingId(), price);
                        } catch (NumberFormatException e) {
                            Toast.makeText(itemView.getContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(itemView.getContext(), "Please enter a price", Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (status.equals("rejected") || status.equals("cancelled")) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(v -> deleteBooking(booking.getBookingId()));
            }
        }

        private void updateBookingStatus(String bookingId, String newStatus) {
            db.collection("bookings").document(bookingId)
                    .update("Status", newStatus)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(itemView.getContext(), "Booking " + newStatus, Toast.LENGTH_SHORT).show();
                        activity.loadBookings();
                    })
                    .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        private void setBookingPrice(String bookingId, double price) {
            db.collection("bookings").document(bookingId)
                    .update("FinalPrice", price, "IsPriceSet", true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(itemView.getContext(), "Price set: R " + price, Toast.LENGTH_SHORT).show();
                        activity.loadBookings();
                    })
                    .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        private void deleteBooking(String bookingId) {
            new android.app.AlertDialog.Builder(itemView.getContext())
                    .setTitle("Delete Booking")
                    .setMessage("Are you sure you want to delete this booking?")
                    .setPositiveButton("Delete", (dialog, which) ->
                            db.collection("bookings").document(bookingId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(itemView.getContext(), "Booking deleted", Toast.LENGTH_SHORT).show();
                                        activity.loadBookings();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(itemView.getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}
