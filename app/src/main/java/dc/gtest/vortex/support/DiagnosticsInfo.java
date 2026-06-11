package dc.gtest.vortex.support;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.ActivityManager;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dc.gtest.vortex.BuildConfig;

import static dc.gtest.vortex.support.MyPrefs.PREF_API_CONNECTION_TIMEOUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_API_READ_TIMEOUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class DiagnosticsInfo {

    public static String collect(Context ctx) {
        StringBuilder sb = new StringBuilder();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());

        sb.append("========================================\n");
        sb.append("       VORTEX DIAGNOSTICS REPORT        \n");
        sb.append("========================================\n");
        sb.append("Generated: ").append(timestamp).append("\n\n");

        // --- Device ---
        sb.append("--- Device ---\n");
        sb.append("Manufacturer : ").append(Build.MANUFACTURER).append("\n");
        sb.append("Model        : ").append(Build.MODEL).append("\n");
        sb.append("Device       : ").append(Build.DEVICE).append("\n");
        sb.append("Android Ver  : ").append(Build.VERSION.RELEASE).append("\n");
        sb.append("API Level    : ").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("Build Number : ").append(Build.DISPLAY).append("\n\n");

        // --- App ---
        sb.append("--- Application ---\n");
        sb.append("App Version  : ").append(BuildConfig.VERSION_NAME).append("\n");
        sb.append("Build Type   : ").append(BuildConfig.BUILD_TYPE).append("\n");
        sb.append("Username     : ").append(MyPrefs.getString(PREF_USER_NAME, "-")).append("\n");
        sb.append("Device ID    : ").append(MyPrefs.getDeviceId(PREF_DEVICE_ID, "-")).append("\n\n");

        // --- Server Settings ---
        sb.append("--- Server Settings ---\n");
        sb.append("Base URL     : ").append(MyPrefs.getString(PREF_BASE_HOST_URL, "-")).append("\n");
        sb.append("Conn Timeout : ").append(MyPrefs.getInt(PREF_API_CONNECTION_TIMEOUT, 15)).append("s\n");
        sb.append("Read Timeout : ").append(MyPrefs.getInt(PREF_API_READ_TIMEOUT, 120)).append("s\n\n");

        // --- Permissions ---
        sb.append("--- Permissions ---\n");
        sb.append(permissionStatus(ctx, Manifest.permission.CAMERA,               "Camera          "));
        sb.append(permissionStatus(ctx, Manifest.permission.ACCESS_FINE_LOCATION,  "Location Fine   "));
        sb.append(permissionStatus(ctx, Manifest.permission.ACCESS_COARSE_LOCATION,"Location Coarse "));
        sb.append(permissionStatus(ctx, Manifest.permission.READ_EXTERNAL_STORAGE, "Read Storage    "));
        sb.append(permissionStatus(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE,"Write Storage   "));
        sb.append(permissionStatus(ctx, Manifest.permission.INTERNET,              "Internet        "));
        sb.append(permissionStatus(ctx, Manifest.permission.ACCESS_NETWORK_STATE,  "Network State   "));
        sb.append("\n");

        // --- Network ---
        sb.append("--- Network ---\n");
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                sb.append("Status       : Connected\n");
                sb.append("Type         : ").append(activeNetwork.getTypeName()).append("\n");
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wm = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wi = wm.getConnectionInfo();
                    sb.append("SSID         : ").append(wi.getSSID()).append("\n");
                    sb.append("Signal       : ").append(wi.getRssi()).append(" dBm\n");
                }
            } else {
                sb.append("Status       : Not Connected\n");
            }
        } catch (Exception e) {
            sb.append("Status       : Unable to determine (").append(e.getMessage()).append(")\n");
        }
        sb.append("\n");

        // --- Memory ---
        sb.append("--- Memory ---\n");
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memInfo);
            sb.append("Available    : ").append(memInfo.availMem / 1048576L).append(" MB\n");
            sb.append("Total        : ").append(memInfo.totalMem / 1048576L).append(" MB\n");
            sb.append("Low Memory   : ").append(memInfo.lowMemory).append("\n");
        } catch (Exception e) {
            sb.append("Unable to determine (").append(e.getMessage()).append(")\n");
        }
        sb.append("\n");

        // --- Battery ---
        sb.append("--- Battery ---\n");
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = ctx.registerReceiver(null, ifilter);
            if (batteryStatus != null) {
                int level  = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale  = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                float pct  = level * 100 / (float) scale;
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL;
                sb.append("Level        : ").append(String.format(Locale.ENGLISH, "%.0f%%", pct)).append("\n");
                sb.append("Charging     : ").append(isCharging).append("\n");
            }
        } catch (Exception e) {
            sb.append("Unable to determine (").append(e.getMessage()).append(")\n");
        }
        sb.append("\n");

        sb.append("========================================\n");
        sb.append("             END OF REPORT              \n");
        sb.append("========================================\n");

        return sb.toString();
    }

    private static String permissionStatus(Context ctx, String permission, String label) {
        boolean granted = ContextCompat.checkSelfPermission(ctx, permission)
                == PackageManager.PERMISSION_GRANTED;
        return label + ": " + (granted ? "GRANTED" : "DENIED") + "\n";
    }
}