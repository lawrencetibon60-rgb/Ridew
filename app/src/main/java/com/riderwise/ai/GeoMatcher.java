package com.riderwise.ai;

public final class GeoMatcher {
    private GeoMatcher() {}
    public static double distanceKm(GeographicDataset.Point a, GeographicDataset.Point b) {
        if (a == null || b == null) return Double.NaN;
        double r = 6371, dLat = Math.toRadians(b.lat-a.lat), dLon = Math.toRadians(b.lon-a.lon);
        double x = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(a.lat))*Math.cos(Math.toRadians(b.lat))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2*r*Math.atan2(Math.sqrt(x), Math.sqrt(1-x));
    }
    public static double progress(GeographicDataset.Point start, GeographicDataset.Point end,
                                  GeographicDataset.Point p) {
        if (start == null || end == null || p == null) return Double.NaN;
        double total = distanceKm(start, end);
        if (total == 0 || Double.isNaN(total)) return Double.NaN;
        return (distanceKm(start,p) + distanceKm(p,end) - total) / 2.0;
    }
}
