package dc.gtest.vortex.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class ZoneModel {

    @SerializedName("ZoneId")
    private String zoneId = "";
    @SerializedName("ProjectId")
    private String projectId = "";
    @SerializedName("ProjectZoneDescription")
    private String zoneName = "";
    private String zoneFullName = "";
    @SerializedName("ProjectZoneNotes")
    private String zoneNotes = "";
    @SerializedName("ProjectInstallationId")
    private String projectInstallationId = "";
    @SerializedName("ProjectZoneCode")
    private String zoneCode = "";
    @SerializedName("CustomFieldsString")
    private String customFieldsString = "";
    private String ZoneProducts = "";
    @SerializedName("CustomFields")
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
