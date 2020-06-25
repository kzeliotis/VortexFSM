package am.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.UUID;

import am.gtest.vortex.BuildConfig;
import am.gtest.vortex.R;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

//    private String deviceImei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Crashlytics.getInstance().crash(); // Force a crash

//        TextView tvDeviceId = findViewById(R.id.tvDeviceId);

        // remove when registration process is switched on
        String deviceId = getString(R.string.default_device_id);

        if (deviceId.isEmpty()) { // Means release variant, where default_device_id is empty
            deviceId = MyPrefs.getDeviceId(PREF_DEVICE_ID, "");

            Log.e(LOG_TAG, "-------------------- deviceId: " + deviceId);

            if (deviceId.isEmpty()) { // Means no previously saved device_id is available
                //String Version = BuildConfig.VERSION_NAME;
                //Version = Version.replace(".","");
                deviceId = String.valueOf( UUID.randomUUID()).replace("-", "");
            }
        }

        MyPrefs.setDeviceId(PREF_DEVICE_ID, deviceId);

        Log.e(LOG_TAG, "-------------------- deviceId: " + deviceId);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

        // uncomment when registration process is switched on
//        TextView tvDeviceId = (TextView) findViewById(R.id.tvDeviceId);
//
//        boolean isRegisteredApk = MyPrefs.getBoolean(MainActivity.this, MyPrefs.PREF_REGISTERED_APK);
//
//        if (isRegisteredApk) {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            String userId = String.valueOf( UUID.randomUUID()).replace("-", "");
//
//            Log.e(LOG_TAG, "-------------------- userId: " + userId);
//
//            MyPrefs.setString(MainActivity.this, MyPrefs.PREF_USER_ID, userId);
//
//            if (MyUtils.isNetworkAvailable()) {
//                ToSendDeviceId toSendDeviceId = new ToSendDeviceId(this);
//                toSendDeviceId.execute(userId);
//            } else {
//                tvDeviceId.setText(userId);
//            }
//        }

        // The old approach with getting IMEI, Android User ID, and WiFi mac address
//        requestPermission();
//
//        if (deviceImei != null) {
//            checkRegistrationContinue(deviceImei);
//        } else {
//            /*
//             * A 64-bit number (as a hex string) that is randomly generated when the user first sets
//             * up the device and should remain constant for the lifetime of the user's device.
//             * The value may change if a factory reset is performed on the device.
//             * When a device has multiple users (available on certain devices running Android 4.2 or higher),
//             * each user appears as a completely separate device, so the ANDROID_ID value is unique to each user.
//             */
//            String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID); // or null
//
//            if (androidId != null) {
//                checkRegistrationContinue(androidId);
//            } else {
//                String macAddress = getMacAddress();
//
//                if (macAddress != null) {
//                    checkRegistrationContinue(macAddress);
//                } else {
//                    tvDeviceId.setText(localized_no_id_is_available);
//                }
//            }
//        }
    }

//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                new AlertDialog.Builder(this)
//                        .setMessage(localized_provide_device_id)
//                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                ActivityCompat.requestPermissions(MainActivity.this, new String[] { READ_PHONE_STATE }, REQUEST_PERMISSION_READ_PHONE_STATE);
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, null)
//                        .show();
//            } else {
//                permissionResultAction();
//            }
//        } else {
//            permissionResultAction();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSION_READ_PHONE_STATE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED ) {
//                        permissionResultAction();
//                    }
//                }
////                else {
////                    new AlertDialog.Builder(this)
////                            .setMessage(someFeatures)
////                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
////                                public void onClick(DialogInterface dialog, int which) {
////                                    dialog.dismiss();
////                                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {READ_PHONE_STATE}, REQUEST_PERMISSION_READ_PHONE_STATE);
////                                }
////                            })
////                            .show();
////                }
//                break;
//
//            default:
//                onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    private void permissionResultAction() {
//        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        deviceImei = tm.getDeviceId(); // or null
//    }
//
//    private void checkRegistrationContinue(String deviceId) {
//        boolean isRegisteredApk = MyPrefs.getBoolean(MainActivity.this, MyPrefs.PREF_REGISTERED_APK);
//        MyPrefs.setString(MainActivity.this, PREF_DEVICE_ID, deviceId);
//
//        if (isRegisteredApk) {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            if (MyUtils.isNetworkAvailable()) {
//                ToSendDeviceId toSendDeviceId = new ToSendDeviceId(this);
//                toSendDeviceId.execute(deviceId);
//            } else {
//                tvDeviceId.setText(deviceId);
//            }
//        }
//    }
//
//    public static String getMacAddress() {
//        try {
//            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for (NetworkInterface nif : all) {
//
//                Log.e("myLogs: ", "-------- nif.getName(): " + nif.getName());
//
//                if (!nif.getName().equalsIgnoreCase("wlan0") && !nif.getName().equalsIgnoreCase("eth1")) {
//                    continue;
//                }
//
//                byte[] macBytes = nif.getHardwareAddress();
//                if (macBytes == null) {
////                    return "";
//                    continue;
//                }
//
//                StringBuilder res1 = new StringBuilder();
//                for (byte b : macBytes) {
//                    res1.append(String.format("%02X:",b));
////                    res1.append(Integer.toHexString(b & 0xFF)).append(":");
//                }
//
//                if (res1.length() > 0) {
//                    res1.deleteCharAt(res1.length() - 1);
//                }
//
//                Log.e("myLogs: ", "-------- res1.toString(): " + res1.toString());
//                return res1.toString();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
