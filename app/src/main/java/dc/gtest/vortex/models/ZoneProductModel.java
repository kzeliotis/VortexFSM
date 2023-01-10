package dc.gtest.vortex.models;

import java.util.List;

public class ZoneProductModel {

    private String zoneProductId = "";
    private String zoneProductIdentity = "";
    private String zoneProductName = "";
    private List<MeasurableAttributeModel> measurableAttributeModel;

    public String getZoneProductId() {
        return zoneProductId;
    }

    public void setZoneProductId(String zoneProductId) {
        this.zoneProductId = zoneProductId;
    }

    public String getZoneProductIdentity() {
        return zoneProductIdentity;
    }

    public void setZoneProductIdentity(String zoneProductIdentity) {
        this.zoneProductIdentity = zoneProductIdentity;
    }

    public String getZoneProductName() {
        return zoneProductName;
    }

    public void setZoneProductName(String zoneProductName) {
        this.zoneProductName = zoneProductName;
    }

    public List<MeasurableAttributeModel> getMeasurableAttributeModel() {
        return measurableAttributeModel;
    }

    public void setMeasurableAttributeModel(List<MeasurableAttributeModel> measurableAttributeModel) {
        this.measurableAttributeModel = measurableAttributeModel;
    }

    @Override
    public String toString() {
        String zoneModel =
                "\n{\n" +
                "  \"ZoneProductId\": \"" + getZoneProductId() + "\",\n" +
                "  \"ZoneProductIdentity\": \"" + getZoneProductIdentity() + "\",\n" +
                "  \"ZoneProductDescription\": \"" + getZoneProductName() + "\",\n" +
                "  \"MeasurableAttributes\": " + getMeasurableAttributeModel() + "\n" +
                "}";

        zoneModel = zoneModel.replace("}]", "}\n  ]");

        return zoneModel;
    }
}
