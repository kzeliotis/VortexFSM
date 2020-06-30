package am.gtest.vortex.models;

import java.util.List;

public class CustomFieldDetailColumnModel {

    private String customFieldsDetailColumnId = "0";
    private String customFieldId = "0";
    private String columnName = "";
    private String columnDescription = "";
    private String columnDataType = "";
    private String columnValue = "";
    private boolean hasValues;

    //private List<CustomFieldDetailModel> customFieldDetails;

    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String CustomFieldId) {
        this.customFieldId = CustomFieldId;
    }

    public String getCustomFieldsDetailColumnId() {
        return customFieldsDetailColumnId;
    }

    public void setCustomFieldsDetailColumnId(String customFieldsDetailColumnId) {this.customFieldsDetailColumnId = customFieldsDetailColumnId;}

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {this.columnName = columnName;}

    public String getColumnDescription() {
        return columnDescription;
    }

    public void setColumnDescription(String columnDescription) {this.columnDescription = columnDescription;}

    public String getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(String columnDataType) {this.columnDataType = columnDataType;}

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {this.columnValue = columnValue;}

    public boolean getHasValues() {
        return hasValues;
    }

    public void setHasValues(boolean HasValues) {
        this.hasValues = HasValues;
    }


    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"CustomFieldId\": \"" + getCustomFieldId() + "\",\n" +
                        "  \"CustomFieldsDetailColumnId\": \"" + getCustomFieldsDetailColumnId() + "\",\n" +
                        "  \"ColumnName\": \"" + getColumnName() + "\",\n" +
                        "  \"ColumnDescription\": \"" + getColumnDescription() + "\",\n" +
                        "  \"ColumnDataType\": \"" + getColumnDataType() + "\",\n" +
                        "  \"HasValues\": \"" + getHasValues() + "\",\n" +
                        "  \"ColumnValue\": \"" + getColumnValue() + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }


}
