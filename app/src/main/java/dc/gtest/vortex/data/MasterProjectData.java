package dc.gtest.vortex.data;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENT_TYPES_LIST;
import static dc.gtest.vortex.support.MyGlobals.MASTER_PROJECTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.MASTER_PROJECTS_LIST_FILTERED;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.models.AssignmentTypeModel;
import dc.gtest.vortex.models.MasterProjectModel;
import dc.gtest.vortex.support.MyJsonParser;

public class MasterProjectData {

    public static void generate(String masterProjects) {

        MASTER_PROJECTS_LIST.clear();
        MASTER_PROJECTS_LIST_FILTERED.clear();

        if (!masterProjects.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(masterProjects);

                MasterProjectModel masterProjectModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    masterProjectModel = new MasterProjectModel();

                    masterProjectModel.setMasterProjectId(MyJsonParser.getStringValue(oneObject, "MasterProjectId", "0"));
                    masterProjectModel.setMasterProjectDescription(MyJsonParser.getStringValue(oneObject, "MasterProjectDescription", ""));

                    MASTER_PROJECTS_LIST.add(masterProjectModel);
                }

                MASTER_PROJECTS_LIST_FILTERED.addAll(MASTER_PROJECTS_LIST);
                //Collections.sort(ASSIGNMENT_TYPES_LIST, (a, b) -> a.getServiceDescription().compareTo(b.getServiceDescription()));

//                Log.e("myLogs: ServicesData", "===================== SERVICES_LIST_FILTERED:\n" + SERVICES_LIST_FILTERED.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
