package am.gtest.vortex.models;

import java.util.List;

public class MeasurementModel {

    private String ProjectProductId = "";
    private String attributeName = "";
    private String measurementCompleted = "";
    private List<MeasurableAttributeDefaultModel> attributeDefaultModel;

    public String getProjectProductId() {
        return ProjectProductId;
    }

    public void setProjectProductId(String ProjectProductId) {
        this.ProjectProductId = ProjectProductId;
    }

    public String getMeasurementCompleted() {return measurementCompleted;}

    public void setMeasurementCompleted(String value) {this.measurementCompleted = value;}

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
                        "      \"ProjectProductId\": \"" + getProjectProductId() + "\",\n" +
                        "      \"MeasurableAttribute\": \"" + getAttributeName() + "\",\n" +
                        "      \"MeasurementCompleted\": \"" + measurementCompleted + "\",\n" +
                        "      \"MeasurableAttributeDefaultValues\": " + getAttributeDefaultModel() + "\n" +
                        "    }";

        zoneModel = zoneModel.replace("}]", "}\n      ]");

        return zoneModel;
    }
}
