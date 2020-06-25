package am.gtest.vortex.support;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import am.gtest.vortex.R;

public class MyDialogs {

    public static void showOK(Context ctx, String message) {
        try {
            new AlertDialog.Builder(ctx)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
