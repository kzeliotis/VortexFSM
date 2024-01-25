package dc.gtest.vortex.models;

public class MasterProjectModel {

    private String masterProjectDescription = "";
    private String masterProjectId = "";


    @Override
    public String toString() {

        String modelToString =
                "{\n" +
                        "  \"MasterProjectId\": \"" + masterProjectId + "\",\n" +
                        "  \"MasterProjectDescription\": " + masterProjectDescription + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getMasterProjectDescription() {
        return masterProjectDescription;
    }

    public void setMasterProjectDescription(String MasterProjectDescription) {
        this.masterProjectDescription = MasterProjectDescription;
    }

    public String getMasterProjectId() {
        return masterProjectId;
    }

    public void setMasterProjectId(String MasterProjectId) {
        this.masterProjectId = MasterProjectId;
    }

}
