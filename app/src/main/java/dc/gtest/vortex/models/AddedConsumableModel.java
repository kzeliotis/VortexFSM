package dc.gtest.vortex.models;

import dc.gtest.vortex.support.MyUtils;

public class AddedConsumableModel {

    private String name = "";
    private String notes = "";
    private String suggested = "";
    private String used = "";
    private int ProductId = 0;
    private String warehouseId = "0";
    private String stock = "0";
    private int detPickingId = 0;
    private String projectWarehouseId = "0";

    @Override
    public String toString() {
        String modelToString =
                "\n{\n" +
                        "  \"name\": \"" + name + "\",\n" +
                        "  \"notes\": \"" + notes + "\",\n" +
                        "  \"suggested\": \"" + suggested + "\",\n" +
                        "  \"ProductId\": \"" + ProductId  + "\",\n" +
                        "  \"DetPickingId\": \"" + detPickingId  + "\",\n" +
                        "  \"WarehouseId\": \"" + warehouseId  + "\",\n" +
                        "  \"projectWarehouseId\": \"" + projectWarehouseId  + "\",\n" +
                        "  \"Stock\": \"" + stock  + "\",\n" +
                        "  \"used\": \"" + used + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = MyUtils.ToJson(name);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = MyUtils.ToJson(notes);
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

    public int getDetPickingId() {
        return detPickingId;
    }

    public void setDetPickingId(int DetPickingId) {
        this.detPickingId = DetPickingId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String WarehouseId) {
        this.warehouseId = WarehouseId;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String Stock) {
        this.stock = Stock;
    }

    public String getProjectWarehouseId() {
        return projectWarehouseId;
    }

    public void setProjectWarehouseId(String projectWarehouseId) {
        this.projectWarehouseId = projectWarehouseId;
    }
}
