package com.riderwise.ai;

import java.util.*;

public class RecommendationEngine {
    public static class Recommendation {
        public final List<Booking> bookings;
        public final double totalFare, score;
        public final String compatibility;
        Recommendation(List<Booking> b, double fare, double score, String label) {
            bookings = b; totalFare = fare; this.score = score; compatibility = label;
        }
    }
    private final RouteCompatibilityEngine matcher = new RouteCompatibilityEngine();

    public List<Recommendation> recommend(List<Booking> bookings, Booking reference) {
        List<Recommendation> out = new ArrayList<>();
        if (reference == null) return out;
        List<Booking> candidates = new ArrayList<>();
        for (Booking b : bookings) if (!b.id.equals(reference.id) && BookingValidator.isValid(b)) candidates.add(b);
        // Bounded combinations up to 6 additional bookings (7 total including reference).
        enumerate(candidates, 0, new ArrayList<>(), reference, out);
        out.sort((a,b) -> Double.compare(b.score, a.score));
        return out.subList(0, Math.min(20, out.size()));
    }

    private void enumerate(List<Booking> src, int index, List<Booking> chosen,
                           Booking ref, List<Recommendation> out) {
        if (!chosen.isEmpty()) {
            double fare = ref.fare;
            double compatibility = 0, confidence = ref.confidence;
            String label = "POSSIBLE COMPATIBILITY";
            for (Booking b : chosen) {
                RouteCompatibilityEngine.Result r = matcher.evaluate(ref, b);
                compatibility += r.score;
                if (r.score >= 0.75) label = r.label;
                fare += b.fare;
                confidence += b.confidence;
            }
            double avg = compatibility / chosen.size();
            double avgConfidence = confidence / (chosen.size()+1);
            double score = avg * 0.65 + avgConfidence * 0.20 + Math.min(fare/1000.0,1.0)*0.15;
            out.add(new Recommendation(new ArrayList<>(chosen), fare, score, label));
        }
        if (chosen.size() >= 6) return;
        for (int i=index; i<src.size(); i++) {
            chosen.add(src.get(i));
            enumerate(src, i+1, chosen, ref, out);
            chosen.remove(chosen.size()-1);
        }
    }
}
