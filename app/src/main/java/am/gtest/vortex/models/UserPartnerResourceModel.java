package am.gtest.vortex.models;

import java.util.List;

public class UserPartnerResourceModel {

    private String resourceId = "";
    private String resourceName = "";
    private boolean isChecked = false;
    private List<ResourceLeaveModel> resourceLeaves;

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                "  \"resourceId\": \"" + resourceId + "\",\n" +
                "  \"resourceName\": \"" + resourceName + "\",\n" +
                "  \"isChecked\": " + isChecked + "\",\n" +
                "  \"Leaves\": " + resourceLeaves + "\n" +
                "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public List<ResourceLeaveModel> getLeaves() {
        return resourceLeaves;
    }

    public void setLeaves(List<ResourceLeaveModel> ResourceLeaves) {
        resourceLeaves = ResourceLeaves;
    }

}
