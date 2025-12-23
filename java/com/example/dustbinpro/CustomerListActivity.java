package com.example.dustbinpro;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.adapters.CustomerAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noDataText;
    private FirebaseFirestore db;
    private CustomerAdapter adapter;
    private List<Map<String, Object>> customerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        recyclerView = findViewById(R.id.customerRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noDataText = findViewById(R.id.noCustomersText);

        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(customerList);
        recyclerView.setAdapter(adapter);

        loadCustomers();
    }

    private void loadCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        noDataText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        // Get users
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                processUserDocuments(task.getResult());
            } else {
                progressBar.setVisibility(View.GONE);
                noDataText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void processUserDocuments(@NonNull QuerySnapshot usersSnapshot) {
        customerList.clear();

        for (DocumentSnapshot doc : usersSnapshot.getDocuments()) {
            Map<String, Object> data = new HashMap<>(doc.getData());
            data.put("id", doc.getId());
            data.put("UserType", "User");

            String role = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                role = (String) data.getOrDefault("role", "Customer");
            }
            data.put("DisplayRole", role.toUpperCase());

            customerList.add(data);
        }

        // Now get workers
        db.collection("workers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    Map<String, Object> data = new HashMap<>(doc.getData());
                    data.put("id", doc.getId());
                    data.put("UserType", "Worker");
                    data.put("DisplayRole", "WORKER");

                    // Normalize fields
                    if (data.containsKey("Name") && !data.containsKey("name"))
                        data.put("name", data.get("Name"));
                    if (data.containsKey("Email") && !data.containsKey("email"))
                        data.put("email", data.get("Email"));
                    if (data.containsKey("Phone") && !data.containsKey("phone"))
                        data.put("phone", data.get("Phone"));

                    customerList.add(data);
                }
            }

            // Update UI
            progressBar.setVisibility(View.GONE);
            if (customerList.isEmpty()) {
                noDataText.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
