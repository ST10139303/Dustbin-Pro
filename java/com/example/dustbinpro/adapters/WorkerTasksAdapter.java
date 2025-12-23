package com.example.dustbinpro.adapters;



import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.R;
import com.example.dustbinpro.WorkerDashboardActivity;

import java.util.List;

public class WorkerTasksAdapter extends RecyclerView.Adapter<WorkerTasksAdapter.TaskViewHolder> {

    private List<WorkerDashboardActivity.Booking> tasks;
    private WorkerDashboardActivity activity;

    public WorkerTasksAdapter(List<WorkerDashboardActivity.Booking> tasks, WorkerDashboardActivity activity) {
        this.tasks = tasks;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_worker_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        WorkerDashboardActivity.Booking task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName, address, serviceType, bookingDate, timeSlot, statusBadge;
        private Spinner statusSpinner;
        private Button updateStatusBtn;
        private LinearLayout urgentBadge;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customerName);
            address = itemView.findViewById(R.id.address);
            serviceType = itemView.findViewById(R.id.serviceType);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            timeSlot = itemView.findViewById(R.id.timeSlot);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            statusSpinner = itemView.findViewById(R.id.statusSpinner);
            updateStatusBtn = itemView.findViewById(R.id.updateStatusBtn);
            urgentBadge = itemView.findViewById(R.id.urgentBadge);
        }

        public void bind(WorkerDashboardActivity.Booking task) {
            customerName.setText(task.getCustomerName());
            address.setText(task.getAddress());
            serviceType.setText(task.getServiceType() + " Service");
            bookingDate.setText(task.getBookingDate());
            timeSlot.setText(task.getTimeSlot());

            // Set status badge
            setStatusBadge(task.getWorkerStatus());

            // Set spinner selection
            setSpinnerSelection(task.getWorkerStatus());

            // Show urgent badge if needed (you can implement date comparison logic)
            urgentBadge.setVisibility(View.GONE); // Implement your urgent logic

            updateStatusBtn.setOnClickListener(v -> {
                String newStatus = statusSpinner.getSelectedItem().toString();
                activity.updateTaskStatus(task.getAssignmentId(), newStatus);
            });

            // Setup action buttons
            setupActionButtons(task);
        }

        private void setStatusBadge(String status) {
            switch (status) {
                case "Pending":
                    statusBadge.setText("🕒 " + status);
                    statusBadge.setBackgroundResource(R.drawable.badge_pending);
                    break;
                case "Attending":
                    statusBadge.setText("🚶 " + status);
                    statusBadge.setBackgroundResource(R.drawable.badge_attending);
                    break;
                case "Completed":
                    statusBadge.setText("✅ " + status);
                    statusBadge.setBackgroundResource(R.drawable.badge_completed);
                    break;
            }
        }

        private void setSpinnerSelection(String status) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    itemView.getContext(),
                    R.array.worker_status_options,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(adapter);

            int position = 0;
            switch (status) {
                case "Pending": position = 0; break;
                case "Attending": position = 1; break;
                case "Completed": position = 2; break;
            }
            statusSpinner.setSelection(position);
        }

        private void setupActionButtons(WorkerDashboardActivity.Booking task) {
            Button directionsBtn = itemView.findViewById(R.id.directionsBtn);
            Button contactBtn = itemView.findViewById(R.id.contactBtn);
            Button reportBtn = itemView.findViewById(R.id.reportBtn);

            directionsBtn.setOnClickListener(v -> {
                // Open Google Maps with address
                String uri = "http://maps.google.com/maps?daddr=" + task.getAddress();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri));
                itemView.getContext().startActivity(intent);
            });

            contactBtn.setOnClickListener(v -> {
                Toast.makeText(itemView.getContext(),
                        "Contact " + task.getCustomerName(),
                        Toast.LENGTH_SHORT).show();
            });

            reportBtn.setOnClickListener(v -> {
                // Show report issue dialog
                showReportDialog(task.getBookingId());
            });
        }

        private void showReportDialog(String bookingId) {
            // Implement report issue dialog
            Toast.makeText(itemView.getContext(),
                    "Report issue for booking " + bookingId,
                    Toast.LENGTH_SHORT).show();
        }
    }
}