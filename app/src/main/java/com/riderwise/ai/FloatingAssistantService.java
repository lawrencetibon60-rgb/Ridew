package com.riderwise.ai;

import android.app.*;
import android.content.*;
import android.graphics.PixelFormat;
import android.os.*;
import android.view.*;
import android.widget.*;

public class FloatingAssistantService extends Service {
    private WindowManager wm;
    private View panel;
    private TextView content;
    @Override public void onCreate() {
        super.onCreate();
        if (!android.provider.Settings.canDrawOverlays(this)) { stopSelf(); return; }
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(18,14,18,14);
        box.setBackgroundColor(0xEE202124);
        TextView title = new TextView(this); title.setText("RiderWise AI 2.0"); title.setTextColor(0xFFFF8A50); title.setTextSize(15);
        content = new TextView(this); content.setText("Monitoring ready. Review bookings only when safely stopped."); content.setTextColor(0xFFFFFFFF); content.setTextSize(12);
        box.addView(title); box.addView(content);
        panel = box;
        int type = Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                280, WindowManager.LayoutParams.WRAP_CONTENT, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.TOP | Gravity.END; p.x = 12; p.y = 180;
        wm.addView(panel, p);
    }
    public static void update(Context c, String text) {
        Intent i = new Intent(c, FloatingAssistantService.class);
        i.setAction("UPDATE"); i.putExtra("text", text);
        if (Build.VERSION.SDK_INT >= 26) c.startService(i); else c.startService(i);
    }
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "UPDATE".equals(intent.getAction()) && content != null)
            content.setText(intent.getStringExtra("text"));
        return START_STICKY;
    }
    @Override public void onDestroy() { if (wm != null && panel != null) wm.removeView(panel); super.onDestroy(); }
    @Override public IBinder onBind(Intent intent) { return null; }
}
