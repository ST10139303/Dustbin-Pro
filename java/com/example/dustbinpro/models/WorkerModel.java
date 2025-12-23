package com.example.dustbinpro.models;

public class WorkerModel {
    private String id;
    private String name;
    private String email;

    public WorkerModel() {} // Required for Firestore

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}