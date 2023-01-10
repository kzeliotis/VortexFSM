package dc.gtest.vortex.models;

import java.util.List;

import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;

public class CustomFieldDetailModel {


    private String customFieldId = "0";
    private String VortexTableId = "0";
    private String VortexTableIdField = "";
    private String VortexTable = "";
    private String customFieldDescription = "";
    private String detailTableId = "0";
    private String customFieldDetailsString = "";
    private String detailTable;
    private String detailTableId_Field;
    private boolean isEdited;
    private List<CustomFieldDetailColumnModel> customFieldsDetailColumns;


    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String CustomFieldId) {
        this.customFieldId = CustomFieldId;
    }

    public String getVortexTableIdField() {
        return VortexTableIdField;
    }

    public void setVortexTableIdField(String VortexTableIdField) {this.VortexTableIdField = VortexTableIdField;}

    public String getVortexTable() {
        return VortexTable;
    }

    public void setVortexTable(String VortexTable) {this.VortexTable = VortexTable;}

    public String getCustomFieldDescription() {
        return customFieldDescription;
    }

    public void setCustomFieldDescription(String CustomFieldDescription) {this.customFieldDescription = CustomFieldDescription;}

    public String getCustomFieldDetailsString() {
        return customFieldDetailsString;
    }

    public void setCustomFieldDetailsString(String CustomFieldDetailsString) {this.customFieldDetailsString = CustomFieldDetailsString;}

    public String getDetailTableId() {
        return detailTableId;
    }

    public void setDetailTableId(String DetailTableId) {this.detailTableId = DetailTableId;}

    public String getDetailTable() {
        return detailTable;
    }

    public void setDetailTable(String DetailTable) {this.detailTable = DetailTable;}

    public String getVortexTableId() {
        return VortexTableId;
    }

    public void setVortexTableId(String VortexTableId) { this.VortexTableId = VortexTableId;}

    public String getDetailTableId_Field() {
        return detailTableId_Field;
    }

    public void setDetailTableId_Field(String detailTableId_Field) {this.detailTableId_Field = detailTableId_Field;}

    public List<CustomFieldDetailColumnModel> getCustomFieldsDetailColumns() {return customFieldsDetailColumns;}

    public void setCustomFieldsDetailColumns(List<CustomFieldDetailColumnModel> CustomFieldsDetailColumns) {this.customFieldsDetailColumns = CustomFieldsDetailColumns;}

    public boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean IsEdited) {
        this.isEdited = IsEdited;
    }

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"CustomFieldId\": \"" + getCustomFieldId() + "\",\n" +
                        "  \"VortexTableIdField\": \"" + getVortexTableIdField() + "\",\n" +
                        "  \"VortexTable\": \"" + getVortexTable() + "\",\n" +
                        "  \"CustomFieldDescription\": \"" + getCustomFieldDescription() + "\",\n" +
                        "  \"CustomFieldDetailsString\": \"" + getCustomFieldDetailsString() + "\",\n" +
                        "  \"DetailTable\": \"" + getDetailTable() + "\",\n" +
                        "  \"DetailTableId_Field\": \"" + getDetailTableId_Field() + "\",\n" +
                        "  \"DetailTableId\": \"" + getDetailTableId() + "\",\n" +
                        "  \"VortexTableId\": \"" + getVortexTableId() + "\",\n" +
                        "  \"IsEdited\": \"" + getIsEdited() + "\",\n" +
                        "  \"UserCreated\": \"" + MyPrefs.getString(PREF_USERID, "0") + "\",\n" +
                        "  \"CustomFieldsDetailColumns\": " + getCustomFieldsDetailColumns() + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

}
