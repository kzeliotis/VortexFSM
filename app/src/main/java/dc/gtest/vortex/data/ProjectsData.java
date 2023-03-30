package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.models.ProjectModel;
import dc.gtest.vortex.support.MyJsonParser;

import static dc.gtest.vortex.support.MyGlobals.PROJECTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PROJECTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_COMPANY;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PROJECT;

public class ProjectsData {

    public static void generate(String ProjectId) {

        if(ProjectId.length() == 0){
            PROJECTS_LIST.clear();
        }

        String projects = SELECTED_COMPANY.getCompanyProjects();

        if (!projects.isEmpty()) {
            try {
                JSONArray jArrayCompanies = new JSONArray(projects);

                ProjectModel projectModel;

                for (int i = 0; i < jArrayCompanies.length(); i++) {
                    JSONObject oneObject = jArrayCompanies.getJSONObject(i);

                    projectModel = new ProjectModel();

                    projectModel.setProjectId(MyJsonParser.getStringValue(oneObject, "ProjectId", ""));
                    projectModel.setProjectDescription(MyJsonParser.getStringValue(oneObject, "ProjectDescription", ""));
                    projectModel.setProjectProducts(MyJsonParser.getStringValue(oneObject, "ProjectProducts", ""));

                    if(ProjectId.length() > 0){
                      SELECTED_PROJECT = projectModel;
                    }else{
                      PROJECTS_LIST.add(projectModel);
                    }

                }

                if (ProjectId.length() == 0){
                    Collections.sort(PROJECTS_LIST, (a, b) -> a.getProjectDescription().compareTo(b.getProjectDescription()));

                    PROJECTS_LIST_FILTERED.clear();
                    PROJECTS_LIST_FILTERED.addAll(PROJECTS_LIST);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
