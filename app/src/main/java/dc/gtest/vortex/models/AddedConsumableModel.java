package dc.gtest.vortex.models;

public class AddedConsumableModel {

    private String name = "";
    private String notes = "";
    private String suggested = "";
    private String used = "";
    private int ProductId = 0;
    private String warehouseId = "0";

    @Override
    public String toString() {
        String modelToString =
                "\n{\n" +
                        "  \"name\": \"" + name + "\",\n" +
                        "  \"notes\": \"" + notes + "\",\n" +
                        "  \"suggested\": \"" + suggested + "\",\n" +
                        "  \"ProductId\": \"" + ProductId  + "\",\n" +
                        "  \"WarehouseId\": \"" + warehouseId  + "\",\n" +
                        "  \"used\": \"" + used + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSuggested() {
        return suggested;
    }

    public void setSuggested(String suggested) {
        this.suggested = suggested;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String WarehouseId) {
        this.warehouseId = WarehouseId;
    }
}
