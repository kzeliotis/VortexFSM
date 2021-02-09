package am.gtest.vortex.models;

public class ResourceLeaveModel {

    private String leaveStart = "";
    private String leaveEnd = "";

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"LeaveStart\": \"" + leaveStart + "\",\n" +
                        "  \"LeaveEnd\": " + leaveEnd + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getLeaveStart() {
        return leaveStart;
    }

    public void setLeaveStart(String LeaveStart) {
        this.leaveStart = LeaveStart;
    }

    public String getLeaveEnd() {
        return leaveEnd;
    }

    public void setLeaveEnd(String LeaveEnd) {
        this.leaveEnd = LeaveEnd;
    }

}
