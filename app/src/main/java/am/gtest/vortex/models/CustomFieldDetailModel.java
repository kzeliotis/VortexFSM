package am.gtest.vortex.models;

import java.util.List;

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

    public void setVortexTableId(String VortexableId) { this.VortexTableId = VortexTableId;}

    public String getDetailTableId_Field() {
        return detailTableId_Field;
    }

    public void setDetailTableId_Field(String detailTableId_Field) {
        this.detailTableId_Field = detailTableId_Field;
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
                        "  \"VortexTableId\": \"" + getVortexTableId() + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

}
