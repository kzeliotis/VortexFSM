package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Spinner;

import java.util.Objects;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentsActivity;
import dc.gtest.vortex.data.StatusesData;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_STATUSES;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_STATUSES;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_STATUSES;
import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;

public class GetStatuses extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private boolean all = false;
    private boolean refresh = false;

    public GetStatuses(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {

        String _all = params[0];

        if(params.length > 1){
            refresh = Objects.equals(params[1], "Refresh");
        }


        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        if(_all.equals("1")){
            all = true;
            apiUrl = baseHostUrl + API_GET_STATUSES + "?All=1&UserId=" + MyPrefs.getString(MyPrefs.PREF_USERID, "0");
        } else {
            all = false;
            apiUrl = baseHostUrl + API_GET_STATUSES;
        }


        try {
            Bundle bundle = MyApi.get(apiUrl, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(refresh){
            fillDatasets();
        }

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {
       // MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no_body_for_get_request", responseCode, responseMessage, responseBody);

        if (responseCode == 200 && responseBody != null && !responseBody.isEmpty() && !refresh) {

            fillDatasets();

        }
    }

    private void fillDatasets(){
        if(all) {
            MyPrefs.setString(PREF_DATA_ALL_STATUSES, responseBody);
        } else {
            MyPrefs.setString(PREF_DATA_STATUSES, responseBody);
        }

        StatusesData.generate(responseBody, all);

        if(all){
            Spinner spStatusFilter = ((AppCompatActivity) ctx).findViewById(R.id.spStatusFilter);

            if (spStatusFilter != null) {
                AssignmentsActivity.setAdapterOnSpinner(ctx, spStatusFilter);
            }
        } else {

        }
    }
}
