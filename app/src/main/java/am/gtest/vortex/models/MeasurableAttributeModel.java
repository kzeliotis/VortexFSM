package am.gtest.vortex.models;

import java.util.List;

public class MeasurableAttributeModel {

    private String attributeId = "";
    private String attributeName = "";
    private String measurementCompleted = "";
    private List<MeasurableAttributeDefaultModel> attributeDefaultModel;

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }



    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public List<MeasurableAttributeDefaultModel> getAttributeDefaultModel() {
        return attributeDefaultModel;
    }

    public void setAttributeDefaultModel(List<MeasurableAttributeDefaultModel> attributeDefaultModel) {
        this.attributeDefaultModel = attributeDefaultModel;
    }

    @Override
    public String toString() {
        String zoneModel =
                "\n    {\n" +
                "      \"MeasurableAttributeId\": \"" + getAttributeId() + "\",\n" +
                "      \"MeasurableAttributeDescription\": \"" + getAttributeName() + "\",\n" +
                "      \"MeasurableAttributeDefaultValues\": " + getAttributeDefaultModel() + "\n" +
                "    }";

        zoneModel = zoneModel.replace("}]", "}\n      ]");

        return zoneModel;
    }
}
