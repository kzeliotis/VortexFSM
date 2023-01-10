package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;

public class AttributeModel {

    private String attributeId = "";
    private String attributeDescription = "";
    private String attributeValue = "";
    private String valueId = "";
    private String projectProductId = "";
    private JSONArray attributeDefaultValues = new JSONArray();

    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "\n  {\n" +
                        "  \"AttributeId\": \"" + attributeId + "\",\n" +
                        "  \"AttributeDescription\": \"" + attributeDescription + "\",\n" +
                        "  \"Value\": \"" + attributeValue + "\",\n" +
                        "  \"ValueId\": \"" + valueId + "\",\n" +
                        "  \"ProjectProductId\": \"" + projectProductId + "\",\n" +
                        "  \"AttributeDefaultValues\": " + attributeDefaultValues + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }


    public String getAttributeDescription() {
        return attributeDescription;
    }

    public void setAttributeDescription(String attributeDescription) {
        this.attributeDescription = attributeDescription;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }


    public String getProjectProductId() {
        return projectProductId;
    }

    public void setProjectProductId(String projectProductId) {
        this.projectProductId = projectProductId;
    }

    public JSONArray getAttributeDefaultValues() {
        return attributeDefaultValues;
    }

    public void setAttributeDefaultValues(JSONArray attributeDefaultValues) {
        this.attributeDefaultValues = attributeDefaultValues;
    }
}
