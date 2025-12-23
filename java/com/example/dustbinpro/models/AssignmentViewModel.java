package com.example.dustbinpro.models;

import java.util.Date;

public class AssignmentViewModel {
    private String id;
    private String AssignedWorker;
    private String BookingId;
    private String Status;
    private String WorkerStatus;
    private Boolean IsFullyCompleted;
    private Date CreatedAt;
    private Date UpdatedAt;
    private Date CompletedAt;

    // Additional fields we'll populate
    private String WorkerName;
    private String WorkerEmail;
    private String BookingAddress;
    private String CustomerName;
    private String ServiceType;
    private Date BookingDate;

    public AssignmentViewModel() {} // Required for Firestore

    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWorkerId() { return AssignedWorker; }
    public void setWorkerId(String workerId) { this.AssignedWorker = workerId; }

    public String getBookingId() { return BookingId; }
    public void setBookingId(String bookingId) { this.BookingId = bookingId; }

    public String getBookingStatus() { return Status; }
    public void setBookingStatus(String status) { this.Status = status; }

    public String getWorkerStatus() { return WorkerStatus; }
    public void setWorkerStatus(String workerStatus) { this.WorkerStatus = workerStatus; }

    public Boolean getIsFullyCompleted() { return IsFullyCompleted; }
    public void setIsFullyCompleted(Boolean isFullyCompleted) { this.IsFullyCompleted = isFullyCompleted; }

    public Date getAssignedAt() { return CreatedAt; }
    public void setAssignedAt(Date assignedAt) { this.CreatedAt = assignedAt; }

    public Date getUpdatedAt() { return UpdatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.UpdatedAt = updatedAt; }

    public Date getCompletedAt() { return CompletedAt; }
    public void setCompletedAt(Date completedAt) { this.CompletedAt = completedAt; }

    public String getWorkerName() { return WorkerName; }
    public void setWorkerName(String workerName) { this.WorkerName = workerName; }

    public String getWorkerEmail() { return WorkerEmail; }
    public void setWorkerEmail(String workerEmail) { this.WorkerEmail = workerEmail; }

    public String getBookingAddress() { return BookingAddress; }
    public void setBookingAddress(String bookingAddress) { this.BookingAddress = bookingAddress; }

    public String getCustomerName() { return CustomerName; }
    public void setCustomerName(String customerName) { this.CustomerName = customerName; }

    public String getServiceType() { return ServiceType; }
    public void setServiceType(String serviceType) { this.ServiceType = serviceType; }

    public Date getBookingDate() { return BookingDate; }
    public void setBookingDate(Date bookingDate) { this.BookingDate = bookingDate; }

    public Date getDate() { return BookingDate; }
    public void setDate(Date date) { this.BookingDate = date; }
}