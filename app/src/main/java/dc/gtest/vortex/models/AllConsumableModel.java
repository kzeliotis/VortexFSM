package dc.gtest.vortex.models;

public class AllConsumableModel {

    private String consumableName = "";
    private String notes = "";
    private int ProductId = 0;
    private int typeId = -1;
    private String Stock = "";

    public String getConsumableName() {
        return consumableName;
    }

    public void setConsumableName(String consumableName) {
        this.consumableName = consumableName;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getStock() { return Stock;}

    public void setStock(String Stock) { this.Stock = Stock; }

    @Override
    public String toString() {
        String modelToString =
                "\n{\n" +
                        "  \"ProductDescription\": \"" + consumableName + "\",\n" +
                        "  \"notes\": \"" + notes + "\",\n" +
                        "  \"ProductId\": \"" + ProductId  + "\",\n" +
                        "  \"Stock\": \"" + Stock + "\",\n" +
                        "  \"TypeId\": " + typeId + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }
}
