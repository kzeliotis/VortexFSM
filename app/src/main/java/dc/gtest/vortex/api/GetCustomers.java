package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.CompaniesActivity;
import dc.gtest.vortex.activities.CompanyDetailsActivity;
import dc.gtest.vortex.activities.SearchProductsActivity;
import dc.gtest.vortex.data.CompaniesData;
import dc.gtest.vortex.data.ProjectsData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_SEARCH_CUSTOMERS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.COMPANIES_LIST;
import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_COMPANY;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PROJECT;
import static dc.gtest.vortex.support.MyLocalization.localized_no_company_is_available;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class GetCustomers extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final boolean isForNewAssignment;
    private final String CustomerId;
    private final String ProjectId;


    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public GetCustomers(Context ctx, boolean isForNewAssignment, String CustomerId, String ProjectId) {

        this.ctx = ctx;


        this.isForNewAssignment = isForNewAssignment;
        this.CustomerId = CustomerId;
        this.ProjectId = ProjectId;

    }

    @Override
    protected void onPreExecute() {
        if (ctx != null) {
            mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_SEARCH_CUSTOMERS;

        postBody =
                "{\n" +
                "  \"Company\": \"" + params[0] + "\",\n" +
                "  \"Phone\": \"" + params[1] + "\",\n" +
                "  \"Address\": \"" + params[2] + "\",\n" +
                "  \"ProjectDescription\": \"" + params[3] + "\",\n" +
                "  \"VATNumber\": \"" + params[4] + "\",\n" +
                "  \"CustomerId\": \"" + CustomerId + "\",\n" +
                "  \"UserId\": \"" + MyPrefs.getString(MyPrefs.PREF_USERID, "0") + "\",\n" +
                "  \"ExcludeFromAssignments\": \"" + (!ProjectId.isEmpty() && isForNewAssignment ? "1" : "0") + "\",\n" +
                "  \"ProjectId\": \"" + ProjectId + "\"\n" +
                "}";

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (responseCode == 200 && responseBody != null) {
            CompaniesData.generate(responseBody, CustomerId);
            if (ProjectId.length()> 0){
                ProjectsData.generate(ProjectId);
            }
        }

        return responseBody;

    }

    @Override
    protected void onPostExecute(String responseBody) {
        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }


        if (CustomerId == ""){
            if (COMPANIES_LIST.size() > 0) {
                Intent intent = new Intent(ctx, CompaniesActivity.class);
                intent.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, isForNewAssignment);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ctx.startActivity(intent);

                if (isForNewAssignment) {
                    ((AppCompatActivity) ctx).finish(); // finish activity to go back to new assignment
                }
            } else {
                Toast toast = Toast.makeText(ctx, localized_no_company_is_available, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else {
            if (SELECTED_COMPANY.getCompanyName() != ""){
                if (ProjectId == "") {
                    Intent intent = new Intent(ctx, CompanyDetailsActivity.class);
                    intent.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, isForNewAssignment);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);

                    if (isForNewAssignment) {
                        NEW_ASSIGNMENT.setCustomerId(SELECTED_COMPANY.getCompanyId());
                        NEW_ASSIGNMENT.setCustomerName(SELECTED_COMPANY.getCompanyName());
                        NEW_ASSIGNMENT.setProjectId(""); // clear selected project when selecting customer
                        NEW_ASSIGNMENT.setProjectDescription("");
                        NEW_ASSIGNMENT.setProductId("");  // clear selected product when selecting customer
                        NEW_ASSIGNMENT.setProjectProductId("");
                        NEW_ASSIGNMENT.setProductDescription("");
                        String ctxName = ctx.toString();
                        if(!ctxName.contains("NewAssignmentActivity")){
                            ((AppCompatActivity) ctx).finish(); // finish activity to go back to new assignment
                        }
                    }
                } else {
                    if (SELECTED_PROJECT.getProjectDescription() != ""){
                        Intent intent = new Intent(ctx, SearchProductsActivity.class);
                        intent.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, isForNewAssignment);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);

                        if (isForNewAssignment) {
                            NEW_ASSIGNMENT.setProjectId(SELECTED_PROJECT.getProjectId());
                            NEW_ASSIGNMENT.setProjectDescription(SELECTED_PROJECT.getProjectDescription());
                            NEW_ASSIGNMENT.setProductId("");        // clear selected product when selecting project
                            NEW_ASSIGNMENT.setProjectProductId("");
                            NEW_ASSIGNMENT.setProductDescription("");
                            ((AppCompatActivity) ctx).finish(); // finish activity to go back to new assignment
                        }
                    }

                }

            }
        }

    }
}
