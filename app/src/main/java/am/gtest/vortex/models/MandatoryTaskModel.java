package am.gtest.vortex.models;

import org.json.JSONArray;

public class MandatoryTaskModel {

    private String stepSequence = "";
    private String stepDescription = "";
    private String measurementValue = "";
    private String measurableAttribute = "";
    private String hasMeasurement = "";
    private String requiresPhoto = "";
    private String stepPhotoPath = "";
    private String stepPhoto = "";
    private String SetToProject = "";
    private String ProductId = "";
    private String Comments = "";
    private String isDateTime = "";
    private String isOptional = "";
    private String stepId = "";
    private JSONArray serviceStepDefaultValues = new JSONArray();

    @Override
    public String toString() {

        String defaultValues = "";

        try {
            defaultValues = serviceStepDefaultValues.toString(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String modelToString =
                "\n  {\n" +
                        "    \"StepSequence\": \"" + stepSequence + "\",\n" +
                        "    \"StepDescription\": \"" + stepDescription + "\",\n" +
                        "    \"MeasurementValue\": \"" + measurementValue + "\",\n" +
                        "    \"MeasureableAttribute\": \"" + measurableAttribute + "\",\n" +
                        "    \"HasMeasurement\": \"" + hasMeasurement + "\",\n" +
                        "    \"IsDateTime\": \"" + isDateTime + "\",\n" +
                        "    \"isOptional\": \"" + isOptional + "\",\n" +
                        "    \"RequiresPhoto\": \"" + requiresPhoto + "\",\n" +
                        "    \"StepPhotoPath\": \"" + stepPhotoPath + "\",\n" +
                        "    \"ProductId\": \"" + ProductId + "\",\n" +
                        "    \"Comments\": \"" + Comments + "\",\n" +
                        "    \"SetToProject\": \"" + SetToProject + "\",\n" +
                        "    \"StepId\": \"" + stepId + "\",\n" +
                        "    \"StepImage\": \"" + stepPhoto + "\",\n" +
                        "    \"ServiceStepDefaultValues\": " + defaultValues + "\n" +
                        "  }";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getStepSequence() {
        return stepSequence;
    }

    public void setStepSequence(String stepSequence) {
        this.stepSequence = stepSequence;
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public String getStepComments() {
        return Comments;
    }

    public void setStepComments(String Comments) {
        this.Comments = Comments;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(String measurementValue) {
        this.measurementValue = measurementValue;
    }

    public String getMeasurableAttribute() {
        return measurableAttribute;
    }

    public void setMeasurableAttribute(String measurableAttribute) {
        this.measurableAttribute = measurableAttribute;
    }

    public String getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(String isOptional) {
        this.isOptional = isOptional;
    }

    public String getIsDateTime() {
        return isDateTime;
    }

    public void setIsDateTime(String isDateTime) {
        this.isDateTime = isDateTime;
    }

    public String getHasMeasurement() {
        return hasMeasurement;
    }

    public void setHasMeasurement(String hasMeasurement) {
        this.hasMeasurement = hasMeasurement;
    }

    public String getRequiresPhoto() {
        return requiresPhoto;
    }

    public void setRequiresPhoto(String requiresPhoto) {
        this.requiresPhoto = requiresPhoto;
    }

    public String getStepPhotoPath() {
        return stepPhotoPath;
    }

    public void setStepPhotoPath(String stepPhotoPath) {
        this.stepPhotoPath = stepPhotoPath;
    }

    public String getStepPhoto() {
        return stepPhoto;
    }

    public void setStepPhoto(String stepPhoto) {
        this.stepPhoto = stepPhoto;
    }

    public String getProductId() { return ProductId; }

    public void setProductId(String productId){ this.ProductId = productId; }

    public String getSetToProject() {return SetToProject;}

    public void setSetToProject(String setToProject){ this.SetToProject = setToProject; }

    public JSONArray getServiceStepDefaultValues() {
        return serviceStepDefaultValues;
    }

    public void setServiceStepDefaultValues(JSONArray serviceStepDefaultValues) {
        this.serviceStepDefaultValues = serviceStepDefaultValues;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String StepId) {
        this.stepId = StepId;
    }



}
