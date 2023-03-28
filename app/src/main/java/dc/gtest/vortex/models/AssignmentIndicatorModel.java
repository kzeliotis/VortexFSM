package dc.gtest.vortex.models;

import java.util.List;

public class AssignmentIndicatorModel {

    private String assignmentIndicatorId = "";
    private String assignmentIndicatorDescription = "";
    private boolean isChecked = false;

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"AssignmentIndicatorId\": \"" + assignmentIndicatorId + "\",\n" +
                        "  \"AssignmentIndicatorDescription\": \"" + assignmentIndicatorDescription + "\",\n" +
                        "  \"isChecked\": " + isChecked + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getAssignmentIndicatorId() {
        return assignmentIndicatorId;
    }

    public void setAssignmentIndicatorId(String AssignmentIndicatorId) {
        this.assignmentIndicatorId = AssignmentIndicatorId;
    }

    public String getAssignmentIndicatorDescription() {
        return assignmentIndicatorDescription;
    }

    public void setAssignmentIndicatorDescription(String AssignmentIndicatorDescription) {
        this.assignmentIndicatorDescription = AssignmentIndicatorDescription;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }



}
