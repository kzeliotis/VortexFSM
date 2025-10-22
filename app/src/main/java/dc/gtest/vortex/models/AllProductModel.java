package dc.gtest.vortex.models;

public class AllProductModel {

    private String productId = "";
    private String productDescription = "";
    private String notes = "";
    private String ProjectProductId = "";
    private String Stock = "";
    private String BasicValue = "";
    private String AttributeId = "";
    private String AttributeDescription = "";
    private String masterProductComponentId = "";
    private String barcode = "";
    private int typeId = -1;

    @Override
    public String toString() {
        String modelToString =
                "\n{\n" +
                        "  \"ProductId\": \"" + productId + "\",\n" +
                        "  \"ProductDescription\": \"" + productDescription + "\",\n" +
                        "  \"notes\": \"" + notes + "\",\n" +
                        "  \"ProjectProductId\": \"" + ProjectProductId + "\",\n" +
                        "  \"Stock\": \"" + Stock + "\",\n" +
                        "  \"BasicValue\": \"" + BasicValue + "\",\n" +
                        "  \"AttributeId\": \"" + AttributeId + "\",\n" +
                        "  \"Barcode\": \"" + barcode + "\",\n" +
                        "  \"MasterProductComponentId\": \"" + masterProductComponentId + "\",\n" +
                        "  \"AttributeDescription\": \"" + AttributeDescription + "\",\n" +
                        "  \"TypeId\": " + typeId + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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

    public String getProjectProductId() { return ProjectProductId;}

    public void setProjectProductId(String ProjectProductId) { this.ProjectProductId = ProjectProductId; }

    public String getStock() { return Stock;}

    public void setStock(String Stock) { this.Stock = Stock; }

    public String getBasicValue() { return BasicValue;}

    public void setBasicValue(String BasicValue) { this.BasicValue = BasicValue; }

    public String getAttributeId() { return AttributeId;}

    public void setAttributeId(String AttributeId) { this.AttributeId = AttributeId; }

    public String getAttributeDescription() { return AttributeDescription;}

    public void setAttributeDescription(String AttributeDescription) { this.AttributeDescription = AttributeDescription; }


    public String getMasterProductComponentId() {
        return masterProductComponentId;
    }

    public void setMasterProductComponentId(String masterProductComponentId) {
        this.masterProductComponentId = masterProductComponentId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
