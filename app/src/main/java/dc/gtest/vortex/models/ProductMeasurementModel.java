package dc.gtest.vortex.models;

public class ProductMeasurementModel {

    private String assignmentId = "";
    private String zoneProductId = "";
    private String measurableAttributeId = "";
    private String defaultValueId = "";
    private String value = "";
    private String ProjectZoneId = "";
    private String measurementPhotoPath = "";
    private String measurementPhoto = "";


    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getZoneProductId() {
        return zoneProductId;
    }

    public void setZoneProductId(String zoneProductId) {
        this.zoneProductId = zoneProductId;
    }

    public String getMeasurableAttributeId() {
        return measurableAttributeId;
    }

    public void setMeasurableAttributeId(String measurableAttributeId) {
        this.measurableAttributeId = measurableAttributeId;
    }

    public String getDefaultValueId() {
        return defaultValueId;
    }

    public void setDefaultValueId(String defaultValueId) {
        this.defaultValueId = defaultValueId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProjectZoneId() {
        return ProjectZoneId;
    }

    public void setProjectZoneId(String ProjectZoneId) {
        this.ProjectZoneId = ProjectZoneId;
    }

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


    // This is sent to server, do not change key names
    @Override
    public String toString() {
        String modelToString =
                "\n  {\n" +
                "    \"AssignmentId\": \"" + getAssignmentId() + "\",\n" +
                "    \"ZoneProductId\": \"" + getZoneProductId() + "\",\n" +
                "    \"MeasurableAttributeId\": \"" + getMeasurableAttributeId() + "\",\n" +
                "    \"MeasurableAttributeDefaultValueId\": \"" + getDefaultValueId() + "\",\n" +
                "    \"MeasurementPhotoPath\": \"" + getMeasurementPhotoPath() + "\",\n" +
                "    \"MeasurementPhoto\": \"" + getMeasurementPhoto() + "\",\n" +
                "    \"Value\": \"" + getValue() + "\",\n" +
                "    \"ProjectZoneId\": \"" + getProjectZoneId() + "\"\n" +
                "  }";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }
}
