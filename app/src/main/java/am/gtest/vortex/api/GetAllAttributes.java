package am.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import am.gtest.vortex.adapters.AllAttributesRvAdapter;
import am.gtest.vortex.data.AllAttributesData;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_GET_ALL_ATTRIBUTES;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_ATTRIBUTES;

public class GetAllAttributes extends AsyncTask<String, Void, String > {

    private final AllAttributesRvAdapter allAttributesRvAdapter;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public GetAllAttributes(AllAttributesRvAdapter allAttributesRvAdapter) {
        this.allAttributesRvAdapter = allAttributesRvAdapter;
    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_ALL_ATTRIBUTES;

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
//        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for GET request", responseCode, responseMessage, responseBody);

        if (responseCode == 200 && responseBody != null) {
            MyPrefs.setString(PREF_DATA_ALL_ATTRIBUTES, responseBody);
        }

        AllAttributesData.generate();

        if (allAttributesRvAdapter != null) {
            allAttributesRvAdapter.notifyDataSetChanged();
        }
    }
}