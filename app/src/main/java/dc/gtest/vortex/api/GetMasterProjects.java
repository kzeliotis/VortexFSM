package dc.gtest.vortex.api;

import static dc.gtest.vortex.api.MyApi.API_GET_ASSIGNMENT_TYPES;
import static dc.gtest.vortex.api.MyApi.API_GET_MASTER_PROJECTS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENT_TYPES;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_MASTER_PROJECTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_ALL_MASTER_PROJECTS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.data.AssignmentTypesData;
import dc.gtest.vortex.data.MasterProjectData;
import dc.gtest.vortex.support.MyPrefs;

public class GetMasterProjects extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    //private final Context ctx;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private Context ctx;

    public GetMasterProjects(Context ctx) {
        this.ctx = ctx;

    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_MASTER_PROJECTS + params[0];

        try {
            Bundle bundle = MyApi.get(apiUrl);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {
//        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, MyGlobals.CONST_NO_BODY_FOR_GET_REQUEST, responseCode, responseMessage, responseBody);

        if (responseCode == 200 && responseBody != null && !responseBody.isEmpty()) {
            if(MyPrefs.getBoolean(PREF_SHOW_ALL_MASTER_PROJECTS, false)){
                MyPrefs.setString(PREF_DATA_MASTER_PROJECTS, responseBody);
            }
            MasterProjectData.generate(responseBody);

            RecyclerView rvMasterProjects = ((Activity)ctx).findViewById(R.id.rvMasterProjects);

            if(rvMasterProjects != null && rvMasterProjects.getAdapter() != null){
                rvMasterProjects.getAdapter().notifyDataSetChanged();
            }
        }
    }
}
