package com.riderwise.ai;

import com.google.mlkit.vision.text.Text;
import java.util.*;
import java.util.regex.*;

public class BookingExtractor {
    private static final Pattern FARE = Pattern.compile("₱\\s*([0-9,]+(?:\\.[0-9]{1,2})?)");
    private static final Pattern KM = Pattern.compile("~?\\s*([0-9]+(?:\\.[0-9]+)?)\\s*km", Pattern.CASE_INSENSITIVE);

    public List<Booking> extract(Text text) {
        List<Text.Line> lines = new ArrayList<>();
        for (Text.TextBlock block : text.getTextBlocks()) lines.addAll(block.getLines());
        lines.sort(Comparator.comparingInt(l -> l.getBoundingBox() == null ? 0 : l.getBoundingBox().centerY()));
        List<Booking> results = new ArrayList<>();
        List<String> card = new ArrayList<>();
        int cardStart = -1;
        for (Text.Line line : lines) {
            String s = line.getText().trim();
            if (s.toLowerCase(Locale.ROOT).contains("pick up now")) {
                if (!card.isEmpty()) addCard(card, results);
                card.clear(); cardStart = line.getBoundingBox() == null ? 0 : line.getBoundingBox().centerY();
            }
            if (cardStart >= 0) card.add(s);
        }
        if (!card.isEmpty()) addCard(card, results);
        return results;
    }

    private void addCard(List<String> lines, List<Booking> out) {
        String pickup = null, dropoff = null;
        double fare = 0, pickupKm = Double.NaN;
        int routeFields = 0;
        String joined = String.join(" ", lines);
        Matcher fm = FARE.matcher(joined);
        if (fm.find()) try { fare = Double.parseDouble(fm.group(1).replace(",", "")); } catch (Exception ignored) {}
        Matcher km = KM.matcher(joined);
        if (km.find()) try { pickupKm = Double.parseDouble(km.group(1)); } catch (Exception ignored) {}
        for (String line : lines) {
            String clean = line.replaceAll("[^\\p{L}\\p{N} .'-]", "").trim();
            if (clean.isEmpty() || clean.toLowerCase(Locale.ROOT).contains("pick up")
                    || clean.toLowerCase(Locale.ROOT).contains("delivery")
                    || clean.toLowerCase(Locale.ROOT).contains("priority")
                    || clean.toLowerCase(Locale.ROOT).contains("motorcycle")) continue;
            if (clean.matches("(?i).*(₱|km|\u20b1).*")) continue;
            if (routeFields == 0 && clean.length() >= 3) { pickup = clean; routeFields++; }
            else if (routeFields == 1 && clean.length() >= 3) { dropoff = clean; routeFields++; break; }
        }
        if (pickup != null && dropoff != null && fare > 0) {
            Booking b = new Booking(pickup, dropoff, fare, pickupKm, 0.55f, System.currentTimeMillis());
            if (BookingValidator.isValid(b)) out.add(b);
        }
    }
}
