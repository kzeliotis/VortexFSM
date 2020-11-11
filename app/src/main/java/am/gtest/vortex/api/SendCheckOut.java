package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import am.gtest.vortex.R;
import am.gtest.vortex.activities.AssignmentsActivity;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_UPDATE_ASSIGNMENT;
import static am.gtest.vortex.api.MyApi.API_UPDATE_RETURN_TO_BASE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static am.gtest.vortex.support.MyLocalization.localized_error_synchronizing;
import static am.gtest.vortex.support.MyLocalization.localized_successfully_checked_out;
import static am.gtest.vortex.support.MyLocalization.localized_successfully_return_to_base;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_OUT_DATA_TO_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_RETURN_TO_BASE_DATA_TO_SYNC;

public class SendCheckOut extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final String assignmentId;
    private final boolean showProgressAndToast;
    private final boolean returnToBase;


    private int responseCode;

    private boolean isCheckingOut = false;

    public SendCheckOut(Context ctx, String assignmentId, boolean showProgressAndToast, boolean ReturnToBase) {
        this.ctx = ctx;
        this.assignmentId = assignmentId;
        this.showProgressAndToast = showProgressAndToast;
        this.returnToBase = ReturnToBase;
    }

    @Override
    protected void onPreExecute() {

        isCheckingOut = true;

        if (showProgressAndToast) {
            mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String responseMessage = "";
        String responseBody = "";
        String apiUrl;

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        if (returnToBase) {
            apiUrl = baseHostUrl + API_UPDATE_RETURN_TO_BASE;
        } else {
            apiUrl = baseHostUrl + API_UPDATE_ASSIGNMENT;
        }


        String postBody;
        if (returnToBase) {
            postBody = MyPrefs.getStringWithFileName(PREF_FILE_RETURN_TO_BASE_DATA_TO_SYNC, assignmentId, "");
        } else {
            postBody = MyPrefs.getStringWithFileName(PREF_FILE_CHECK_OUT_DATA_TO_SYNC, assignmentId, "");
        }


        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        Activity activity = (Activity) ctx;

        Button btnCheckOut = activity.findViewById(R.id.btnCheckOut);
        Button btnToConsumables = activity.findViewById(R.id.btnToConsumables);
        Button btnToServices = activity.findViewById(R.id.btnToServices);
        EditText etCommentsSolution = activity.findViewById(R.id.etCommentsSolution);
        EditText etNotes = activity.findViewById(R.id.etNotes);
        Button btnTakePhoto = activity.findViewById(R.id.btnTakePhoto);
        EditText etChargedAmount = activity.findViewById(R.id.etChargedAmount);
        EditText etPaidAmount = activity.findViewById(R.id.etPaidAmount);
        TextView tvSignatureImage = activity.findViewById(R.id.tvSignatureImage);
        Spinner spStatus = activity.findViewById(R.id.spStatus);
        FrameLayout flSignatureImage = activity.findViewById(R.id.flSignatureImage);
        RecyclerView rvMandatoryTasks = activity.findViewById(R.id.rvMandatoryTasks);
        LinearLayout llPayment = activity.findViewById(R.id.llPayment);

        if (responseBody != null && responseBody.equals("1") ) {

            if(returnToBase) {
                MyPrefs.removeStringWithFileName(PREF_FILE_RETURN_TO_BASE_DATA_TO_SYNC, assignmentId);
            } else {
                MyPrefs.removeStringWithFileName(PREF_FILE_CHECK_OUT_DATA_TO_SYNC, assignmentId);
            }


            MyPrefs.clearOneAssignmentData(assignmentId);
            MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, true);
            if(btnCheckOut != null) { // "If" added because pressing sync from the AssignmentActivity would return null and the app would crash
                btnCheckOut.setEnabled(false);
                spStatus.setEnabled(false);
                etCommentsSolution.setEnabled(false);
                etNotes.setEnabled(false);
                btnTakePhoto.setEnabled(false);
                etChargedAmount.setEnabled(false);
                etPaidAmount.setEnabled(false);
                tvSignatureImage.setEnabled(false);

                rvMandatoryTasks.setBackgroundResource(R.drawable.rounded_layout_grey);
                llPayment.setBackgroundResource(R.drawable.rounded_layout_grey);
                flSignatureImage.setBackgroundResource(R.drawable.rounded_layout_white);

                // TODO check why true if grey or is not needed
                btnToConsumables.setEnabled(true);
                btnToConsumables.setBackgroundResource(R.drawable.rounded_button_grey);

                // TODO check why true if grey or is not needed
                btnToServices.setEnabled(true);
                btnToServices.setBackgroundResource(R.drawable.rounded_button_grey);
            }
            // the following section is for disabling mandatory tasks recycler view.
            // but we manage it by refreshing recycler view after clicking on check out.
            // delete if no problem appear.
//                    for (int i = 0; i < rvMandatoryTasks.getChildCount(); i++) {
//                        LinearLayout llMandatoryTasksContent = (LinearLayout) rvMandatoryTasks.getChildAt(i);
//                        llMandatoryTasksContent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_300));
//
//                        LinearLayout llMandatoryTasksTitleRow = (LinearLayout) llMandatoryTasksContent.getChildAt(0);
//                        llMandatoryTasksTitleRow.getChildAt(2).setEnabled(false);
//
//                        LinearLayout llMandatoryTasksMeasurableRow = (LinearLayout) llMandatoryTasksContent.getChildAt(1);
//                        llMandatoryTasksMeasurableRow.getChildAt(1).setEnabled(false);
//                    }

            if (showProgressAndToast) {
                if (returnToBase) {
                    Toast.makeText(ctx,localized_successfully_return_to_base, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ctx, localized_successfully_checked_out, Toast.LENGTH_LONG).show();
                }

                activity.finishAffinity();
                Intent intent = new Intent(activity, AssignmentsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_DOWNLOAD_ALL_DATA, false);
                ctx.startActivity(intent);
            }
        } else {
            if (showProgressAndToast) {
                MyDialogs.showOK(ctx, localized_error_synchronizing+ "\n\n" + this.getClass().getSimpleName());
            }
        }

        isCheckingOut = false;
    }

    public boolean isCheckingOut() {
        return isCheckingOut;
    }
}
