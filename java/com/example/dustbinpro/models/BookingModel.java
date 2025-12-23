package com.example.dustbinpro.models;

import java.util.Date;

public class BookingModel {
    private String id;
    private String BookingAddress;
    private Date BookingDate;
    private String Status;
    private String CustomerName;
    private String ServiceType;
    private String AssignedWorker;
    private Date CreatedAt;
    private Date UpdatedAt;

    public BookingModel() {} // Required for Firestore

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBookingAddress() { return BookingAddress; }
    public void setBookingAddress(String bookingAddress) { this.BookingAddress = bookingAddress; }
    public Date getBookingDate() { return BookingDate; }
    public void setBookingDate(Date bookingDate) { this.BookingDate = bookingDate; }
    public String getStatus() { return Status; }
    public void setStatus(String status) { this.Status = status; }
    public String getCustomerName() { return CustomerName; }
    public void setCustomerName(String customerName) { this.CustomerName = customerName; }
    public String getServiceType() { return ServiceType; }
    public void setServiceType(String serviceType) { this.ServiceType = serviceType; }
    public String getAssignedWorker() { return AssignedWorker; }
    public void setAssignedWorker(String assignedWorker) { this.AssignedWorker = assignedWorker; }
    public Date getCreatedAt() { return CreatedAt; }
    public void setCreatedAt(Date createdAt) { this.CreatedAt = createdAt; }
    public Date getUpdatedAt() { return UpdatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.UpdatedAt = updatedAt; }
}