package com.example.dustbinpro.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.NotificationsActivity;
import com.example.dustbinpro.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<NotificationsActivity.Notification> notifications;
    private SimpleDateFormat dateFormat;

    public NotificationsAdapter(List<NotificationsActivity.Notification> notifications) {
        this.notifications = notifications;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationsActivity.Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView title, message, timestamp, typeBadge;
        private View notificationDot, typeIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            timestamp = itemView.findViewById(R.id.notificationTimestamp);
            typeBadge = itemView.findViewById(R.id.typeBadge);
            notificationDot = itemView.findViewById(R.id.notificationDot);
            typeIndicator = itemView.findViewById(R.id.typeIndicator);
        }

        public void bind(NotificationsActivity.Notification notification) {
            // Set notification data
            title.setText(notification.getTitle() != null ? notification.getTitle() : "Notification");
            message.setText(notification.getMessage() != null ? notification.getMessage() : "No message content");

            // Format timestamp
            if (notification.getCreatedAt() != null) {
                timestamp.setText(dateFormat.format(notification.getCreatedAt()));
            } else {
                timestamp.setText("Just now");
            }

            // Set type badge
            String type = notification.getType() != null ? notification.getType() : "info";
            typeBadge.setText(getTypeDisplayName(type));

            // Set styles based on type and read status
            setNotificationStyle(type, notification.isRead());

            // Setup click listener
            itemView.setOnClickListener(v -> {
                markAsRead(notification);
            });
        }

        private String getTypeDisplayName(String type) {
            switch (type.toLowerCase()) {
                case "booking":
                    return "Booking";
                case "payment":
                    return "Payment";
                case "service":
                    return "Service";
                case "system":
                    return "System";
                case "promotion":
                    return "Promotion";
                case "info":
                default:
                    return "Information";
            }
        }

        private void setNotificationStyle(String type, boolean isRead) {
            int typeColor = getTypeColor(type);
            int backgroundColor = isRead ?
                    ContextCompat.getColor(itemView.getContext(), android.R.color.background_light) :
                    ContextCompat.getColor(itemView.getContext(), R.color.unread_notification_bg);

            // Set background based on read status
            itemView.setBackgroundColor(backgroundColor);

            // Set type indicator color
            typeIndicator.setBackgroundColor(typeColor);

            // Set type badge color
            GradientDrawable badgeBackground = new GradientDrawable();
            badgeBackground.setColor(typeColor);
            badgeBackground.setCornerRadius(16f);
            typeBadge.setBackground(badgeBackground);
            typeBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));

            // Show/hide notification dot based on read status
            notificationDot.setVisibility(isRead ? View.GONE : View.VISIBLE);

            // Set text appearance based on read status
            if (isRead) {
                title.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                title.setAlpha(0.7f);
            } else {
                title.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
                title.setAlpha(1.0f);
                title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
            }
        }

        private int getTypeColor(String type) {
            switch (type.toLowerCase()) {
                case "booking":
                    return ContextCompat.getColor(itemView.getContext(), R.color.blue);
                case "payment":
                    return ContextCompat.getColor(itemView.getContext(), R.color.green);
                case "service":
                    return ContextCompat.getColor(itemView.getContext(), R.color.teal_700);
                case "system":
                    return ContextCompat.getColor(itemView.getContext(), R.color.orange);
                case "promotion":
                    return ContextCompat.getColor(itemView.getContext(), R.color.purple);
                case "info":
                default:
                    return ContextCompat.getColor(itemView.getContext(), R.color.gray);
            }
        }

        private void markAsRead(NotificationsActivity.Notification notification) {
            // Here you would update the notification in Firestore as read
            // For now, we'll just update the UI
            if (!notification.isRead()) {
                notification.setRead(true);
                setNotificationStyle(notification.getType(), true);

                // Show a quick toast
                android.widget.Toast.makeText(itemView.getContext(), "Marked as read", android.widget.Toast.LENGTH_SHORT).show();
            }

            // You could also show a detailed view of the notification
            showNotificationDetails(notification);
        }

        private void showNotificationDetails(NotificationsActivity.Notification notification) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
            builder.setTitle(notification.getTitle())
                    .setMessage(notification.getMessage() + "\n\n" +
                            "Type: " + getTypeDisplayName(notification.getType()) + "\n" +
                            "Time: " + (notification.getCreatedAt() != null ?
                            dateFormat.format(notification.getCreatedAt()) : "Unknown"))
                    .setPositiveButton("OK", null)
                    .setNeutralButton("Mark Unread", (dialog, which) -> {
                        notification.setRead(false);
                        setNotificationStyle(notification.getType(), false);
                    })
                    .show();
        }
    }
}