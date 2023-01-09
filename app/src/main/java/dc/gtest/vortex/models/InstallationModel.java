package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

import java.util.List;

public class InstallationModel {


    private String projectInstallationId = "";
    private String projectInstallationCode = "";
    private String projectInstallationDescription = "";
    private String projectInstallationFullDescription = "";
    private String projectInstallationNotes = "";
    private String projectInstallationTypeId = "";
    private String projectInstallationTypeDescription = "";
    private List<CustomFieldModel> customFields;
    private String projectId = "";

    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "\n  {\n" +
                        "  \"ProjectInstallationId\": \"" + projectInstallationId + "\",\n" +
                        "  \"ProjectInstallationCode\": \"" + projectInstallationCode + "\",\n" +
                        "  \"ProjectInstallationDescription\": \"" + projectInstallationDescription + "\",\n" +
                        "  \"projectInstallationFullDescription\": \"" + projectInstallationFullDescription + "\",\n" +
                        "  \"ProjectInstallationNotes\": \"" + projectInstallationNotes + "\",\n" +
                        "  \"ProjectInstallationTypeId\": \"" + projectInstallationTypeId + "\",\n" +
                        "  \"ProjectInstallationTypeDescription\": \"" + projectInstallationTypeDescription + "\",\n" +
                        "  \"ProjectId\": " + projectId + "\n" +
                        "  \"CustomFields\": " + customFields + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getProjectInstallationId() {
        return projectInstallationId;
    }

    public void setProjectInstallationId(String ProjectInstallationId) {
        this.projectInstallationId = ProjectInstallationId;
    }

    public String getProjectInstallationCode() {
        return projectInstallationCode;
    }

    public void setProjectInstallationCode(String ProjectInstallationCode) {
        this.projectInstallationCode = ProjectInstallationCode;
    }

    public String getProjectInstallationDescription() {
        return projectInstallationDescription;
    }

    public void setProjectInstallationDescription(String ProjectInstallationDescription) {
        this.projectInstallationDescription = ProjectInstallationDescription;
    }

    public String getProjectInstallationFullDescription() {
        return projectInstallationFullDescription;
    }

    public void setProjectInstallationFullDescription(String ProjectInstallationFullDescription) {
        this.projectInstallationFullDescription = ProjectInstallationFullDescription;
    }


    public String getProjectInstallationNotes() {
        return projectInstallationNotes;
    }

    public void setProjectInstallationNotes(String ProjectInstallationNotes) {
        this.projectInstallationNotes = ProjectInstallationNotes;
    }

    public String getProjectInstallationTypeId() {
        return projectInstallationTypeId;
    }

    public void setProjectInstallationTypeId(String ProjectInstallationTypeId) {
        this.projectInstallationTypeId = ProjectInstallationTypeId;
    }

    public String getProjectInstallationTypeDescription() {
        return projectInstallationTypeDescription;
    }

    public void setProjectInstallationTypeDescription(String ProjectInstallationTypeDescription) {
        this.projectInstallationTypeDescription = ProjectInstallationTypeDescription;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String ProjectId) {
        this.projectId = ProjectId;
    }


    public List<CustomFieldModel> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldModel> CustomFields) {this.customFields = CustomFields;}

}
