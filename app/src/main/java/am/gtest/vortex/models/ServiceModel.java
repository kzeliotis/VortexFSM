package am.gtest.vortex.models;

public class ServiceModel {

    private String serviceId = "";
    private String serviceCode = "";
    private String serviceDescription = "";
    private String serviceDuration = "";

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                "  \"serviceId\": \"" + serviceId + "\",\n" +
                "  \"serviceCode\": \"" + serviceCode + "\",\n" +
                "  \"serviceDescription\": \"" + serviceDescription + "\",\n" +
                "  \"serviceDuration\": \"" + serviceDuration + "\"\n" +
                "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(String serviceDuration) {
        this.serviceDuration = serviceDuration;
    }
}
