package com.example.dustbinpro.adapters;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dustbinpro.R;
import com.example.dustbinpro.models.AssignmentViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private final List<AssignmentViewModel> assignments;
    private final AssignmentActionListener listener;
    private final SimpleDateFormat dateFormat;

    public interface AssignmentActionListener {
        void onAssignmentAction(String action, AssignmentViewModel assignment);
    }

    public AssignmentAdapter(List<AssignmentViewModel> assignments, AssignmentActionListener listener) {
        this.assignments = assignments;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignmentViewModel assignment = assignments.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public void updateData(List<AssignmentViewModel> newAssignments) {
        assignments.clear();
        assignments.addAll(newAssignments);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkerName, tvBookingAddress, tvDate, tvWorkerStatus, tvBookingStatus;
        Button btnComplete, btnDelete;
        ImageView statusIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            tvWorkerName = itemView.findViewById(R.id.tvWorkerName);
            tvBookingAddress = itemView.findViewById(R.id.tvBookingAddress);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvWorkerStatus = itemView.findViewById(R.id.tvWorkerStatus);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }

        void bind(AssignmentViewModel assignment) {
            // Set basic information
            tvWorkerName.setText(assignment.getWorkerName() != null ?
                    assignment.getWorkerName() : "Unknown Worker");
            tvBookingAddress.setText(assignment.getBookingAddress() != null ?
                    assignment.getBookingAddress() : "Unknown Address");

            // Format date
            if (assignment.getDate() != null) {
                tvDate.setText(dateFormat.format(assignment.getDate()));
            } else {
                tvDate.setText("Date not set");
            }

            // Set status views and colors
            setStatusViews(assignment);

            // Set status indicator color
            int statusColor = getStatusColor(assignment.getWorkerStatus());
            statusIndicator.setBackgroundColor(statusColor);

            // Handle complete button visibility
            setupCompleteButton(assignment);

            // Setup delete button
            btnDelete.setOnClickListener(v ->
                    listener.onAssignmentAction("DELETE", assignment));
        }

        private void setStatusViews(AssignmentViewModel assignment) {
            // Worker Status
            String workerStatus = assignment.getWorkerStatus();
            if (workerStatus == null) workerStatus = "Unknown";
            tvWorkerStatus.setText(workerStatus);

            int workerBgColor = getStatusColor(workerStatus);
            int workerTextColor = getStatusTextColor(workerStatus);
            tvWorkerStatus.setBackgroundColor(workerBgColor);
            tvWorkerStatus.setTextColor(workerTextColor);

            // Booking Status
            String bookingStatus = assignment.getBookingStatus();
            if (bookingStatus == null) bookingStatus = "Unknown";
            tvBookingStatus.setText(bookingStatus);

            int bookingBgColor = getStatusColor(bookingStatus);
            int bookingTextColor = getStatusTextColor(bookingStatus);
            tvBookingStatus.setBackgroundColor(bookingBgColor);
            tvBookingStatus.setTextColor(bookingTextColor);
        }

        private void setupCompleteButton(AssignmentViewModel assignment) {
            // Show complete button only for completed worker status AND not fully completed
            boolean shouldShowComplete = "Completed".equalsIgnoreCase(assignment.getWorkerStatus())
                    && (assignment.getIsFullyCompleted() == null || !assignment.getIsFullyCompleted());

            if (shouldShowComplete) {
                btnComplete.setVisibility(View.VISIBLE);
                btnComplete.setOnClickListener(v ->
                        listener.onAssignmentAction("COMPLETE", assignment));
            } else {
                btnComplete.setVisibility(View.GONE);
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return 0xFF6C757D; // Gray

            switch (status.toLowerCase()) {
                case "pending":
                    return 0xFFFFC107; // Amber
                case "in progress":
                case "attending":
                    return 0xFF17A2B8; // Teal
                case "completed":
                    return 0xFF28A745; // Green
                case "cancelled":
                    return 0xFFDC3545; // Red
                case "assigned":
                    return 0xFF6F42C1; // Purple
                default:
                    return 0xFF6C757D; // Gray
            }
        }

        private int getStatusTextColor(String status) {
            if (status == null) return Color.WHITE;

            switch (status.toLowerCase()) {
                case "pending":
                    return Color.BLACK; // Dark text for light background
                default:
                    return Color.WHITE;
            }
        }
    }
}