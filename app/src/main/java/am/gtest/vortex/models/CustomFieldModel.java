package am.gtest.vortex.models;

import java.util.List;

public class CustomFieldModel {


    private String customFieldId = "0";
    private String objectTableId = "0";
    private String objectTableIdField = "";
    private String objectTable = "";
    private String customFieldDescription = "";
    private String customFieldValue = "";
    private String customFieldValueId = "0";
    private String customFieldDataType = "";
    private String assignmentId = "0";
    private boolean hasValues;
    private boolean editable;
    private List<CustomFieldDetailModel> customFieldDetails;

    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String CustomFieldId) {
        this.customFieldId = CustomFieldId;
    }

    public String getObjectTableIdField() {
        return objectTableIdField;
    }

    public void setObjectTableIdField(String ObjectTableIdField) {this.objectTableIdField = ObjectTableIdField;}

    public String getObjectTable() {
        return objectTable;
    }

    public void setObjectTable(String ObjectTable) {this.objectTable = ObjectTable;}

    public String getCustomFieldDescription() {
        return customFieldDescription;
    }

    public void setCustomFieldDescription(String CustomFieldDescription) {this.customFieldDescription = CustomFieldDescription;}

    public String getCustomFieldValue() {
        return customFieldValue;
    }

    public void setCustomFieldValue(String CustomFieldValue) {this.customFieldValue = CustomFieldValue;}

    public String getCustomFieldValueId() {
        return customFieldValueId;
    }

    public void setCustomFieldValueId(String CustomFieldValueId) {this.customFieldValueId = CustomFieldValueId;}


    public String getCustomFieldDataType() {
        return customFieldDataType;
    }

    public void setCustomFieldDataType(String customFieldDataType) {this.customFieldDataType = customFieldDataType;}

    public String getObjectTableId() {
        return objectTableId;
    }

    public void setObjectTableId(String ObjectTableId) { this.objectTableId = ObjectTableId;}

    public boolean getHasValues() {
        return hasValues;
    }

    public void setHasValues(boolean HasValues) {
        this.hasValues = HasValues;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean Editable) {
        this.editable = Editable;
    }

    public List<CustomFieldDetailModel> getCustomFieldDetails() {
        return customFieldDetails;
    }

    public void setCustomFieldDetails(List<CustomFieldDetailModel> CustomFieldDetails) {this.customFieldDetails = CustomFieldDetails;}

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String AssignmentId) {this.assignmentId = AssignmentId;}


    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"CustomFieldId\": \"" + getCustomFieldId() + "\",\n" +
                        "  \"VortexTableIdField\": \"" + getObjectTableIdField() + "\",\n" +
                        "  \"VortexTable\": \"" + getObjectTable() + "\",\n" +
                        "  \"AssignmentId\": \"" + getAssignmentId() + "\",\n" +
                        "  \"CustomFieldDescription\": \"" + getCustomFieldDescription() + "\",\n" +
                        "  \"CustomFieldValue\": \"" + getCustomFieldValue() + "\",\n" +
                        "  \"VortexTableCustomFieldId\": \"" + getCustomFieldValueId() + "\",\n" +
                        "  \"HasValues\": \"" + getHasValues() + "\",\n" +
                        "  \"Editable\": \"" + getEditable() + "\",\n" +
                        "  \"CustomFieldDataType\": \"" + getCustomFieldDataType() + "\",\n" +
                        "  \"VortexTableId\": \"" + getObjectTableId() + "\",\n" +
                        "  \"CustomFieldDetails\": " + getCustomFieldDetails() + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

}
