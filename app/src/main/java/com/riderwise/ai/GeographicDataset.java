package com.riderwise.ai;

import java.util.*;

public final class GeographicDataset {
    public static final class Point {
        public final double lat, lon;
        Point(double lat, double lon) { this.lat = lat; this.lon = lon; }
    }
    private static final Map<String, Point> POINTS = new HashMap<>();
    static {
        // Approximate city/area centroids. Extend with verified local data as needed.
        add("san mateo", 14.6982, 121.1236); add("batasan", 14.6917, 121.0950);
        add("commonwealth", 14.6910, 121.0830); add("holy spirit", 14.6870, 121.0770);
        add("diliman", 14.6536, 121.0685); add("quezon avenue", 14.6426, 121.0380);
        add("quezon city", 14.6760, 121.0437); add("pasig", 14.5764, 121.0851);
        add("marikina", 14.6507, 121.1029); add("manila", 14.5995, 120.9842);
        add("san juan", 14.6019, 121.0355); add("mandaluyong", 14.5794, 121.0359);
        add("makati", 14.5547, 121.0244); add("taguig", 14.5176, 121.0509);
        add("caloocan", 14.6488, Double.NaN);
        add("novaliches", 14.7214, 121.0389); add("fairview", 14.7350, 121.0580);
        add("cubao", 14.6198, 121.0569); add("katipunan", 14.6370, 121.0730);
    }
    private static void add(String name, double lat, double lon) {
        if (!Double.isNaN(lon)) POINTS.put(name, new Point(lat, lon));
    }
    public static Point get(String name) { return POINTS.get(BookingValidator.normalize(name)); }
    public static Set<String> names() { return Collections.unmodifiableSet(POINTS.keySet()); }
}
