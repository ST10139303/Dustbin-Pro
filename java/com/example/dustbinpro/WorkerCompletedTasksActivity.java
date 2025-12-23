package com.example.dustbinpro;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class WorkerCompletedTasksActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String workerId;

    private RecyclerView completedTasksRecyclerView;
    private ProgressBar progressBar;
    private TextView noTasksText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_completed_tasks);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        workerId = auth.getCurrentUser().getUid();

        initializeViews();
        loadCompletedTasks();

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        completedTasksRecyclerView = findViewById(R.id.completedTasksRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noTasksText = findViewById(R.id.noTasksText);
    }

    private void loadCompletedTasks() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("assignments")
                .whereEqualTo("workerId", workerId)
                .whereEqualTo("workerStatus", "Completed")
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);

                    if (queryDocumentSnapshots.isEmpty()) {
                        noTasksText.setVisibility(View.VISIBLE);
                    } else {
                        noTasksText.setVisibility(View.GONE);
                        // Setup recycler view with completed tasks
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load completed tasks", Toast.LENGTH_SHORT).show();
                });
    }
}