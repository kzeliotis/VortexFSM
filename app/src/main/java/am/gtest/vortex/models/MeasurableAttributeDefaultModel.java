package am.gtest.vortex.models;

public class MeasurableAttributeDefaultModel {

    private String defaultValueId = "";
    private String defaultValueName = "";
    private boolean isInitial;
    private boolean isEdited;

    public String getDefaultValueId() {
        return defaultValueId;
    }

    public void setDefaultValueId(String defaultValueId) {
        this.defaultValueId = defaultValueId;
    }

    public String getDefaultValueName() {
        return defaultValueName;
    }

    public void setDefaultValueName(String defaultValueName) {
        this.defaultValueName = defaultValueName;
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

    @Override
    public String toString() {
        String zoneModel =
                "\n        {\n" +
                "          \"MeasurableAttributeDefaultValueId\": \"" + getDefaultValueId() + "\",\n" +
                "          \"DefaultValue\": \"" + getDefaultValueName() + "\",\n" +
                "          \"IsEdited\": \"" + getIsEdited() + "\",\n" +
                "          \"Initial\": " + getInitial() + "\n" +
                "        }";

        zoneModel = zoneModel.replace("}]", "}        \n]");

        return zoneModel;
    }
}
