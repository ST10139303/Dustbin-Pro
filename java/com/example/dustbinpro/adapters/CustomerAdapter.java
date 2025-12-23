package com.example.dustbinpro.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.R;

import java.util.List;
import java.util.Map;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private final List<Map<String, Object>> customerList;

    public CustomerAdapter(List<Map<String, Object>> customerList) {
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Map<String, Object> user = customerList.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.nameText.setText((String) user.getOrDefault("name", "Unknown"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.emailText.setText((String) user.getOrDefault("email", "Not provided"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.roleText.setText((String) user.getOrDefault("DisplayRole", "CUSTOMER"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.typeText.setText((String) user.getOrDefault("UserType", "User"));
        }
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText, roleText, typeText;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textName);
            emailText = itemView.findViewById(R.id.textEmail);
            roleText = itemView.findViewById(R.id.textRole);
            typeText = itemView.findViewById(R.id.textType);
        }
    }
}
