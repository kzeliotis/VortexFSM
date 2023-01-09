package dc.gtest.vortex.models;

import java.util.List;

public class ZoneModel {

    private String zoneId = "";
    private String projectId = "";
    private String zoneName = "";
    private String zoneFullName = "";
    private String zoneNotes = "";
    private String projectInstallationId = "";
    private String zoneCode = "";
    private String customFieldsString = "";
    private List<CustomFieldModel> customFields;

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getZoneFullName() {
        return zoneFullName;
    }

    public void setZoneFullName(String zoneFullName) {
        this.zoneFullName = zoneFullName;
    }

    public List<CustomFieldModel> getZoneCustomFields() {
        return customFields;
    }

    public void setZoneCustomFields(List<CustomFieldModel> CustomFields) {this.customFields = CustomFields;}

    public String getZoneNotes() {
        return zoneNotes;
    }

    public void setZoneNotes(String zoneNotes) {
        this.zoneNotes = zoneNotes;
    }

    public String getProjectInstallationId() {
        return projectInstallationId;
    }

    public void setProjectInstallationId(String ProjectInstallationId) {this.projectInstallationId = ProjectInstallationId;}

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String ZoneCode) {
        this.zoneCode = ZoneCode;
    }

    public String getCustomFieldsString() {
        return customFieldsString;
    }

    public void setCustomFieldsString(String CustomFieldsString) {this.customFieldsString = CustomFieldsString;}

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String ProjectId) {
        this.projectId = ProjectId;
    }

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                "  \"zoneId\": \"" + getZoneId() + "\",\n" +
                "  \"ProjectId\": \"" + getProjectId() + "\",\n" +
                "  \"zoneName\": \"" + getZoneName() + "\",\n" +
                "  \"zoneFullName\": \"" + getZoneFullName() + "\",\n" +
                "  \"zoneNotes\": \"" + getZoneNotes() + "\",\n" +
                "  \"ProjectInstallationId\": \"" + getProjectInstallationId() + "\",\n" +
                "  \"ZoneCode\": \"" + getZoneCode() + "\",\n" +
                "  \"CustomFieldsString\": \"" + getCustomFieldsString() + "\",\n" +
                "  \"zoneCustomFields\": " + getZoneCustomFields() + "\n" +
                "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }
}
