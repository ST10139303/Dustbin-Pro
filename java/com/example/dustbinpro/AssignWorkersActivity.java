package com.example.dustbinpro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import com.example.dustbinpro.adapters.AssignmentAdapter;
import com.example.dustbinpro.models.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class AssignWorkersActivity extends AppCompatActivity {

    private Spinner spinnerWorkers, spinnerBookings;
    private Button btnAssignWorker, btnRefresh, btnBack;
    private RecyclerView recyclerViewAssignments;
    private TextView tvNoAssignments, tvAssignmentCount;
    // REMOVED: private ProgressBar progressBar;

    private List<WorkerModel> workers = new ArrayList<>();
    private List<BookingModel> bookings = new ArrayList<>();
    private List<AssignmentViewModel> assignments = new ArrayList<>();
    private AssignmentAdapter assignmentAdapter;

    private FirebaseFirestore db;
    private CollectionReference workersRef, bookingsRef, assignmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_workers);

        initializeViews();
        setupFirebase();
        setupRecyclerView();
        setupClickListeners();

        loadData();
    }

    private void initializeViews() {
        spinnerWorkers = findViewById(R.id.spinnerWorkers);
        spinnerBookings = findViewById(R.id.spinnerBookings);
        btnAssignWorker = findViewById(R.id.btnAssignWorker);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnBack = findViewById(R.id.btnBack);
        recyclerViewAssignments = findViewById(R.id.recyclerViewAssignments);
        tvNoAssignments = findViewById(R.id.tvNoAssignments);
        tvAssignmentCount = findViewById(R.id.tvAssignmentCount);
        // REMOVED: progressBar = findViewById(R.id.progressBar);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        workersRef = db.collection("workers");
        bookingsRef = db.collection("bookings");
        assignmentsRef = db.collection("assignments");
    }

    private void setupRecyclerView() {
        assignmentAdapter = new AssignmentAdapter(assignments, new AssignmentAdapter.AssignmentActionListener() {
            @Override
            public void onAssignmentAction(String action, AssignmentViewModel assignment) {
                switch (action) {
                    case "COMPLETE":
                        completeAssignment(assignment);
                        break;
                    case "DELETE":
                        deleteAssignment(assignment);
                        break;
                }
            }
        });
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAssignments.setAdapter(assignmentAdapter);
    }

    private void setupClickListeners() {
        btnAssignWorker.setOnClickListener(v -> assignWorker());
        btnRefresh.setOnClickListener(v -> loadData());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadData() {
        // REMOVED: showLoading(true);
        loadWorkers();
        loadBookings();
        loadAssignments();
    }

    private void loadWorkers() {
        Log.d("FIREBASE", "Loading workers from Firestore...");

        workersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                workers.clear();
                int workerCount = 0;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    WorkerModel worker = new WorkerModel();
                    worker.setId(document.getId());

                    // Handle different field name cases
                    if (document.contains("Name")) {
                        worker.setName(document.getString("Name"));
                    } else if (document.contains("name")) {
                        worker.setName(document.getString("name"));
                    } else {
                        worker.setName("Unknown Worker");
                    }

                    if (document.contains("Email")) {
                        worker.setEmail(document.getString("Email"));
                    } else if (document.contains("email")) {
                        worker.setEmail(document.getString("email"));
                    }

                    workers.add(worker);
                    workerCount++;
                    Log.d("FIREBASE", "Loaded worker: " + worker.getName() + " (ID: " + worker.getId() + ")");
                }

                Log.d("FIREBASE", "Total workers loaded: " + workerCount);

                if (workerCount == 0) {
                    Log.w("FIREBASE", "No workers found in database!");
                    Toast.makeText(AssignWorkersActivity.this, "No workers found in database", Toast.LENGTH_LONG).show();
                }

                populateWorkerSpinner();
            } else {
                Log.e("FIREBASE", "Error loading workers: " + task.getException().getMessage());
                Toast.makeText(AssignWorkersActivity.this, "Error loading workers", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadBookings() {
        // Get bookings that are pending or approved (not completed)
        bookingsRef.whereIn("Status", Arrays.asList("pending", "approved", "assigned"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookings.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BookingModel booking = document.toObject(BookingModel.class);
                            booking.setId(document.getId());

                            // Convert Firestore Timestamp to Date
                            if (document.getTimestamp("BookingDate") != null) {
                                booking.setBookingDate(document.getTimestamp("BookingDate").toDate());
                            }
                            if (document.getTimestamp("CreatedAt") != null) {
                                booking.setCreatedAt(document.getTimestamp("CreatedAt").toDate());
                            }

                            bookings.add(booking);
                        }
                        populateBookingSpinner();
                    } else {
                        Toast.makeText(this, "Error loading bookings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAssignments() {
        Log.d("FIREBASE", "Loading assignments from Firestore...");

        assignmentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                assignments.clear();
                int assignmentCount = 0;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    AssignmentViewModel assignment = new AssignmentViewModel();
                    assignment.setId(document.getId());

                    // Map fields exactly as they appear in Firestore
                    if (document.contains("AssignedWorker")) {
                        assignment.setWorkerId(document.getString("AssignedWorker"));
                    }
                    if (document.contains("BookingId")) {
                        assignment.setBookingId(document.getString("BookingId"));
                    }
                    if (document.contains("WorkerStatus")) {
                        assignment.setWorkerStatus(document.getString("WorkerStatus"));
                    }
                    if (document.contains("Status")) {
                        assignment.setBookingStatus(document.getString("Status"));
                    }
                    if (document.contains("IsFullyCompleted")) {
                        assignment.setIsFullyCompleted(document.getBoolean("IsFullyCompleted"));
                    }
                    if (document.getTimestamp("CreatedAt") != null) {
                        assignment.setAssignedAt(document.getTimestamp("CreatedAt").toDate());
                    }
                    if (document.getTimestamp("UpdatedAt") != null) {
                        assignment.setUpdatedAt(document.getTimestamp("UpdatedAt").toDate());
                    }

                    assignments.add(assignment);
                    assignmentCount++;
                    Log.d("FIREBASE", "Loaded assignment: " + assignment.getId() +
                            " Worker: " + assignment.getWorkerId() +
                            " Status: " + assignment.getWorkerStatus());
                }

                Log.d("FIREBASE", "Total assignments loaded: " + assignmentCount);

                if (assignmentCount == 0) {
                    Log.w("FIREBASE", "No assignments found in database!");
                    Toast.makeText(AssignWorkersActivity.this, "No assignments found", Toast.LENGTH_LONG).show();
                } else {
                    // Now load the worker and booking details for each assignment
                    fetchAssignmentDetails();
                }
            } else {
                Log.e("FIREBASE", "Error loading assignments: " + task.getException().getMessage());
                Toast.makeText(AssignWorkersActivity.this, "Error loading assignments", Toast.LENGTH_SHORT).show();
                updateAssignmentsUI();
            }
        });
    }

    private void fetchAssignmentDetails() {
        if (assignments.isEmpty()) {
            updateAssignmentsUI();
            return;
        }

        final int[] completedFetches = {0};
        int totalAssignments = assignments.size();

        for (int i = 0; i < assignments.size(); i++) {
            AssignmentViewModel assignment = assignments.get(i);
            final int index = i;

            // Fetch worker details
            if (assignment.getWorkerId() != null && !assignment.getWorkerId().isEmpty()) {
                workersRef.document(assignment.getWorkerId()).get()
                        .addOnSuccessListener(workerDocument -> {
                            if (workerDocument.exists()) {
                                WorkerModel worker = workerDocument.toObject(WorkerModel.class);
                                if (worker != null) {
                                    assignment.setWorkerName(worker.getName());
                                    assignment.setWorkerEmail(worker.getEmail());
                                }
                            }

                            // Fetch booking details
                            if (assignment.getBookingId() != null && !assignment.getBookingId().isEmpty()) {
                                bookingsRef.document(assignment.getBookingId()).get()
                                        .addOnSuccessListener(bookingDocument -> {
                                            if (bookingDocument.exists()) {
                                                BookingModel booking = bookingDocument.toObject(BookingModel.class);
                                                if (booking != null) {
                                                    assignment.setBookingAddress(booking.getBookingAddress());
                                                    assignment.setCustomerName(booking.getCustomerName());
                                                    assignment.setServiceType(booking.getServiceType());

                                                    if (bookingDocument.getTimestamp("BookingDate") != null) {
                                                        assignment.setBookingDate(bookingDocument.getTimestamp("BookingDate").toDate());
                                                    }
                                                }
                                            }

                                            completedFetches[0]++;
                                            if (completedFetches[0] == totalAssignments) {
                                                updateAssignmentsUI();
                                            }
                                        });
                            } else {
                                completedFetches[0]++;
                                if (completedFetches[0] == totalAssignments) {
                                    updateAssignmentsUI();
                                }
                            }
                        });
            } else {
                completedFetches[0]++;
                if (completedFetches[0] == totalAssignments) {
                    updateAssignmentsUI();
                }
            }
        }
    }

    private void populateWorkerSpinner() {
        List<String> workerNames = new ArrayList<>();
        workerNames.add("-- Choose Worker --");

        for (WorkerModel worker : workers) {
            workerNames.add(worker.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, workerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkers.setAdapter(adapter);
    }

    private void populateBookingSpinner() {
        List<String> bookingDetails = new ArrayList<>();
        bookingDetails.add("-- Choose Booking --");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (BookingModel booking : bookings) {
            String dateStr = booking.getBookingDate() != null ?
                    sdf.format(booking.getBookingDate()) : "Unknown Date";
            String detail = booking.getBookingAddress() + " (" + dateStr + ")";
            bookingDetails.add(detail);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, bookingDetails);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBookings.setAdapter(adapter);
    }

    private void assignWorker() {
        int workerPosition = spinnerWorkers.getSelectedItemPosition();
        int bookingPosition = spinnerBookings.getSelectedItemPosition();

        if (workerPosition == 0 || bookingPosition == 0) {
            Toast.makeText(this, "Please select both worker and booking", Toast.LENGTH_SHORT).show();
            return;
        }

        String workerId = workers.get(workerPosition - 1).getId();
        String bookingId = bookings.get(bookingPosition - 1).getId();

        // Create assignment data matching your Firebase structure
        Map<String, Object> assignmentData = new HashMap<>();
        assignmentData.put("AssignedWorker", workerId);
        assignmentData.put("BookingId", bookingId);
        assignmentData.put("Status", "assigned");
        assignmentData.put("WorkerStatus", "Pending");
        assignmentData.put("IsFullyCompleted", false);
        assignmentData.put("CreatedAt", FieldValue.serverTimestamp());
        assignmentData.put("UpdatedAt", FieldValue.serverTimestamp());

        // Update booking status
        Map<String, Object> bookingUpdate = new HashMap<>();
        bookingUpdate.put("AssignedWorker", workerId);
        bookingUpdate.put("Status", "assigned");
        bookingUpdate.put("WorkerStatus", "Pending");
        bookingUpdate.put("UpdatedAt", FieldValue.serverTimestamp());

        // Check if assignment already exists for this booking
        assignmentsRef.whereEqualTo("BookingId", bookingId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Update existing assignment
                        String existingAssignmentId = task.getResult().getDocuments().get(0).getId();
                        assignmentsRef.document(existingAssignmentId).update(assignmentData)
                                .addOnSuccessListener(aVoid -> {
                                    // Update booking
                                    bookingsRef.document(bookingId).update(bookingUpdate)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(this, "Worker assignment updated!", Toast.LENGTH_SHORT).show();
                                                loadData();
                                            });
                                });
                    } else {
                        // Create new assignment
                        assignmentsRef.add(assignmentData)
                                .addOnSuccessListener(documentReference -> {
                                    // Update booking
                                    bookingsRef.document(bookingId).update(bookingUpdate)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Worker assigned successfully!", Toast.LENGTH_SHORT).show();
                                                loadData();
                                            });
                                });
                    }
                });
    }

    private void completeAssignment(AssignmentViewModel assignment) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("IsFullyCompleted", true);
        updates.put("WorkerStatus", "Completed");
        updates.put("Status", "completed");
        updates.put("CompletedAt", FieldValue.serverTimestamp());
        updates.put("UpdatedAt", FieldValue.serverTimestamp());

        assignmentsRef.document(assignment.getId()).update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Also update the booking
                    Map<String, Object> bookingUpdates = new HashMap<>();
                    bookingUpdates.put("Status", "completed");
                    bookingUpdates.put("WorkerStatus", "Completed");
                    bookingUpdates.put("UpdatedAt", FieldValue.serverTimestamp());

                    bookingsRef.document(assignment.getBookingId()).update(bookingUpdates)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Assignment completed successfully!", Toast.LENGTH_SHORT).show();
                                loadData();
                            });
                });
    }

    private void deleteAssignment(AssignmentViewModel assignment) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this assignment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    assignmentsRef.document(assignment.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                // Reset booking assignment
                                Map<String, Object> bookingUpdates = new HashMap<>();
                                bookingUpdates.put("AssignedWorker", FieldValue.delete());
                                bookingUpdates.put("Status", "approved");
                                bookingUpdates.put("WorkerStatus", FieldValue.delete());
                                bookingUpdates.put("UpdatedAt", FieldValue.serverTimestamp());

                                bookingsRef.document(assignment.getBookingId()).update(bookingUpdates)
                                        .addOnSuccessListener(aVoid1 -> {
                                            Toast.makeText(this, "Assignment deleted!", Toast.LENGTH_SHORT).show();
                                            loadData();
                                        });
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateAssignmentsUI() {
        tvAssignmentCount.setText(String.valueOf(assignments.size()));

        if (assignments.isEmpty()) {
            tvNoAssignments.setVisibility(View.VISIBLE);
            recyclerViewAssignments.setVisibility(View.GONE);
        } else {
            tvNoAssignments.setVisibility(View.GONE);
            recyclerViewAssignments.setVisibility(View.VISIBLE);
            assignmentAdapter.notifyDataSetChanged();
        }
    }

    // REMOVED: private void showLoading(boolean show) method
}