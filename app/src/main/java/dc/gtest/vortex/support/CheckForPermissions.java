package dc.gtest.vortex.support;

public class CheckForPermissions {

////    public final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
//    private static final int REQUEST_READ_PHONE_STATE_FOR_SEND_DATA = 1;
//    private final Activity activity;
//    private final TextView tvDeviceId;
//
//    public CheckForPermissions(Activity activity) {
//        this.activity = activity;
//        tvDeviceId = (TextView) activity.findViewById(R.id.tvDeviceId);
//    }
//
//    public void checkForPermissionsForSendData() {
//
//        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
//                showMessageOKCancel("You need to provide device ID to be able to use the app.",
//                        (dialog, which) -> ActivityCompat.requestPermissions(activity,
//                                new String[]{Manifest.permission.READ_PHONE_STATE},
//                                REQUEST_READ_PHONE_STATE_FOR_SEND_DATA));
//            } else {
//                ActivityCompat.requestPermissions(activity,
//                        new String[]{Manifest.permission.READ_PHONE_STATE},
//                        REQUEST_READ_PHONE_STATE_FOR_SEND_DATA);
//            }
//        } else {
//            TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//            String deviceID = tm.getDeviceId();
//
//            if (MyUtils.isNetworkAvailable()) {
//                ToSendDeviceId toSendDeviceId = new ToSendDeviceId(activity);
//                toSendDeviceId.execute(deviceID);
//            } else {
//                tvDeviceId.setText(deviceID);
//            }
//        }
//    }
//
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(activity)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }
}
