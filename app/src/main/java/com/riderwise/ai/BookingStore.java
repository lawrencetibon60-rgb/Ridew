package com.riderwise.ai;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class BookingStore extends SQLiteOpenHelper {
    public BookingStore(Context c) { super(c, "riderwise.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bookings(id TEXT PRIMARY KEY, pickup TEXT, dropoff TEXT, fare REAL, pickup_km REAL, confidence REAL, detected_at INTEGER, fingerprint TEXT UNIQUE, status TEXT, is_reference INTEGER)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {}
    public synchronized void upsert(Booking b) {
        ContentValues v = new ContentValues();
        v.put("id", b.id); v.put("pickup", b.pickup); v.put("dropoff", b.dropoff);
        v.put("fare", b.fare); v.put("pickup_km", b.pickupDistanceKm);
        v.put("confidence", b.confidence); v.put("detected_at", b.detectedAt);
        v.put("fingerprint", b.fingerprint); v.put("status", b.status);
        v.put("is_reference", b.reference ? 1 : 0);
        getWritableDatabase().insertWithOnConflict("bookings", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public synchronized List<Booking> getRecent() {
        List<Booking> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT pickup,dropoff,fare,pickup_km,confidence,detected_at FROM bookings ORDER BY detected_at DESC LIMIT 100", null)) {
            while (c.moveToNext()) list.add(new Booking(c.getString(0), c.getString(1), c.getDouble(2),
                    c.getDouble(3), c.getFloat(4), c.getLong(5)));
        }
        return list;
    }
}
