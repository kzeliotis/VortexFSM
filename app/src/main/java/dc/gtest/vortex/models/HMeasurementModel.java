package dc.gtest.vortex.models;

import java.util.ArrayList;
import java.util.List;

public class HMeasurementModel {

    private int hAssignmentId;
    private List<HMeasurementValueModel> hMeasurementValues = new ArrayList<>();
    private String product;
    private int projectProductId;

    public int getHAssignmentId() {
        return hAssignmentId;
    }

    public void setHAssignmentId(int hAssignmentId) {
        this.hAssignmentId = hAssignmentId;
    }

    public List<HMeasurementValueModel> getHMeasurementValues() {
        return hMeasurementValues;
    }

    public void setHMeasurementValues(List<HMeasurementValueModel> hMeasurementValues) {
        this.hMeasurementValues = hMeasurementValues;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getProjectProductId() {
        return projectProductId;
    }

    public void setProjectProductId(int projectProductId) {
        this.projectProductId = projectProductId;
    }
}
