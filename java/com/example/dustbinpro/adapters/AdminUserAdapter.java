package com.example.dustbinpro.adapters;

import android.content.Context;
import android.os.Build;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private final List<Map<String, Object>> userList;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminUserAdapter(List<Map<String, Object>> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> user = userList.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.nameText.setText((String) user.getOrDefault("name", "Unknown"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.emailText.setText((String) user.getOrDefault("email", "No email"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.roleText.setText("Role: " + user.getOrDefault("DisplayRole", "UNKNOWN"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.typeText.setText("Type: " + user.getOrDefault("UserType", "UNKNOWN"));
        }

        String status = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            status = (String) user.getOrDefault("status", "active");
        }
        holder.statusText.setText("Status: " + status);

        // Change color based on status
        holder.statusText.setTextColor(status.equals("disabled")
                ? context.getColor(android.R.color.holo_red_dark)
                : context.getColor(android.R.color.holo_green_dark));

        holder.enableButton.setVisibility(status.equals("disabled") ? View.VISIBLE : View.GONE);
        holder.disableButton.setVisibility(status.equals("active") ? View.VISIBLE : View.GONE);

        holder.enableButton.setOnClickListener(v -> updateStatus(user, "active"));
        holder.disableButton.setOnClickListener(v -> updateStatus(user, "disabled"));
        holder.deleteButton.setOnClickListener(v -> deleteUser(user));
    }

    private void updateStatus(Map<String, Object> user, String newStatus) {
        String id = (String) user.get("Id");
        String collection = user.get("UserType").equals("Worker") ? "workers" : "users";

        db.collection(collection).document(id)
                .update("status", newStatus)
                .addOnSuccessListener(a -> {
                    Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show();
                    user.put("status", newStatus);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteUser(Map<String, Object> user) {
        String id = (String) user.get("Id");
        String collection = user.get("UserType").equals("Worker") ? "workers" : "users";

        db.collection(collection).document(id)
                .delete()
                .addOnSuccessListener(a -> {
                    Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
                    userList.remove(user);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText, roleText, typeText, statusText;
        Button enableButton, disableButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            emailText = itemView.findViewById(R.id.emailText);
            roleText = itemView.findViewById(R.id.roleText);
            typeText = itemView.findViewById(R.id.typeText);
            statusText = itemView.findViewById(R.id.statusText);
            enableButton = itemView.findViewById(R.id.enableButton);
            disableButton = itemView.findViewById(R.id.disableButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
