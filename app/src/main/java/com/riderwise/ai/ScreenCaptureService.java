package com.riderwise.ai;

import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.hardware.display.*;
import android.media.*;
import android.media.projection.*;
import android.os.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScreenCaptureService extends Service {
    public static final String EXTRA_RESULT_CODE = "resultCode";
    public static final String EXTRA_DATA = "resultData";
    private static final int NOTIFICATION_ID = 210;
    private Handler handler;
    private MediaProjection projection;
    private VirtualDisplay display;
    private ImageReader reader;
    private OcrProcessor ocr;
    private BookingStore store;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private final Runnable scan = new Runnable() {
        @Override public void run() {
            captureAndProcess();
            if (handler != null) handler.postDelayed(this, 5000);
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        ocr = new OcrProcessor();
        store = new BookingStore(this);
        createChannel();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || !intent.hasExtra(EXTRA_DATA)) { stopSelf(); return START_NOT_STICKY; }
        startForeground(NOTIFICATION_ID, notification());
        int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED);
        Intent data = (Intent) intent.getParcelableExtra(EXTRA_DATA);
        MediaProjectionManager mpm = (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        projection = mpm.getMediaProjection(resultCode, data);
        if (projection == null) { stopSelf(); return START_NOT_STICKY; }
        projection.registerCallback(new MediaProjection.Callback() {
            @Override public void onStop() { stopCapture(); stopSelf(); }
        }, handler);
        startCapture();
        handler.removeCallbacks(scan);
        handler.post(scan);
        if (android.provider.Settings.canDrawOverlays(this))
            startService(new Intent(this, FloatingAssistantService.class));
        return START_NOT_STICKY;
    }

    private void startCapture() {
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels, height = dm.heightPixels, density = dm.densityDpi;
        reader = ImageReader.newInstance(width, height, android.graphics.PixelFormat.RGBA_8888, 2);
        display = projection.createVirtualDisplay("RiderWiseCapture", width, height, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, reader.getSurface(), null, handler);
    }

    private void captureAndProcess() {
        if (reader == null || !processing.compareAndSet(false, true)) return;
        Image image = null;
        try {
            image = reader.acquireLatestImage();
            if (image == null) { processing.set(false); return; }
            Image.Plane plane = image.getPlanes()[0];
            ByteBuffer buffer = plane.getBuffer();
            int pixelStride = plane.getPixelStride(), rowStride = plane.getRowStride();
            int width = image.getWidth(), height = image.getHeight();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap padded = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            padded.copyPixelsFromBuffer(buffer);
            Bitmap bitmap = Bitmap.createBitmap(padded, 0, 0, width, height);
            padded.recycle();
            image.close(); image = null;
            ocr.process(bitmap, bookings -> {
                for (Booking b : bookings) store.upsert(b);
                List<Booking> all = store.getRecent();
                String summary = "OCR cycle complete\nDetected: " + all.size()
                        + "\nLatest: " + (bookings.isEmpty() ? "No validated card" :
                        bookings.get(0).pickup + " → " + bookings.get(0).dropoff);
                FloatingAssistantService.update(this, summary);
                bitmap.recycle();
                processing.set(false);
            });
        } catch (Exception e) {
            if (image != null) image.close();
            processing.set(false);
            android.util.Log.e("RiderWise", "Capture/OCR cycle failed", e);
        }
    }

    private void stopCapture() {
        if (handler != null) handler.removeCallbacks(scan);
        if (display != null) { display.release(); display = null; }
        if (reader != null) { reader.close(); reader = null; }
        if (projection != null) { projection.stop(); projection = null; }
    }
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel("riderwise_capture", "RiderWise monitoring", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
    }
    private Notification notification() {
        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, "riderwise_capture") : new Notification.Builder(this);
        return b.setContentTitle("RiderWise AI is monitoring")
                .setContentText("OCR target interval: 5 seconds. Use only while safely stopped.")
                .setSmallIcon(android.R.drawable.ic_menu_view).build();
    }
    @Override public void onDestroy() { stopCapture(); if (ocr != null) ocr.close(); if (store != null) store.close(); super.onDestroy(); }
    @Override public android.os.IBinder onBind(Intent intent) { return null; }
}
