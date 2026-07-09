package dc.gtest.vortex.data;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;
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
import dc.gtest.vortex.models.DetChildrenModel;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.CALENDAR_EVENTS;
import static dc.gtest.vortex.support.MyLocalization.localized_no_available_mobile_slots;
import static dc.gtest.vortex.support.MyLocalization.localized_wrong_credentials;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_TIME;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_TIME_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_OUT_TIME_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CHILDREN_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_TRAVEL_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NOTES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_IS_LOGGED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_TRAVEL_TIME;

public class AssignmentsData {



    public static void generate(Context ctx) {

        long totalStart = SystemClock.elapsedRealtime();
        long t = totalStart;

        ASSIGNMENTS_LIST.clear();
        CALENDAR_EVENTS.clear();

        String assignmentData = MyPrefs.getString(PREF_DATA_ASSIGNMENTS, "");

        String currentLatString = MyPrefs.getString(PREF_CURRENT_LAT, "");
        String currentLngString = MyPrefs.getString(PREF_CURRENT_LNG, "");

        boolean hasCurrentLocation = !currentLatString.isEmpty() && !currentLngString.isEmpty();
        double currentLat = 0;
        double currentLng = 0;
        float[] distanceResult = new float[1];

        if (hasCurrentLocation) {
            try {
                currentLat = Double.parseDouble(currentLatString);
                currentLng = Double.parseDouble(currentLngString);
            } catch (Exception e) {
                hasCurrentLocation = false;
            }
        }

        long nowMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.ENGLISH);


        if (!assignmentData.isEmpty()) {

            MyPrefs.PrefsBatch prefsBatch = MyPrefs.newBatch();

            try {

                long part = SystemClock.elapsedRealtime();

                JSONArray jArrayAssignments = new JSONArray(assignmentData);

                Log.e("AssignmentsPerf", "load array: " +
                        (SystemClock.elapsedRealtime() - part) + " ms");

                AssignmentModel assignmentModel;
                Location locationA;
                Location locationB;

                float distance;

                for (int i = 0; i < jArrayAssignments.length(); i++) {

                    long assignmentStart = SystemClock.elapsedRealtime();

                    part = SystemClock.elapsedRealtime();

                    JSONObject oneObject = jArrayAssignments.getJSONObject(i);

                    Log.e("AssignmentsPerf", "load object: " +
                            (SystemClock.elapsedRealtime() - part) + " ms");


                    assignmentModel = new AssignmentModel();

                    part = SystemClock.elapsedRealtime();

                    assignmentModel.setProjectDescription(oneObject.optString("ProjectDescription", ""));
                    assignmentModel.setProductDescription(oneObject.optString("ProductDescription", ""));
                    assignmentModel.setServiceDescription(oneObject.optString("ServiceDescription", ""));


                    Log.e("AssignmentsPerf", "basic fields: " + (SystemClock.elapsedRealtime() - part) + " ms");

                    part = SystemClock.elapsedRealtime();

                    String assignmentStartDateString = oneObject.optString("DateStart", "");
                    assignmentModel.setStartDateTime(MyDateTime.getServerDateFromAssignmentDate(assignmentStartDateString));
                    assignmentModel.setStartDate(MyDateTime.getAppDateFromAssignmentDate(assignmentStartDateString));

                    Log.e("AssignmentsPerf", "date fields: " + (SystemClock.elapsedRealtime() - part) + " ms");

                    String assignmentTime = "";
                    String timeTo = "";

                    try {
                        String dateStart = oneObject.optString("DateStart", "");
                        String dateEnd = oneObject.optString("DateEnd", "");
                        assignmentModel.setDateStart(dateStart);
                        assignmentModel.setDateEnd(dateEnd);

                        String month = dateStart.substring(0, 2);
                        String day = dateStart.substring(3, 5);

                        //SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.ENGLISH);
//                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        long dateEndMillis = sdf.parse(dateStart).getTime();

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
                    assignmentModel.setContact(oneObject.optString("ProjectContact", ""));

                    assignmentModel.setAddress(
                            oneObject.optString("ProjectAddress", "") + ", " +
                            oneObject.optString("ProjectCity", ""));

                    String projectLat = oneObject.optString("ProjectLat", "");
                    String projectLon = oneObject.optString("ProjectLon", "");
                    assignmentModel.setProjectLat(projectLat);
                    assignmentModel.setProjectLon(projectLon);

                    distance = -1;

//                    if (!currentLatString.isEmpty() &&
//                            !currentLngString.isEmpty() &&
//                            !projectLat.isEmpty() && !projectLon.isEmpty()) {
//
//                        try {
//                            locationA = new Location("point A");
//                            locationA.setLatitude(Double.parseDouble(currentLatString));
//                            locationA.setLongitude(Double.parseDouble(currentLngString));
//
//                            locationB = new Location("point B");
//                            locationB.setLatitude(Double.parseDouble(projectLat));
//                            locationB.setLongitude(Double.parseDouble(projectLon));
//
//                            distance = locationA.distanceTo(locationB) / 1000;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }

                    if (hasCurrentLocation && !projectLat.isEmpty() && !projectLon.isEmpty()) {
                        try {
                            Location.distanceBetween(
                                    currentLat,
                                    currentLng,
                                    Double.parseDouble(projectLat),
                                    Double.parseDouble(projectLon),
                                    distanceResult
                            );

                            distance = distanceResult[0] / 1000f;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    assignmentModel.setDistance(distance);
                    assignmentModel.setCabLat(projectLat);
                    assignmentModel.setCabLng(projectLon);
                    assignmentModel.setProjectId(oneObject.optString("ProjectId", ""));
                    String assigmentId = oneObject.optString("AssignmentId", "");
                    assignmentModel.setAssignmentId(assigmentId);
                    assignmentModel.setMasterAssignment(oneObject.optString("MasterAssignment", ""));
                    assignmentModel.setProblem(oneObject.optString("Problem", ""));
                    assignmentModel.setPhone(oneObject.optString("ProjectTel", ""));
                    assignmentModel.setMobile(oneObject.optString("ProjectMobile", ""));
//                    assignmentModel.setProducts(oneObject.optString("ProjectProducts", ""));               // moved to a separate API
                    assignmentModel.setAssignmentType(oneObject.optString("AssignmentType", ""));
                    assignmentModel.setCustomerName(oneObject.optString("CustomerName", ""));
                    assignmentModel.setCustomerBusiness(oneObject.optString("CustomerBusiness", ""));
                    assignmentModel.setCustomerBusinessTitle(oneObject.optString("CustomerBusinessTitle", ""));
                    assignmentModel.setCustomerVatNumber(oneObject.optString("CustomerVatNumber", ""));
                    assignmentModel.setCustomerRevenue(oneObject.optString("CustomerRevenue", ""));
                    assignmentModel.setSorting(oneObject.optString("Sorting", ""));
                    assignmentModel.setStatusName(oneObject.optString("Status", "").toUpperCase());
                    assignmentModel.setStatusId(oneObject.optString("StatusId", ""));
                    assignmentModel.setStatusColor(oneObject.optString("StatusColor", ""));
                    assignmentModel.setCustomFields(oneObject.optString("CustomFieldsString", ""));
                    assignmentModel.setPickingList(oneObject.optString("PickingList", ""));
                    assignmentModel.setCommentsSolution(oneObject.optString("Solution", ""));
                    assignmentModel.setNotes(oneObject.optString("Notes", ""));
                    assignmentModel.setProjectAddress(oneObject.optString("ProjectAddress", ""));
                    assignmentModel.setProjectCity(oneObject.optString("ProjectCity", ""));
                    assignmentModel.setprojectProductId(MyJsonParser.getStringValue(oneObject,"ProjectProductId", "0"));

                    String assignmentId = assignmentModel.getAssignmentId();

                    part = SystemClock.elapsedRealtime();

                    if (! MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false)){
                        prefsBatch.putString(PREF_FILE_NOTES_FOR_SHOW, assignmentId, assignmentModel.getNotes());
                    }

                    Log.e("AssignmentsPerf", "prefs access: " + (SystemClock.elapsedRealtime() - part) + " ms");

                    String startTravel = oneObject.optString("StartTravel", "");
                    String checkIn = oneObject.optString("CheckIn", "");
                    String checkOut = oneObject.optString("CheckOut", "");

                    if (!startTravel.isEmpty()){
                        prefsBatch.putString(assignmentId, PREF_START_TRAVEL_TIME, startTravel);
                        prefsBatch.putBoolean(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, true);
                    }

                    if (!checkIn.isEmpty()){
                        prefsBatch.putBoolean(PREF_FILE_IS_CHECKED_IN, assignmentId, true);
                        String chkIn = "CHECK IN" + System.lineSeparator() + checkIn;
                        prefsBatch.putString(PREF_CHECK_IN_TIME_FOR_SHOW, assignmentId, chkIn);
                        prefsBatch.putString(assignmentId, PREF_CHECK_IN_TIME, checkIn);
                    }

                    if (!checkOut.isEmpty()){
                        prefsBatch.putBoolean(PREF_FILE_IS_CHECKED_OUT, assignmentId, true);
                        String chkOut = "CHECK OUT" + System.lineSeparator() + checkOut;
                        prefsBatch.putString(PREF_CHECK_OUT_TIME_FOR_SHOW, assignmentId, chkOut);
                    }

                    assignmentModel.setChargedAmount(oneObject.optString("Charge", ""));
                    assignmentModel.setPaidAmount(oneObject.optString("Payment", ""));
                    assignmentModel.setMandatoryTasks(MyJsonParser.getJsonArrayValue(oneObject, "ServiceSteps"));
                    assignmentModel.setSignatureName(oneObject.optString("SignatureName", "")) ;
                    assignmentModel.setProposedCheckOutStatus(oneObject.optString("ProposedCheckOutStatus", "0"));
                    assignmentModel.setInstallationWarning(oneObject.optString("InstallationWarning", ""));
                    assignmentModel.setMandatoryZoneMeasurementsService(oneObject.optString("MandatoryZoneMeasurementsService", "0"));
                    assignmentModel.setResourceId(oneObject.optString("ResourceId", ""));
                    assignmentModel.setAdditionalTechnicians(oneObject.optString("AdditionalTechnicians", ""));
                    assignmentModel.setContract(oneObject.optString("Contract", ""));
                    assignmentModel.setProjectInstallationDescription(oneObject.optString("ProjectInstallationDescription", ""));
                    assignmentModel.setMinimumPayment(oneObject.optString("MinimumPayment", "0"));
                    assignmentModel.setMaximumPayment(oneObject.optString("MaximumPayment", "0"));
                    boolean lockStatusChange = MyJsonParser.getBooleanValue(oneObject,"LockStatusChange", false);
                    assignmentModel.setLockStatusChange(lockStatusChange ? "1" : "0");
                    assignmentModel.setCorrelatedStatuses(oneObject.optString("CorrelatedStatuses", ""));
                    assignmentModel.setAssignmentIndicators(oneObject.optString("AssignmentIndicators", ""));
                    assignmentModel.setServicePickingList(oneObject.optString("ServicePickingList", "0"));
                    assignmentModel.setContainsGroup(oneObject.optString("ContainsGroup", "0"));
                    assignmentModel.setProjectWarehouseId(oneObject.optString("ProjectWarehouseId", "0"));
                    assignmentModel.setProductPickingList(oneObject.optString("ProductPickingList", "0"));
                    assignmentModel.setCustomerCode(oneObject.optString("CustomerCode", ""));

                    String assignmentid = assignmentModel.getAssignmentId();
                    String Problem = assignmentModel.getProblem();
                    if( assignmentid.equals("-1") &&  Problem.equals("-1")){
                        Toast.makeText(MyApplication.getContext(), localized_no_available_mobile_slots, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if( assignmentid.equals("-2") &&  Problem.equals("-2")){
                        prefsBatch.apply();
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


                    JSONArray jArrayPjAttachments = MyJsonParser.getJsonArrayValue(oneObject, "ProjectAttachments");
                    List<AttachmentModel> PjAttachmentsList = new ArrayList<>();
                    for (int a = 0; a < jArrayPjAttachments.length(); a++){
                        JSONObject jObjectOnePjAttachment = jArrayPjAttachments.getJSONObject(a);

                        AttachmentModel attachmentModel = new AttachmentModel();

                        attachmentModel.setObjectId(MyJsonParser.getStringValue(jObjectOnePjAttachment, "ObjectId", "0"));
                        attachmentModel.setObjectType(MyJsonParser.getStringValue(jObjectOnePjAttachment, "ObjectType", ""));
                        attachmentModel.setFilename(MyJsonParser.getStringValue(jObjectOnePjAttachment, "Filename", ""));
                        attachmentModel.setCloudFileURL(MyJsonParser.getStringValue(jObjectOnePjAttachment, "CloudFileURL", ""));
                        attachmentModel.setAttachmentId(MyJsonParser.getStringValue(jObjectOnePjAttachment, "AttachmentId", "0"));
                        attachmentModel.setBlobAttachmentId(MyJsonParser.getStringValue(jObjectOnePjAttachment, "BlobAttachmentId", "0"));

                        PjAttachmentsList.add(attachmentModel);
                    }

                    assignmentModel.setProjectAttachments(PjAttachmentsList);

                    JSONArray jArrayDetChildren = MyJsonParser.getJsonArrayValue(oneObject, "DetChildren");

                    String resourceId = assignmentModel.getResourceId();
                    String detChildrenData = MyPrefs.getStringWithFileName(PREF_FILE_DET_CHILDREN_FOR_SHOW, assignmentid + "_" + resourceId, "");

                    if(!detChildrenData.isEmpty() && !detChildrenData.equals("[]")){
                        //Getting det children from cache if they are already saved.
                        jArrayDetChildren = new JSONArray(detChildrenData);
                    }

                    List<DetChildrenModel> detChildrenList = new ArrayList<>();

                    for (int _i = 0; _i < jArrayDetChildren.length(); _i++) {
                        JSONObject jObjectOneDetChild = jArrayDetChildren.getJSONObject(_i);

                        DetChildrenModel detChildModel = new DetChildrenModel();

                        detChildModel.setDetChildrenId(MyJsonParser.getStringValue(jObjectOneDetChild, "DetChildrenId", "0"));
                        detChildModel.setDetId(MyJsonParser.getStringValue(jObjectOneDetChild, "DetId", "0"));
                        detChildModel.setResourceId(MyJsonParser.getStringValue(jObjectOneDetChild, "ResourceId", ""));
                        detChildModel.setProjectProductId(MyJsonParser.getStringValue(jObjectOneDetChild, "ProjectProductId", ""));
                        detChildModel.setDetChildrenStatusCode(MyJsonParser.getStringValue(jObjectOneDetChild, "DetChildrenStatusCode", ""));
                        detChildModel.setDescription(MyJsonParser.getStringValue(jObjectOneDetChild, "Description", ""));
                        detChildModel.setDetChildStart(MyJsonParser.getStringValue(jObjectOneDetChild, "DetChildStartString", ""));
                        detChildModel.setDetChildStop(MyJsonParser.getStringValue(jObjectOneDetChild, "DetChildStopString", ""));
                        detChildModel.setDetChildrenCheckIn(MyJsonParser.getStringValue(jObjectOneDetChild, "DetChildrenCheckInString", ""));
                        detChildModel.setDetChildrenCheckOut(MyJsonParser.getStringValue(jObjectOneDetChild, "DetChildrenCheckOutString", ""));
                        detChildModel.setDetChildrenSolution(MyJsonParser.getStringValue(jObjectOneDetChild, "DetChildrenSolution", ""));
                        detChildModel.setDetChildCompleted(MyJsonParser.getStringValue(jObjectOneDetChild, "DetChildCompleted", "0"));

                        detChildrenList.add(detChildModel);
                    }

                    assignmentModel.setDetChildren(detChildrenList);


                    ASSIGNMENTS_LIST.add(assignmentModel);


                    Date assignmentStartDate = MyDateTime.getDateFromAssignmentDate(assignmentStartDateString);

                    if (assignmentStartDate != null) {
                        CALENDAR_EVENTS.add(assignmentStartDate);
                    }

                    long assignmentDuration = SystemClock.elapsedRealtime() - assignmentStart;

                    if (assignmentDuration > 100) {
                        Log.e("AssignmentsPerf","Assignment " + i +" id=" + assignmentModel.getAssignmentId() + " took " + assignmentDuration + " ms");
                    }
                }

                prefsBatch.apply();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                prefsBatch.apply();
            }
        }

        Collections.sort(ASSIGNMENTS_LIST, (a, b) -> a.getSorting().compareTo(b.getSorting()));

        Log.e("AssignmentsPerf", "TOTAL generate(): " +
                (SystemClock.elapsedRealtime() - totalStart) + " ms");
//        Log.e("myLogs: AssignmentsData", "----------- ASSIGNMENTS_LIST: \n" + ASSIGNMENTS_LIST);
    }

    private static long logTime(String label, long lastTime) {
        long now = SystemClock.elapsedRealtime();
        Log.e("AssignmentsPerf", label + ": " + (now - lastTime) + " ms");
        return now;
    }
}
