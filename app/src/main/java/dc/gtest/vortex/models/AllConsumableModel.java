package dc.gtest.vortex.models;

public class AllConsumableModel {

    private String consumableName = "";
    private String notes = "";
    private int ProductId = 0;
    private int detPickingId = 0;
    private int typeId = -1;
    private String Stock = "";
    private String used = "";
    private String barcode = "";

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

    public int getDetPickingId() {
        return detPickingId;
    }

    public void setDetPickingId(int DetPickingId) {
        this.detPickingId = DetPickingId;
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

    public String getUsed() { return used;}

    public void setUsed(String Used) { this.used = Used; }

    @Override
    public String toString() {
        String modelToString =
                "\n{\n" +
                        "  \"ProductDescription\": \"" + consumableName + "\",\n" +
                        "  \"notes\": \"" + notes + "\",\n" +
                        "  \"ProductId\": \"" + ProductId  + "\",\n" +
                        "  \"DetPickingId\": \"" + detPickingId  + "\",\n" +
                        "  \"Stock\": \"" + Stock + "\",\n" +
                        "  \"Used\": \"" + used + "\",\n" +
                        "  \"TypeId\": " + typeId + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
