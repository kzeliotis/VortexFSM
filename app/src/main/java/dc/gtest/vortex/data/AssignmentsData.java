package dc.gtest.vortex.data;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import dc.gtest.vortex.activities.LoginActivity;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.models.AssignmentModel;
import dc.gtest.vortex.models.AttachmentModel;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.CALENDAR_EVENTS;
import static dc.gtest.vortex.support.MyLocalization.localized_no_available_mobile_slots;
import static dc.gtest.vortex.support.MyLocalization.localized_wrong_credentials;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NOTES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_IS_LOGGED_IN;

public class AssignmentsData {



    public static void generate(Context ctx) {

        ASSIGNMENTS_LIST.clear();

        String assignmentData = MyPrefs.getString(PREF_DATA_ASSIGNMENTS, "");

        if (!assignmentData.isEmpty()) {
            try {
                JSONArray jArrayAssignments = new JSONArray(assignmentData);

                AssignmentModel assignmentModel;
                Location locationA;
                Location locationB;

                float distance;

                for (int i = 0; i < jArrayAssignments.length(); i++) {
                    JSONObject oneObject = jArrayAssignments.getJSONObject(i);

                    assignmentModel = new AssignmentModel();

                    assignmentModel.setProjectDescription(MyJsonParser.getStringValue(oneObject, "ProjectDescription", ""));
                    assignmentModel.setProductDescription(MyJsonParser.getStringValue(oneObject, "ProductDescription", ""));
                    assignmentModel.setServiceDescription(MyJsonParser.getStringValue(oneObject, "ServiceDescription", ""));

                    String assignmentStartDateString = MyJsonParser.getStringValue(oneObject, "DateStart", "");
                    assignmentModel.setStartDateTime(MyDateTime.getServerDateFromAssignmentDate(assignmentStartDateString));
                    assignmentModel.setStartDate(MyDateTime.getAppDateFromAssignmentDate(assignmentStartDateString));

                    String assignmentTime = "";
                    String timeTo = "";

                    try {
                        String dateStart = MyJsonParser.getStringValue(oneObject, "DateStart", "");
                        String dateEnd = MyJsonParser.getStringValue(oneObject, "DateEnd", "");
                        assignmentModel.setDateStart(dateStart);
                        assignmentModel.setDateEnd(dateEnd);

                        String month = dateStart.substring(0, 2);
                        String day = dateStart.substring(3, 5);

                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.ENGLISH);
//                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Long dateEndMillis = sdf.parse(dateStart).getTime();

                        Calendar date = Calendar.getInstance();
//                        date.setTimeZone(TimeZone.getTimeZone("UTC"));
//                        long currentUtcMillis = date.getTimeInMillis() + 3 * 60 * 60 * 1000;
                        long currentUtcMillis = date.getTimeInMillis();

                        Long diff = dateEndMillis - currentUtcMillis;

                        timeTo = diff / 1000 / 60 + " min";

                        dateStart = dateStart.substring(dateStart.length() - 5);

                        dateEnd = dateEnd.substring(dateEnd.length() - 5);
                        assignmentTime = day + "/" + month + " " + dateStart + " - " + dateEnd;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    assignmentModel.setAssignmentTime(assignmentTime);
                    assignmentModel.setTimeTo(timeTo);
                    assignmentModel.setContact(MyJsonParser.getStringValue(oneObject, "ProjectContact", ""));

                    assignmentModel.setAddress(
                            MyJsonParser.getStringValue(oneObject, "ProjectAddress", "") + ", " +
                            MyJsonParser.getStringValue(oneObject,"ProjectCity", ""));

                    String projectLat = MyJsonParser.getStringValue(oneObject, "ProjectLat", "");
                    String projectLon = MyJsonParser.getStringValue(oneObject, "ProjectLon", "");
                    assignmentModel.setProjectLat(projectLat);
                    assignmentModel.setProjectLon(projectLon);

                    distance = -1;

                    if (!MyPrefs.getString(PREF_CURRENT_LAT, "").isEmpty() &&
                            !MyPrefs.getString(PREF_CURRENT_LNG, "").isEmpty() &&
                            !projectLat.isEmpty() && !projectLon.isEmpty()) {

                        try {
                            locationA = new Location("point A");
                            locationA.setLatitude(Double.parseDouble(MyPrefs.getString(PREF_CURRENT_LAT, "")));
                            locationA.setLongitude(Double.parseDouble(MyPrefs.getString(PREF_CURRENT_LNG, "")));

                            locationB = new Location("point B");
                            locationB.setLatitude(Double.parseDouble(projectLat));
                            locationB.setLongitude(Double.parseDouble(projectLon));

                            distance = locationA.distanceTo(locationB) / 1000;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    assignmentModel.setDistance(distance);
                    assignmentModel.setCabLat(projectLat);
                    assignmentModel.setCabLng(projectLon);
                    assignmentModel.setProjectId(MyJsonParser.getStringValue(oneObject, "ProjectId", ""));
                    assignmentModel.setAssignmentId(MyJsonParser.getStringValue(oneObject, "AssignmentId", ""));
                    assignmentModel.setMasterAssignment(MyJsonParser.getStringValue(oneObject, "MasterAssignment", ""));
                    assignmentModel.setProblem(MyJsonParser.getStringValue(oneObject, "Problem", ""));
                    assignmentModel.setPhone(MyJsonParser.getStringValue(oneObject, "ProjectTel", ""));
                    assignmentModel.setMobile(MyJsonParser.getStringValue(oneObject, "ProjectMobile", ""));
//                    assignmentModel.setProducts(MyJsonParser.getStringValue(oneObject, "ProjectProducts", ""));               // moved to a separate API
                    assignmentModel.setAssignmentType(MyJsonParser.getStringValue(oneObject, "AssignmentType", ""));
                    assignmentModel.setCustomerName(MyJsonParser.getStringValue(oneObject, "CustomerName", ""));
                    assignmentModel.setCustomerBusiness(MyJsonParser.getStringValue(oneObject, "CustomerBusiness", ""));
                    assignmentModel.setCustomerVatNumber(MyJsonParser.getStringValue(oneObject, "CustomerVatNumber", ""));
                    assignmentModel.setCustomerRevenue(MyJsonParser.getStringValue(oneObject, "CustomerRevenue", ""));
                    assignmentModel.setSorting(MyJsonParser.getStringValue(oneObject, "Sorting", ""));
                    assignmentModel.setStatusName(MyJsonParser.getStringValue(oneObject, "Status", "").toUpperCase());
                    assignmentModel.setStatusId(MyJsonParser.getStringValue(oneObject, "StatusId", ""));
                    assignmentModel.setStatusColor(MyJsonParser.getStringValue(oneObject, "StatusColor", ""));
                    assignmentModel.setCustomFields(MyJsonParser.getStringValue(oneObject, "CustomFieldsString", ""));
                    assignmentModel.setPickingList(MyJsonParser.getStringValue(oneObject, "PickingList", ""));
                    assignmentModel.setCommentsSolution(MyJsonParser.getStringValue(oneObject, "Solution", ""));
                    assignmentModel.setNotes(MyJsonParser.getStringValue(oneObject, "Notes", ""));
                    assignmentModel.setProjectAddress(MyJsonParser.getStringValue(oneObject, "ProjectAddress", ""));
                    assignmentModel.setProjectCity(MyJsonParser.getStringValue(oneObject, "ProjectCity", ""));
                    assignmentModel.setprojectProductId(MyJsonParser.getStringValue(oneObject,"ProjectProductId", "0"));

                    String assignmentId = assignmentModel.getAssignmentId();
                    if (! MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false)){
                        MyPrefs.setStringWithFileName(PREF_FILE_NOTES_FOR_SHOW, assignmentId, assignmentModel.getNotes());
                    }

                    assignmentModel.setChargedAmount(MyJsonParser.getStringValue(oneObject, "Charge", ""));
                    assignmentModel.setPaidAmount(MyJsonParser.getStringValue(oneObject, "Payment", ""));
                    assignmentModel.setMandatoryTasks(MyJsonParser.getJsonArrayValue(oneObject, "ServiceSteps"));
                    assignmentModel.setSignatureName(MyJsonParser.getStringValue(oneObject, "SignatureName", "")) ;
                    assignmentModel.setProposedCheckOutStatus(MyJsonParser.getStringValue(oneObject, "ProposedCheckOutStatus", "0"));
                    assignmentModel.setInstallationWarning(MyJsonParser.getStringValue(oneObject, "InstallationWarning", ""));
                    assignmentModel.setMandatoryZoneMeasurementsService(MyJsonParser.getStringValue(oneObject, "MandatoryZoneMeasurementsService", "0"));
                    assignmentModel.setResourceId(MyJsonParser.getStringValue(oneObject, "ResourceId", ""));
                    assignmentModel.setAdditionalTechnicians(MyJsonParser.getStringValue(oneObject, "AdditionalTechnicians", ""));
                    assignmentModel.setContract(MyJsonParser.getStringValue(oneObject, "Contract", ""));
                    assignmentModel.setProjectInstallationDescription(MyJsonParser.getStringValue(oneObject, "ProjectInstallationDescription", ""));
                    assignmentModel.setMinimumPayment(MyJsonParser.getStringValue(oneObject, "MinimumPayment", "0"));
                    assignmentModel.setMaximumPayment(MyJsonParser.getStringValue(oneObject, "MaximumPayment", "0"));
                    boolean lockStatusChange = MyJsonParser.getBooleanValue(oneObject,"LockStatusChange", false);
                    assignmentModel.setLockStatusChange(lockStatusChange ? "1" : "0");
                    assignmentModel.setCorrelatedStatuses(MyJsonParser.getStringValue(oneObject, "CorrelatedStatuses", ""));

                    String assignmentid = assignmentModel.getAssignmentId();
                    String Problem = assignmentModel.getProblem();
                    if( assignmentid.equals("-1") &&  Problem.equals("-1")){
                        Toast.makeText(MyApplication.getContext(), localized_no_available_mobile_slots, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if( assignmentid.equals("-2") &&  Problem.equals("-2")){
                        Toast.makeText(MyApplication.getContext(), localized_wrong_credentials, Toast.LENGTH_LONG).show();
                        MyPrefs.setString(PREF_DATA_ASSIGNMENTS, "");
                        MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, false);
                        ((Activity) ctx).finishAffinity();

                        Intent intent = new Intent(ctx, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);
                        return;
                    }

                    JSONArray jArrayAttachments = MyJsonParser.getJsonArrayValue(oneObject, "Attachments");
                    List<AttachmentModel> AttachmentsList = new ArrayList<>();
                    for (int a = 0; a < jArrayAttachments.length(); a++){
                        JSONObject jObjectOneAttachment = jArrayAttachments.getJSONObject(a);

                        AttachmentModel attachmentModel = new AttachmentModel();

                        attachmentModel.setObjectId(MyJsonParser.getStringValue(jObjectOneAttachment, "ObjectId", "0"));
                        attachmentModel.setObjectType(MyJsonParser.getStringValue(jObjectOneAttachment, "ObjectType", ""));
                        attachmentModel.setFilename(MyJsonParser.getStringValue(jObjectOneAttachment, "Filename", ""));
                        attachmentModel.setCloudFileURL(MyJsonParser.getStringValue(jObjectOneAttachment, "CloudFileURL", ""));
                        attachmentModel.setAttachmentId(MyJsonParser.getStringValue(jObjectOneAttachment, "AttachmentId", "0"));
                        attachmentModel.setBlobAttachmentId(MyJsonParser.getStringValue(jObjectOneAttachment, "BlobAttachmentId", "0"));

                        AttachmentsList.add(attachmentModel);
                    }

                    assignmentModel.setAttachments(AttachmentsList);

                    ASSIGNMENTS_LIST.add(assignmentModel);


                    Date assignmentStartDate = MyDateTime.getDateFromAssignmentDate(assignmentStartDateString);

                    if (assignmentStartDate != null) {
                        CALENDAR_EVENTS.add(assignmentStartDate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(ASSIGNMENTS_LIST, (a, b) -> a.getSorting().compareTo(b.getSorting()));

//        Log.e("myLogs: AssignmentsData", "----------- ASSIGNMENTS_LIST: \n" + ASSIGNMENTS_LIST);
    }
}
