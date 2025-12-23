

public class Payment {
    private double amount;
    private String status;
    private long timestamp;

    public Payment() { }  // Required for Firestore

    public Payment(double amount, String status, long timestamp) {
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
}
