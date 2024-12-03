package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;

public class DetChildrenModel {

    private String detId = "";
    private String projectProductId = "";
    private String detChildrenId = "";
    private String resourceId = "";
    private String detChildStart = "";
    private String detChildStop = "";
    private String detChildrenCheckIn = "";
    private String detChildrenCheckOut = "";
    private String detChildrenSolution = "";
    private String detChildCompleted = "";
    private String detChildrenStatusCode = "";
    private String description = "";

    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "\n  {\n" +
                        "  \"DetChildrenId\": \"" + detChildrenId + "\",\n" +
                        "  \"DetId\": \"" + detId + "\",\n" +
                        "  \"ResourceId\": \"" + resourceId + "\",\n" +
                        "  \"DetChildStart\": \"" + detChildStart + "\",\n" +
                        "  \"DetChildStop\": \"" + detChildStop + "\",\n" +
                        "  \"DetChildrenCheckIn\": \"" + detChildrenCheckIn + "\",\n" +
                        "  \"DetChildrenCheckOut\": \"" + detChildrenCheckOut + "\",\n" +
                        "  \"DetChildrenSolution\": \"" + detChildrenSolution + "\",\n" +
                        "  \"ProjectProductId\": \"" + projectProductId + "\",\n" +
                        "  \"DetChildrenStatusCode\": \"" + detChildrenStatusCode + "\",\n" +
                        "  \"Description\": \"" + description + "\",\n" +
                        "  \"DetChildCompleted\": " + detChildCompleted + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getDetChildrenId() {
        return detChildrenId;
    }

    public void setDetChildrenId(String detChildrenId) {
        this.detChildrenId = detChildrenId;
    }

    public String getDetId() {
        return detId;
    }

    public void setDetId(String detId) {
        this.detId = detId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDetChildStart() {
        return detChildStart;
    }

    public void setDetChildStart(String detChildStart) {
        this.detChildStart = detChildStart;
    }

    public String getDetChildStop() {
        return detChildStop;
    }

    public void setDetChildStop(String detChildStop) {
        this.detChildStop = detChildStop;
    }

    public String getDetChildrenCheckIn() {
        return detChildrenCheckIn;
    }

    public void setDetChildrenCheckIn(String detChildrenCheckIn) {
        this.detChildrenCheckIn = detChildrenCheckIn;
    }

    public String getDetChildrenCheckOut() {
        return detChildrenCheckOut;
    }

    public void setDetChildrenCheckOut(String detChildrenCheckOut) {
        this.detChildrenCheckOut = detChildrenCheckOut;
    }

    public String getDetChildrenSolution() {
        return detChildrenSolution;
    }

    public void setDetChildrenSolution(String detChildrenSolution) {
        this.detChildrenSolution = detChildrenSolution;
    }

    public String getProjectProductId() {
        return projectProductId;
    }

    public void setProjectProductId(String projectProductId) {
        this.projectProductId = projectProductId;
    }

    public String getDetChildrenStatusCode() {
        return detChildrenStatusCode;
    }

    public void setDetChildrenStatusCode(String detChildrenStatusCode) {
        this.detChildrenStatusCode = detChildrenStatusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getdetChildCompleted() {
        return detChildCompleted;
    }

    public void setDetChildCompleted(String detChildCompleted) {
        this.detChildCompleted = detChildCompleted;
    }

}
