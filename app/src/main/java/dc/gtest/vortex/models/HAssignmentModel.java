package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class HAssignmentModel {

    private String dateStart;
    private String hAssignmentId;
    private String resourceName;
    private String product;
    private String service;
    private String problem;
    private String solution;
    private boolean ProjectHistory;
    private List<HMeasurementModel> hMeasurements = new ArrayList<>();
    private List<HConsumableModel> hConsumables = new ArrayList<>();
    private List<AttachmentModel> hAttachments = new ArrayList<>();

    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"dateStart\": \"" + dateStart + "\",\n" +
                        "  \"hAssignmentId\": \"" + hAssignmentId + "\",\n" +
                        "  \"resourceName\": \"" + resourceName + "\",\n" +
                        "  \"product\": \"" + product + "\",\n" +
                        "  \"service\": \"" + service + "\",\n" +
                        "  \"problem\": \"" + problem + "\",\n" +
                        "  \"solution\": \"" + solution + "\",\n" +
                        "  \"ProjectHistory\": \"" + ProjectHistory + "\",\n" +
                        "  \"hMeasurements\": " + hMeasurements + ",\n" +
                        "  \"hConsumables\": " + hConsumables + ",\n" +
                        "  \"hAttachments\": " + hAttachments + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getHAssignmentId() {
        return hAssignmentId;
    }

    public void setHAssignmentId(String hAssignmentId) {
        this.hAssignmentId = hAssignmentId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public boolean getProjectHistory() {
        return ProjectHistory;
    }

    public void setProjectHistory(boolean ProjectHistory) {
        this.ProjectHistory = ProjectHistory;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public List<HMeasurementModel> getHMeasurements() {
        return hMeasurements;
    }

    public void setHMeasurements(List<HMeasurementModel> hMeasurements) {
        this.hMeasurements = hMeasurements;
    }

    public List<HConsumableModel> getHConsumables() {
        return hConsumables;
    }

    public void setHConsumables(List<HConsumableModel> hConsumables) {
        this.hConsumables = hConsumables;
    }

    public List<AttachmentModel> getHAttachments() {
        return hAttachments;
    }

    public void setHAttachments(List<AttachmentModel> hAttachments) {
        this.hAttachments = hAttachments;
    }
}
