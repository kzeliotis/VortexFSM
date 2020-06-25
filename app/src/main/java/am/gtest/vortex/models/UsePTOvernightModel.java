package am.gtest.vortex.models;

public class UsePTOvernightModel {

    private String assignmentId = "";
    private String transitDateTime = "";
    private String transitLat = "";
    private String transitLon = "";
    private int transitStatus;
    private boolean isOvernight;

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"AssignmentId\": \"" + assignmentId + "\",\n" +
                        "  \"TransitDateTime\": \"" + transitDateTime + "\",\n" +
                        "  \"TransitLat\": \"" + transitLat + "\",\n" +
                        "  \"TransitLon\": \"" + transitLon + "\",\n" +
                        "  \"TransitStatus\": " + transitStatus + ",\n" +
                        "  \"Overnight\": " + isOvernight + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getTransitDateTime() {
        return transitDateTime;
    }

    public void setTransitDateTime(String transitDateTime) {
        this.transitDateTime = transitDateTime;
    }

    public String getTransitLat() {
        return transitLat;
    }

    public void setTransitLat(String transitLat) {
        this.transitLat = transitLat;
    }

    public String getTransitLon() {
        return transitLon;
    }

    public void setTransitLon(String transitLon) {
        this.transitLon = transitLon;
    }

    public int getTransitStatus() {
        return transitStatus;
    }

    public void setTransitStatus(int transitStatus) {
        this.transitStatus = transitStatus;
    }

    public boolean isOvernight() {
        return isOvernight;
    }

    public void setOvernight(boolean overnight) {
        isOvernight = overnight;
    }
}
