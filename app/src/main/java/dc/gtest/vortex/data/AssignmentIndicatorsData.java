package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.models.AssignmentIndicatorModel;
import dc.gtest.vortex.models.AssignmentTypeModel;
import dc.gtest.vortex.support.MyJsonParser;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENT_INDICATORS_LIST;

public class AssignmentIndicatorsData {

    public static void generate(String assignmentIndicators) {

            ASSIGNMENT_INDICATORS_LIST.clear();

        if (!assignmentIndicators.isEmpty()) {
        try {
            JSONArray jArrayDataFromApi = new JSONArray(assignmentIndicators);

            AssignmentIndicatorModel assignmentIndicatorModel;

            for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                assignmentIndicatorModel = new AssignmentIndicatorModel();

                assignmentIndicatorModel.setAssignmentIndicatorId(MyJsonParser.getStringValue(oneObject, "AssignmentIndicatorId", ""));
                assignmentIndicatorModel.setAssignmentIndicatorDescription(MyJsonParser.getStringValue(oneObject, "AssignmentIndicatorDescription", ""));

                ASSIGNMENT_INDICATORS_LIST.add(assignmentIndicatorModel);
            }

            //Collections.sort(ASSIGNMENT_TYPES_LIST, (a, b) -> a.getServiceDescription().compareTo(b.getServiceDescription()));

//                Log.e("myLogs: ServicesData", "===================== SERVICES_LIST_FILTERED:\n" + SERVICES_LIST_FILTERED.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

}
