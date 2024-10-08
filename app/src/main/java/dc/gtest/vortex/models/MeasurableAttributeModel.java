package dc.gtest.vortex.models;

import java.util.List;

public class MeasurableAttributeModel {

    private String attributeId = "";
    private String attributeName = "";
    private final String measurementCompleted = "";
    private String enableMeasurementPhoto = "";
    private String measurementPhotoPath = "";
    private String measurementPhoto = "";
    private String projectProductMeasurableAttributeId = "";
    private List<MeasurableAttributeDefaultModel> attributeDefaultModel;

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getProjectProductMeasurableAttributeId() {
        return projectProductMeasurableAttributeId;
    }

    public void setProjectProductMeasurableAttributeId(String projectProductMeasurableAttributeId) {
        this.projectProductMeasurableAttributeId = projectProductMeasurableAttributeId;
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

    public String getEnableMeasurementPhoto() {
        return enableMeasurementPhoto;
    }

    public void setEnableMeasurementPhoto(String EnableMeasurementPhoto) {
        this.enableMeasurementPhoto = EnableMeasurementPhoto;}

    public String getMeasurementPhotoPath() {
        return measurementPhotoPath;
    }

    public void setMeasurementPhotoPath(String measurementPhotoPath) {
        this.measurementPhotoPath = measurementPhotoPath;
    }

    public String getMeasurementPhoto() {
        return measurementPhoto;
    }

    public void setMeasurementPhoto(String measurementPhoto) {
        this.measurementPhoto = measurementPhoto;
    }


    @Override
    public String toString() {
        String zoneModel =
                "\n    {\n" +
                "      \"MeasurableAttributeId\": \"" + getAttributeId() + "\",\n" +
                "      \"ProjectProductMeasurableAttributeId\": \"" + getProjectProductMeasurableAttributeId() + "\",\n" +
                "      \"MeasurableAttributeDescription\": \"" + getAttributeName() + "\",\n" +
                "      \"EnableMeasurementPhoto\": \"" + getEnableMeasurementPhoto() + "\",\n" +
                "      \"MeasurementPhotoPath\": \"" + getMeasurementPhotoPath() + "\",\n" +
                "      \"MeasurementPhoto\": \"" + getMeasurementPhoto() + "\",\n" +
                "      \"MeasurableAttributeDefaultValues\": " + getAttributeDefaultModel() + "\n" +
                "    }";

        zoneModel = zoneModel.replace("}]", "}\n      ]");

        return zoneModel;
    }
}
