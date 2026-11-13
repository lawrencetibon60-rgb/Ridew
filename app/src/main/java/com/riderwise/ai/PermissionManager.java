package com.riderwise.ai;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.provider.Settings;

public final class PermissionManager {
    public static final int REQUEST_CAPTURE = 4102;
    private PermissionManager() {}
    public static boolean hasOverlay(Activity a) { return Settings.canDrawOverlays(a); }
    public static void requestOverlay(Activity a) {
        Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + a.getPackageName()));
        a.startActivity(i);
    }
    public static Intent captureIntent(Activity a) {
        return ((MediaProjectionManager)a.getSystemService(Activity.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent();
    }
}
