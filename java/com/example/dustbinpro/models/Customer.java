package com.example.dustbinpro.models;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Customer {
    private String name;
    private String email;
    private Timestamp registered;

    public Customer() {
        // Required empty constructor for Firestore
    }

    public Customer(String name, String email, Timestamp registered) {
        this.name = name;
        this.email = email;
        this.registered = registered;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getRegistered() {
        return registered;
    }

    public String getFormattedDate() {
        if (registered != null) {
            Date date = registered.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        } else {
            return "Unknown";
        }
    }
}
