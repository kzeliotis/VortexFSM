package dc.gtest.vortex.models;

import dc.gtest.vortex.support.MyPrefs;

public class NewAssignmentModel {

    private String customerId = "";
    private String customerName = "";
    private String customerContact = "";
    private String currentTime = "";
    private String dateStart = "";
    private String dateEnd = "";
    private String problem = "";
    private String projectId = "";
    private String projectDescription = "";  // not required for new assignment field. Used to show project name
    private String projectProductId = "";
    private String productId = "";
    private String productDescription = "";  // not required for new assignment field. Used to show project name
    private String serviceId = "";
    private String serviceDescription = "";
    private String resourceIds = "";
    private String resourceNames = "";
    private String AssignmentId = "0";
    private String assignmentType = "";
    private String assignmentTypeDescription = "";
    private String AssignmentSourceProcedure = "";
    private final String AssignmentSource = "Mobile";

    @Override
    public String toString() {

        return "{\n" +
                "  \"AssignmentId\": \"" + AssignmentId + "\",\n" +
                "  \"CustomerId\": \"" + customerId + "\",\n" +
                "  \"CustomerCode\": \"\",\n" +
                "  \"CustomerName\": \"" + customerName + "\",\n" +
                "  \"CustomerContact\": \"" + customerContact + "\",\n" +
                "  \"Techs\": \"\",\n" +
                "  \"CallTime\": \"" + currentTime + "\",\n" +
                "  \"AcceptTime\": \"\",\n" +
                "  \"DateStart\": \"" + dateStart + "\",\n" +
                "  \"DateEnd\": \"" + dateEnd + "\",\n" +
                "  \"Problem\": \"" + problem + "\",\n" +
                "  \"Hours\": \"0\",\n" +
                "  \"ProjectId\": \"" + projectId + "\",\n" +
                "  \"ProjectCode\": \"\",\n" +
                "  \"ProductCode\": \"\",\n" +
                "  \"ProjectProductId\": \"" + projectProductId + "\",\n" +
                "  \"ProductId\": \"" + productId + "\",\n" +
                "  \"AttributeValue\": \"\",\n" +
                "  \"ServiceId\": \"" + serviceId + "\",\n" +
                "  \"ServiceCode\": \"\",\n" +
                "  \"ServiceDescription\": \"" + serviceDescription + "\",\n" +
                "  \"IsRecurring\": \"\",\n" +
                "  \"RecurEvery\": \"0\",\n" +
                "  \"AdditionalResourceId\": \"\",\n" +
                "  \"ProjectMobilePointId\": \"\",\n" +
                "  \"MasterProjectId\": \"\",\n" +
                "  \"PrepayId\": \"\",\n" +
                "  \"DetType\": \"" + assignmentType + "\",\n" +
                "  \"DetTypeDescription\": \"" + assignmentTypeDescription + "\",\n" +
                "  \"ERPID\": \"\",\n" +
                "  \"UserId\": \"" + MyPrefs.getString(MyPrefs.PREF_USERID, "0") + "\",\n" +
                "  \"AssignmentSource\": \"" + AssignmentSource + "\",\n" +
                "  \"AssignmentSourceProcedure\": \"" + AssignmentSourceProcedure + "\",\n" +
                "  \"ResourceIds\": \"" + resourceIds + "\"\n" +
                "}";
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setAssignmentId(String assignmentId) {
        this.AssignmentId = assignmentId;
    }

    public String getAssignmentTypeDescription() {
        return assignmentTypeDescription;
    }

    public void setAssignmentTypeDescription(String AssignmentTypeDescription) {
        this.assignmentTypeDescription = AssignmentTypeDescription;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String AssignmentType) {
        this.assignmentType = AssignmentType;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectProductId() {
        return projectProductId;
    }

    public void setProjectProductId(String projectProductId) {
        this.projectProductId = projectProductId;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getResourceNames() {
        return resourceNames;
    }

    public void setResourceNames(String resourceNames) {
        this.resourceNames = resourceNames;
    }

    public void setAssignmentSourceProcedure(String assignmentSourceProcedure) {
        this.AssignmentSourceProcedure = assignmentSourceProcedure;
    }

}
