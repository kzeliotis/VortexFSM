package am.gtest.vortex.models;

public class ProjectModel {

    private String projectId = "";
    private String projectDescription = "";
    private String projectProducts = "";

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void clearModel(){
        this.projectId = "";
        this.projectDescription = "";
        this.projectProducts = "";
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectProducts() {
        return projectProducts;
    }

    public void setProjectProducts(String projectProducts) {
        this.projectProducts = projectProducts;
    }

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"ProjectDescription\": \"" + projectDescription + "\",\n" +
                        "  \"ProjectProducts\": \"" + projectProducts + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }
}
