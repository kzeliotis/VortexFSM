package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.support.MyUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProductModel {

    private String productDescription = "";
    private String installationDate = "";
    private String projectProductId = "";
    private String typeDescription = "";
    private String identityValue = "";
    private String notes = "";
    private String productAttributesString = "";
    private String projectInstallationId = "";
    private boolean isNotSynchronized = false; // not server field. Added to mark not synchronized data in red color
    private boolean isChecked = false;
    private String masterId = "";
    private List<AttributeModel> productAttributes = new ArrayList<>();
    private List<MeasurementModel> RemoveFromProjectMandatory = new ArrayList<>();
    @Getter
    @Setter
    private String productComponentId = "";
    private String projectZoneDescription = "";
    private String projectInstallationDescription = "";
    private String prefKey = "";
    private String CustomerName = "";
    private String CustomerId = "";
    private String ProjectDescription = "";
    private String ProjectId = "";


    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "\n{\n" +
                        "  \"ProductDescription\": \"" + MyUtils.ToJson(productDescription) + "\",\n" +
                        "  \"InstallationDate\": \"" + installationDate + "\",\n" +
                        "  \"ProjectProductId\": \"" + projectProductId + "\",\n" +
                        "  \"InstallationId\": \"" + projectInstallationId + "\",\n" +
                        "  \"TypeDescription\": \"" + MyUtils.ToJson(typeDescription) + "\",\n" +
                        "  \"IdentityValue\": \"" + identityValue + "\",\n" +
                        "  \"isNotSynchronized\": \"" + isNotSynchronized + "\",\n" +
                        "  \"isChecked\": " + isChecked + ",\n" +
                        "  \"ProductAttributesString\": \"" + productAttributesString + "\",\n" +
                        "  \"notes\": \"" + notes + "\",\n" +
                        "  \"MasterId\": \"" + masterId + "\",\n" +
                        "  \"ProductComponentId\": \"" + productComponentId + "\",\n" +
                        "  \"prefKey\": \"" + prefKey + "\",\n" +
                        "  \"ProductAttributeValues\": " + productAttributes + ",\n" +
                        "  \"RemoveFromProjectMandatory\": " + RemoveFromProjectMandatory + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getProjectZoneDescription() {
        return projectZoneDescription;
    }

    public void setProjectZoneDescription(String projectZoneDescription) {
        this.projectZoneDescription = projectZoneDescription;
    }

    public String getProjectInstallationDescription() {
        return projectInstallationDescription;
    }

    public void setProjectInstallationDescription(String projectInstallationDescription) {
        this.projectInstallationDescription = projectInstallationDescription;
    }

    public String getPrefKey() {
        return prefKey;
    }

    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }
}
