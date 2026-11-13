package com.riderwise.ai;

public class RouteCompatibilityEngine {
    public static class Result {
        public final String label;
        public final double score, deviationKm;
        Result(String label, double score, double deviationKm) {
            this.label = label; this.score = score; this.deviationKm = deviationKm;
        }
    }

    public Result evaluate(Booking reference, Booking candidate) {
        GeographicDataset.Point rs = GeographicDataset.get(reference.pickup);
        GeographicDataset.Point rd = GeographicDataset.get(reference.dropoff);
        GeographicDataset.Point cp = GeographicDataset.get(candidate.pickup);
        GeographicDataset.Point cd = GeographicDataset.get(candidate.dropoff);
        if (rs == null || rd == null || cp == null || cd == null)
            return new Result("POSSIBLE COMPATIBILITY", 0.35, Double.NaN);

        double pickupDeviation = GeoMatcher.progress(rs, rd, cp);
        double dropDeviation = GeoMatcher.progress(rs, rd, cd);
        double pickupFromStart = GeoMatcher.distanceKm(rs, cp);
        double destinationDistance = GeoMatcher.distanceKm(cp, cd);
        boolean nearPickupCorridor = pickupDeviation <= 4.0;
        boolean nearDropCorridor = dropDeviation <= 4.0;
        boolean plausibleSequence = pickupFromStart < 30 && destinationDistance < 45;

        if (!plausibleSequence) return new Result("EXCESSIVE BACKTRACKING", 0.10, pickupDeviation);
        if (pickupDeviation > 8 && dropDeviation > 8)
            return new Result("INCOMPATIBLE", 0.05, pickupDeviation);
        if (nearPickupCorridor && nearDropCorridor)
            return new Result("SAME ROUTE / ALONG THE WAY", 0.90, Math.max(pickupDeviation, dropDeviation));
        if (nearPickupCorridor)
            return new Result("ALONG THE WAY", 0.78, pickupDeviation);
        if (nearDropCorridor)
            return new Result("NEAR THE ROUTE", 0.62, dropDeviation);
        return new Result("POSSIBLE COMPATIBILITY", 0.40, Math.min(pickupDeviation, dropDeviation));
    }
}
