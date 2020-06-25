package am.gtest.vortex.models;

public class HMeasurementValueModel {

    private String measurableAttribute;
    private String measurementValue;
    private int projectProductId;

    public String getMeasurableAttribute() {
        return measurableAttribute;
    }

    public void setMeasurableAttribute(String measurableAttribute) {
        this.measurableAttribute = measurableAttribute;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(String measurementValue) {
        this.measurementValue = measurementValue;
    }

    public int getProjectProductId() {
        return projectProductId;
    }

    public void setProjectProductId(int projectProductId) {
        this.projectProductId = projectProductId;
    }
}
