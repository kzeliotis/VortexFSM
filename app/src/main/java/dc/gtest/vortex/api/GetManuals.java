package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.widget.Toast;

import dc.gtest.vortex.R;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.data.ManualsData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.MANUALS_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_no_manual;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_MANUALS;
import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;

public class GetManuals extends AsyncTask<String, Void, String > {

    private int responseCode;
    private final boolean ShowToast;
    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    public GetManuals(Context ctx, boolean ShowToast) {
        this.ctx = ctx;
        this.ShowToast = ShowToast;
    }

    @Override
    protected String doInBackground(String... params) {
        String responseMessage = "";
        String responseBody = null;

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl+ "/Vortex.svc/GetManuals?UserId=" + PREF_USERID;

        try {
            Bundle bundle = MyApi.get(apiUrl);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {


        if ( responseBody != null ) {

            MyPrefs.setString(PREF_DATA_MANUALS, responseBody);

            ManualsData.generate();

            if (MANUALS_LIST.size() == 0 && ShowToast) {
                Toast toast = Toast.makeText(MyApplication.getContext(), localized_no_manual, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            RecyclerView rvManuals = ((AppCompatActivity)ctx).findViewById(R.id.rvManuals);

            if (rvManuals != null && rvManuals.getAdapter() != null) {
                rvManuals.getAdapter().notifyDataSetChanged();
            }

//            try {
//                JSONObject jObjectDataFromApi = new JSONObject(dataFromApi);
//
//                //Log.e(LOG_TAG, "jObjectDataFromApi: \n" + jObjectDataFromApi.toString(2));
//
//                if (jObjectDataFromApi.optJSONArray("manuals") != null && jObjectDataFromApi.optJSONArray("manuals").length() > 0) {
//                    JSONArray jArrayDataFromApi = jObjectDataFromApi.getJSONArray("manuals");
//
//                    for (int i = 0; i < jArrayDataFromApi.length(); i++) {
//                        JSONObject oneObjectManuals = jArrayDataFromApi.getJSONObject(i);
//                        String manualUrl = oneObjectManuals.getString("ManualUrl");
//
//                        ToDownloadManuals toDownloadManuals = new ToDownloadManuals(ctx);
//                        toDownloadManuals.execute(manualUrl);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }
}
