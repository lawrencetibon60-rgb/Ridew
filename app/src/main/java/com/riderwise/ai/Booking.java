package com.riderwise.ai;

public class Booking {
    public String id, pickup, dropoff, fingerprint, status;
    public double fare, pickupDistanceKm;
    public float confidence;
    public long detectedAt;
    public boolean reference;

    public Booking(String pickup, String dropoff, double fare, double pickupDistanceKm,
                   float confidence, long detectedAt) {
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.fare = fare;
        this.pickupDistanceKm = pickupDistanceKm;
        this.confidence = confidence;
        this.detectedAt = detectedAt;
        this.status = "DETECTED";
        this.fingerprint = (pickup + "|" + dropoff + "|" + String.format(java.util.Locale.US, "%.2f", fare))
                .toLowerCase(java.util.Locale.ROOT);
        this.id = Integer.toHexString(fingerprint.hashCode());
    }
}
