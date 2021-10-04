package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import am.gtest.vortex.models.MandatoryTaskModel;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyGlobals.MANDATORY_TASKS_LIST;
import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_MANDATORY_TASKS_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_MANDATORY_TASKS_FOR_SYNC;

public class MandatoryTasksData {


    public static void generate() {

        MANDATORY_TASKS_LIST.clear();

        String assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");

        String savedMandatoryTasks = MyPrefs.getStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SHOW, assignmentId, "");

//        if(refresh){
//            MyPrefs.removeStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SHOW, assignmentId);
//            MyPrefs.removeStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SYNC, assignmentId);
//        }

        if (savedMandatoryTasks.isEmpty()||savedMandatoryTasks.equals("[]")) {
            try {
                MandatoryTaskModel mandatoryTaskModel;

                for (int i = 0; i < SELECTED_ASSIGNMENT.getMandatoryTasks().length(); i++) {
                    JSONObject oneObject = SELECTED_ASSIGNMENT.getMandatoryTasks().getJSONObject(i);

                    //                                Log.e(LOG_TAG, "-------- oneObject: \n" + oneObject.toString(2));

                    mandatoryTaskModel = new MandatoryTaskModel();

                    mandatoryTaskModel.setStepSequence(MyJsonParser.getStringValue(oneObject, "StepSequence", ""));
                    mandatoryTaskModel.setStepDescription(MyJsonParser.getStringValue(oneObject, "StepDescription", ""));
                    mandatoryTaskModel.setMeasurableAttribute(MyJsonParser.getStringValue(oneObject, "MeasureableAttribute", "")); // TYPO comes from API
                    mandatoryTaskModel.setHasMeasurement(MyJsonParser.getStringValue(oneObject, "HasMeasurement", ""));
                    mandatoryTaskModel.setRequiresPhoto(MyJsonParser.getStringValue(oneObject, "RequiresPhoto", "0"));
                    mandatoryTaskModel.setProductId(MyJsonParser.getStringValue(oneObject, "ProductId", "0"));
                    mandatoryTaskModel.setSetToProject(MyJsonParser.getStringValue(oneObject, "SetToProject", "0"));
                    mandatoryTaskModel.setServiceStepDefaultValues(MyJsonParser.getJsonArrayValue(oneObject, "ServiceStepDefaultValues"));
                    mandatoryTaskModel.setStepComments(MyJsonParser.getStringValue(oneObject, "Comments", ""));
                    mandatoryTaskModel.setIsDateTime(MyJsonParser.getStringValue(oneObject, "IsDateTime", "0"));
                    mandatoryTaskModel.setIsOptional(MyJsonParser.getStringValue(oneObject, "IsOptional", "0"));
                    mandatoryTaskModel.setStepId(MyJsonParser.getStringValue(oneObject, "StepId", "0"));

                    MANDATORY_TASKS_LIST.add(mandatoryTaskModel);
                }

                Collections.sort(MANDATORY_TASKS_LIST, (a, b) -> a.getStepSequence().compareTo(b.getStepSequence()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                MandatoryTaskModel mandatoryTaskModel;

                JSONArray jArray = new JSONArray(savedMandatoryTasks);

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);

                    mandatoryTaskModel = new MandatoryTaskModel();

                    mandatoryTaskModel.setStepSequence(MyJsonParser.getStringValue(oneObject, "StepSequence", ""));
                    mandatoryTaskModel.setStepDescription(MyJsonParser.getStringValue(oneObject, "StepDescription", ""));
                    mandatoryTaskModel.setMeasurementValue(MyJsonParser.getStringValue(oneObject, "MeasurementValue", ""));
                    mandatoryTaskModel.setMeasurableAttribute(MyJsonParser.getStringValue(oneObject, "MeasureableAttribute", "")); // TYPO comes from API
                    mandatoryTaskModel.setHasMeasurement(MyJsonParser.getStringValue(oneObject, "HasMeasurement", ""));
                    mandatoryTaskModel.setRequiresPhoto(MyJsonParser.getStringValue(oneObject, "RequiresPhoto", "0"));
                    mandatoryTaskModel.setProductId(MyJsonParser.getStringValue(oneObject, "ProductId", "0"));
                    mandatoryTaskModel.setSetToProject(MyJsonParser.getStringValue(oneObject, "SetToProject", "0"));
                    mandatoryTaskModel.setStepPhotoPath(MyJsonParser.getStringValue(oneObject, "StepPhotoPath", "0"));
                    mandatoryTaskModel.setStepPhoto(MyJsonParser.getStringValue(oneObject, "StepImage", "0"));
                    mandatoryTaskModel.setServiceStepDefaultValues(MyJsonParser.getJsonArrayValue(oneObject, "ServiceStepDefaultValues"));
                    mandatoryTaskModel.setStepComments(MyJsonParser.getStringValue(oneObject, "Comments", ""));
                    mandatoryTaskModel.setIsDateTime(MyJsonParser.getStringValue(oneObject, "IsDateTime", "0"));
                    mandatoryTaskModel.setIsOptional(MyJsonParser.getStringValue(oneObject, "IsOptional", "0"));
                    mandatoryTaskModel.setStepId(MyJsonParser.getStringValue(oneObject, "StepId", "0"));

                    MANDATORY_TASKS_LIST.add(mandatoryTaskModel);
                }

                Collections.sort(MANDATORY_TASKS_LIST, (a, b) -> a.getStepSequence().compareTo(b.getStepSequence()));

//                Log.e("myLogs: MandatoryTasks", "-------------- MANDATORY_TASKS_LIST: \n" + MANDATORY_TASKS_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
