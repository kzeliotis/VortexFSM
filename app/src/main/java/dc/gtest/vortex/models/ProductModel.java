package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ProductModel {

    private String productDescription = "";
    private String installationDate = "";
    private String projectProductId = "";
    private String typeDescription = "";
    private String identityValue = "";
    private String productAttributesString = "";
    private String projectInstallationId = "";
    private boolean isNotSynchronized = false; // not server field. Added to mark not synchronized data in red color
    private boolean isChecked = false;
    private String masterId = "";
    private List<AttributeModel> productAttributes = new ArrayList<>();
    private List<MeasurementModel> RemoveFromProjectMandatory = new ArrayList<>();

    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "\n{\n" +
                        "  \"ProductDescription\": \"" + productDescription + "\",\n" +
                        "  \"InstallationDate\": \"" + installationDate + "\",\n" +
                        "  \"ProjectProductId\": \"" + projectProductId + "\",\n" +
                        "  \"InstallationId\": \"" + projectInstallationId + "\",\n" +
                        "  \"TypeDescription\": \"" + typeDescription + "\",\n" +
                        "  \"IdentityValue\": \"" + identityValue + "\",\n" +
                        "  \"isNotSynchronized\": \"" + isNotSynchronized + "\",\n" +
                        "  \"isChecked\": " + isChecked + "\",\n" +
                        "  \"ProductAttributesString\": \"" + productAttributesString + "\",\n" +
                        "  \"MasterId\": \"" + masterId + "\",\n" +
                        "  \"ProductAttributeValues\": " + productAttributes + "\",\n" +
                        "  \"RemoveFromProjectMandatory\": " + RemoveFromProjectMandatory + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String MasterId) {
        this.masterId = MasterId;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public String getProjectProductId() {
        return projectProductId;
    }

    public void setProjectProductId(String projectProductId) {
        this.projectProductId = projectProductId;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getIdentityValue() {
        return identityValue;
    }

    public void setIdentityValue(String identityValue) {
        this.identityValue = identityValue;
    }

    public boolean isNotSynchronized() {
        return isNotSynchronized;
    }

    public void setNotSynchronized(boolean notSynchronized) {
        isNotSynchronized = notSynchronized;
    }

    public List<AttributeModel> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(List<AttributeModel> productAttributes) {
        this.productAttributes = productAttributes;
    }

    public void setRemoveFromProjectMandatory(List<MeasurementModel> RemoveFromProjectMandatory) {
        this.RemoveFromProjectMandatory = RemoveFromProjectMandatory;
    }

    public String getProductAttributesString() {
        return productAttributesString;
    }

    public void setProductAttributesString(String ProductAttributesString) {
        this.productAttributesString = ProductAttributesString;
    }

    public String getProjectInstallationId() {
        return projectInstallationId;
    }

    public void setProjectInstallationId(String ProjectInstallationId) {
        this.projectInstallationId = ProjectInstallationId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
