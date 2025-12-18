package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.api.GetProducts;
import dc.gtest.vortex.models.InstallationModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_LIST;
import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_INSTALLATIONS_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATIONS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_PRODUCTS_DATA_FOR_SHOW;

public class ProjectInstallationsData {

    public static void generate(boolean refresh) {

        INSTALLATION_LIST.clear();
        INSTALLATION_LIST_FILTERED.clear();
        String AssignmentId = SELECTED_ASSIGNMENT.getAssignmentId();

        String installations = MyPrefs.getString(PREF_DATA_INSTALLATIONS_LIST, "");

        if (installations.isEmpty()) {
            installations = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATIONS_DATA_FOR_SHOW,  AssignmentId, "");
        }

        if (!installations.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(installations);

                InstallationModel installationModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    installationModel = new InstallationModel();

                    if (MyJsonParser.getStringValue(oneObject, "ProjectInstallationId", "").equals("0")){
                        continue;
                    }

                    installationModel.setProjectInstallationId(MyJsonParser.getStringValue(oneObject, "ProjectInstallationId", ""));
                    installationModel.setProjectInstallationCode(MyJsonParser.getStringValue(oneObject, "ProjectInstallationCode", ""));
                    installationModel.setProjectInstallationTypeDescription(MyJsonParser.getStringValue(oneObject, "ProjectInstallationTypeDescription", ""));
                    installationModel.setProjectInstallationDescription(MyJsonParser.getStringValue(oneObject, "ProjectInstallationDescription", ""));
                    installationModel.setProjectInstallationFullDescription(MyJsonParser.getStringValue(oneObject, "ProjectInstallationFullDescription", ""));
                    installationModel.setProjectInstallationTypeId(MyJsonParser.getStringValue(oneObject, "ProjectInstallationTypeId", ""));
                    installationModel.setProjectInstallationNotes(MyJsonParser.getStringValue(oneObject, "ProjectInstallationNotes", ""));
                    installationModel.setProjectId(MyJsonParser.getStringValue(oneObject, "ProjectId", ""));

                    String projectInstallationId = installationModel.getProjectInstallationId();
                    String prefKey = projectInstallationId;
                    String insallationProducts = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_PRODUCTS_DATA_FOR_SHOW, prefKey, "");
                    if (insallationProducts.isEmpty() || refresh) {
                        GetProducts getProducts = new GetProducts(null, SELECTED_ASSIGNMENT.getAssignmentId(), true,
                                projectInstallationId, false, "", false);
                        getProducts.execute();
                    }

                    INSTALLATION_LIST.add(installationModel);
                }

                Collections.sort(INSTALLATION_LIST, (a, b) -> a.getProjectInstallationFullDescription().compareTo(b.getProjectInstallationFullDescription()));

                INSTALLATION_LIST_FILTERED.addAll(INSTALLATION_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
