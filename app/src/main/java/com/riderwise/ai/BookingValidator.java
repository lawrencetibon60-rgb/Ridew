package com.riderwise.ai;

import java.util.Locale;

public final class BookingValidator {
    private BookingValidator() {}
    public static boolean isValid(Booking b) {
        return b != null && b.pickup != null && b.dropoff != null
                && !b.pickup.trim().isEmpty() && !b.dropoff.trim().isEmpty()
                && !b.pickup.equalsIgnoreCase(b.dropoff)
                && b.fare > 0 && b.fare <= 100000
                && b.confidence >= 0.30f;
    }
    public static String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
