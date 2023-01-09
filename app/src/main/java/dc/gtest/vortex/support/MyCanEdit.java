package dc.gtest.vortex.support;

import android.view.Gravity;
import android.widget.Toast;

import dc.gtest.vortex.application.MyApplication;

import static dc.gtest.vortex.support.MyGlobals.resendZoneMeasurements;
import static dc.gtest.vortex.support.MyLocalization.localized_check_in_first;
import static dc.gtest.vortex.support.MyLocalization.localized_no_changes_allowed;
import static dc.gtest.vortex.support.MyLocalization.localized_start_travel_first;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_TRAVEL_STARTED;

public class MyCanEdit {

    public static boolean canEdit(String assignmentId) {
        boolean isTravelStarted = MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false);
        boolean isCheckedIn = MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false);
        boolean isCheckedOut = MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false);

        if (!isTravelStarted) {
            showToast(localized_start_travel_first);
            return false;
        } else if (!isCheckedIn){
            showToast(localized_check_in_first);
            return false;
        } else if (isCheckedOut && !resendZoneMeasurements) {
            showToast(localized_no_changes_allowed);
            return false;
        } else {
            return true;
        }
    }

    private static void showToast(String toastMessage) {
        Toast toast = Toast.makeText(MyApplication.getContext(), toastMessage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
