package am.gtest.vortex.models;

import org.json.JSONArray;

public class AssignmentTypeModel {
    private String assignmentTypeDescription = "";
    private String assignmentTypeId = "";


    @Override
    public String toString() {

        String modelToString =
                "{\n" +
                        "  \"AssignmentTypeId\": \"" + assignmentTypeId + "\",\n" +
                        "  \"AssignmentTypeDescription\": " + assignmentTypeDescription + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getAssignmentTypeDescription() {
        return assignmentTypeDescription;
    }

    public void setAssignmentTypeDescription(String AssignmentTypeDescription) {
        this.assignmentTypeDescription = AssignmentTypeDescription;
    }

    public String getAssignmentTypeId() {
        return assignmentTypeId;
    }

    public void setAssignmentTypeId(String AssignmentTypeId) {
        this.assignmentTypeId = AssignmentTypeId;
    }

}
