package com.riderwise.ai;

import android.app.*;
import android.content.*;
import android.os.*;
import android.provider.Settings;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView status;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24,32,24,24);
        root.setBackgroundColor(0xFFF7F7F7);
        TextView title = new TextView(this); title.setText("RiderWise AI v2.0"); title.setTextSize(25); title.setTextColor(0xFF202124);
        TextView sub = new TextView(this); sub.setText("Lalamove booking OCR • Route compatibility • Multibooking analysis"); sub.setTextSize(14);
        status = new TextView(this); status.setText("Status: Not monitoring"); status.setPadding(0,24,0,24);
        Button overlay = new Button(this); overlay.setText("Enable floating assistant");
        overlay.setOnClickListener(v -> { if (!Settings.canDrawOverlays(this)) PermissionManager.requestOverlay(this); else startService(new Intent(this, FloatingAssistantService.class)); });
        Button start = new Button(this); start.setText("Start screen monitoring");
        start.setOnClickListener(v -> startActivityForResult(PermissionManager.captureIntent(this), PermissionManager.REQUEST_CAPTURE));
        Button stop = new Button(this); stop.setText("Stop monitoring");
        stop.setOnClickListener(v -> { stopService(new Intent(this, ScreenCaptureService.class)); stopService(new Intent(this, FloatingAssistantService.class)); status.setText("Status: Stopped"); });
        TextView note = new TextView(this);
        note.setText("Safety: Review recommendations only when your motorcycle is parked safely. RiderWise never accepts bookings automatically. Android may pause capture or background activity.");
        note.setTextSize(12); note.setPadding(0,24,0,0);
        root.addView(title); root.addView(sub); root.addView(status); root.addView(overlay); root.addView(start); root.addView(stop); root.addView(note);
        setContentView(root);
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionManager.REQUEST_CAPTURE && resultCode == RESULT_OK && data != null) {
            Intent service = new Intent(this, ScreenCaptureService.class);
            service.putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, resultCode);
            service.putExtra(ScreenCaptureService.EXTRA_DATA, data);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(service); else startService(service);
            status.setText("Status: Monitoring requested");
        } else status.setText("Status: Screen capture permission not granted");
    }
}
