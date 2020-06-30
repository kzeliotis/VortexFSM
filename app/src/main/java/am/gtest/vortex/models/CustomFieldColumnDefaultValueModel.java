package am.gtest.vortex.models;

public class CustomFieldColumnDefaultValueModel {

    private String customFieldsDetailColumnId = "";
    private String defaultValue = "";
    private String customFieldId = "";
    private boolean isInitial;
    private boolean isEditable;

    public String getCustomFieldsDetailColumnId() {
        return customFieldsDetailColumnId;
    }

    public void setCustomFieldsDetailColumnId(String CustomFieldsDetailColumnId) {this.customFieldsDetailColumnId = CustomFieldsDetailColumnId;}

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValueName) {
        this.defaultValue = defaultValueName;
    }

    public boolean getInitial() {
        return isInitial;
    }

    public void setInitial(boolean isInitial) {
        this.isInitial = isInitial;
    }

    public boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean IsEditable) {this.isEditable = IsEditable;}

    public String getBelongsToCustomFieldId() {
        return customFieldId;
    }

    public void setBelongsToCustomFieldId(String CustomFieldId) {this.customFieldId = CustomFieldId;}

    @Override
    public String toString() {
        String cfdvModel =
                "\n        {\n" +
                        "          \"CustomFieldsDetailColumnId\": \"" + getCustomFieldsDetailColumnId() + "\",\n" +
                        "          \"BelongsToCustomFieldId\": \"" + getBelongsToCustomFieldId() + "\",\n" +
                        "          \"DefaultValue\": \"" + getDefaultValue() + "\",\n" +
                        "          \"IsEditable\": \"" + getIsEditable() + "\",\n" +
                        "          \"Initial\": " + getInitial() + "\n" +
                        "        }";

        cfdvModel = cfdvModel.replace("}]", "}        \n]");

        return cfdvModel;
    }


}
