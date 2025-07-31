package dc.gtest.vortex.models;

import org.json.JSONArray;

public class AllAttributeModel {

    private String attributeDescription = "";
    private String attributeId = "";
    private boolean isDateTime = false;
    private JSONArray attributeDefaultValues = new JSONArray();

    @Override
    public String toString() {

        String defaultValues = "";

        try {
            defaultValues = attributeDefaultValues.toString(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String modelToString =
                "{\n" +
                        "  \"AttributeDescription\": \"" + attributeDescription + "\",\n" +
                        "  \"AttributeId\": \"" + attributeId + "\"\n" +
                        "  \"AttributeDefaultValues\": " + defaultValues + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getAttributeDescription() {
        return attributeDescription;
    }

    public void setAttributeDescription(String attributeDescription) {
        this.attributeDescription = attributeDescription;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public JSONArray getAttributeDefaultValues() {
        return attributeDefaultValues;
    }

    public void setAttributeDefaultValues(JSONArray attributeDefaultValues) {
        this.attributeDefaultValues = attributeDefaultValues;
    }

    public boolean isDateTime() {
        return isDateTime;
    }

    public void setDateTime(boolean dateTime) {
        isDateTime = dateTime;
    }
}
