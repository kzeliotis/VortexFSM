package dc.gtest.vortex.support;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;

import org.json.JSONObject;

import dc.gtest.vortex.R;

public class MyErrorAlert {

    public static void init(Context ctx, String dataFromApi, int responseCode, String message, boolean finishActivity) {
        if (dataFromApi != null) {
            try {
                JSONObject jObjectDataFromApi = new JSONObject(dataFromApi);

                if (jObjectDataFromApi.has("message")) {
                    MyErrorAlert.show(ctx, "004", responseCode, jObjectDataFromApi.optString("message"), finishActivity);
                } else {
                    MyErrorAlert.show(ctx, "003", responseCode, message, finishActivity);
                }
            } catch (Exception e) {
                e.printStackTrace();

                MyErrorAlert.show(ctx, "002", responseCode, message, finishActivity);
            }
        } else {
            MyErrorAlert.show(ctx, "001", responseCode, message, finishActivity);
        }
    }

    public static void show(final Context ctx, String errorCode, int responseCode, String message, final boolean finishActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getString(R.string.error) + " " + errorCode + "-" + responseCode);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.setOnDismissListener(dialog -> {
            if (finishActivity) {
                ((Activity) ctx).finish();
            }
        });
    }
}
