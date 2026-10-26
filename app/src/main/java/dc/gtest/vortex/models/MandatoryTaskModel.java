package dc.gtest.vortex.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MandatoryTaskModel implements Cloneable {

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
                        "    \"IsOptional\": \"" + isOptional + "\",\n" +
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

    // Same keys as toString(), with raw values: org.json does the escaping, so ", \ and new lines reach the server as typed.
    // normalizeHasMeasurement: with the checkout the server deserializes the steps with Newtonsoft, where HasMeasurement is bool.
    public JSONObject toJSONObject(boolean normalizeHasMeasurement) throws JSONException {
        String hasMeasurementValue = nonNull(hasMeasurement);
        if (normalizeHasMeasurement) {
            String value = hasMeasurementValue.trim();
            hasMeasurementValue = String.valueOf(value.equals("1") || value.equalsIgnoreCase("true"));
        }

        JSONObject obj = new JSONObject();
        obj.put("StepSequence", nonNull(stepSequence));
        obj.put("StepDescription", nonNull(stepDescription));
        obj.put("MeasurementValue", nonNull(measurementValue));
        obj.put("MeasureableAttribute", nonNull(measurableAttribute));
        obj.put("HasMeasurement", hasMeasurementValue);
        obj.put("IsDateTime", nonNull(isDateTime));
        obj.put("IsOptional", nonNull(isOptional));
        obj.put("RequiresPhoto", nonNull(requiresPhoto));
        obj.put("StepPhotoPath", nonNull(stepPhotoPath));
        obj.put("ProductId", nonNull(ProductId));
        obj.put("Comments", nonNull(Comments));
        obj.put("SetToProject", nonNull(SetToProject));
        obj.put("StepId", nonNull(stepId));
        obj.put("StepImage", nonNull(stepPhoto));
        // a copy, so the JSONArray of this model is not shared (clone() is shallow)
        obj.put("ServiceStepDefaultValues", serviceStepDefaultValues == null ? new JSONArray() : new JSONArray(serviceStepDefaultValues.toString()));
        return obj;
    }

    public static JSONArray toJSONArray(List<MandatoryTaskModel> mandatoryTasks, boolean normalizeHasMeasurement) throws JSONException {
        JSONArray array = new JSONArray();
        for (MandatoryTaskModel mt : mandatoryTasks) {
            array.put(mt.toJSONObject(normalizeHasMeasurement));
        }
        return array;
    }

    private static String nonNull(String value) {
        return value == null ? "" : value;
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

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
