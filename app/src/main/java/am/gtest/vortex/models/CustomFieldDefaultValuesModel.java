package am.gtest.vortex.models;

public class CustomFieldDefaultValuesModel {

    private String defaultValueId = "";
    private String defaultValue = "";
    private String customFieldId = "";
    private boolean isInitial;
    private boolean isEdited;

    public String getDefaultValueId() {
        return defaultValueId;
    }

    public void setDefaultValueId(String defaultValueId) {
        this.defaultValueId = defaultValueId;
    }

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

    public boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String CustomFieldId) {
        this.customFieldId = CustomFieldId;
    }

    @Override
    public String toString() {
        String cfdvModel =
                "\n        {\n" +
                        "          \"CustomFieldsDefaultValueId\": \"" + getDefaultValueId() + "\",\n" +
                        "          \"CustomFieldId\": \"" + getCustomFieldId() + "\",\n" +
                        "          \"DefaultValue\": \"" + getDefaultValue() + "\",\n" +
                        "          \"IsEdited\": \"" + getIsEdited() + "\",\n" +
                        "          \"Initial\": " + getInitial() + "\n" +
                        "        }";

        cfdvModel = cfdvModel.replace("}]", "}        \n]");

        return cfdvModel;
    }

}
