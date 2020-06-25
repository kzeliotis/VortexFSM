package am.gtest.vortex.models;

public class ProductTypeModel {

    private String typeId = "";
    private String typeColor = "";
    private String typeDescription = "";

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeColor() {
        return typeColor;
    }

    public void setTypeColor(String typeColor) {
        this.typeColor = typeColor;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                "  \"TypeId\": \"" + typeId + "\",\n" +
                "  \"color\": \"" + typeColor + "\",\n" +
                "  \"TypeDescription\": \"" + typeDescription + "\"\n" +
                "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }
}
