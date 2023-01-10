package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.models.AssignmentTypeModel;
import dc.gtest.vortex.support.MyJsonParser;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENT_TYPES_LIST;

public class AssignmentTypesData {

    public static void generate(String assignmentTypes) {

        ASSIGNMENT_TYPES_LIST.clear();

        if (!assignmentTypes.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(assignmentTypes);

                AssignmentTypeModel assignmentTypeModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    assignmentTypeModel = new AssignmentTypeModel();

                    assignmentTypeModel.setAssignmentTypeId(MyJsonParser.getStringValue(oneObject, "AssignmentTypeId", ""));
                    assignmentTypeModel.setAssignmentTypeDescription(MyJsonParser.getStringValue(oneObject, "AssignmentTypeDescription", ""));

                    ASSIGNMENT_TYPES_LIST.add(assignmentTypeModel);
                }

                //Collections.sort(ASSIGNMENT_TYPES_LIST, (a, b) -> a.getServiceDescription().compareTo(b.getServiceDescription()));

//                Log.e("myLogs: ServicesData", "===================== SERVICES_LIST_FILTERED:\n" + SERVICES_LIST_FILTERED.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
