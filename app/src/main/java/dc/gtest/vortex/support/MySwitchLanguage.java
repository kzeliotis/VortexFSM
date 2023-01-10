package dc.gtest.vortex.support;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import dc.gtest.vortex.R;

import static dc.gtest.vortex.support.MyGlobals.CONST_EN;
import static dc.gtest.vortex.support.MyGlobals.CONST_GR;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;

public class MySwitchLanguage {

    //private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private final Context ctx;

    public MySwitchLanguage(Context ctx) {
        this.ctx = ctx;
    }

    public String mySwitchLanguage(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        String language = "";
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbEnglish:
                if (checked) {
                    MyPrefs.setString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);
                    language = "en";
                }
                    break;
            case R.id.rbGreek:
                if (checked) {
                    MyPrefs.setString(PREF_KEY_SELECTED_LANGUAGE, CONST_GR);
                    language = "gr";
                }
                    break;
        }

        MyLocalization.setupLanguage(ctx);

        return language;
    }

    // Moved to MyLocalization class. Delete if no problem.
//    public static void saveNewLanguage(Context ctx, View view) {
//
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.rbEnglish:
//                if (checked) {
//                    MyPrefs.setString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);
//                }
//                break;
//            case R.id.rbGreek:
//                if (checked) {
//                    MyPrefs.setString(PREF_KEY_SELECTED_LANGUAGE, CONST_GR);
//                }
//                break;
//        }
//
//        MyLocalization.setupLanguage(ctx);
//    }
}