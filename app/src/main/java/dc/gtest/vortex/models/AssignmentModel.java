package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AssignmentModel {

    private String projectDescription = "";
    private String productDescription = "";
    private String serviceDescription = "";
    private String startDateTime = "";
    private String startDate = "";
    private String assignmentTime = "";
    private String timeTo = "";
    private String contact = "";
    private String address = "";
    private float distance;
    private String cabLat = "";
    private String cabLng = "";
    private String projectId = "";
    private String assignmentId = "";
    private String masterAssignment = "";
    private String problem = "";
    private String phone = "";
    private String mobile = "";
    private String products = "";
    private String assignmentType = "";
    private String customerName = "";
    private String customerBusiness = "";
    private String customerVatNumber = "";
    private String customerRevenue = "";
    private String sorting = "";
    private String statusName = "";
    private String statusId = "";
    private String customFields = "";
    private String pickingList = "";
    private String commentsSolution = "";
    private String notes = "";
    private String chargedAmount = "";
    private String paidAmount = "";
    private String StatusColor = "";
    private String SignatureName = "";
    private String ProposedCheckOutStatus = "0";
    private String installationWarning = "";
    private String projectInstallationDescription = "";
    private String mandatoryZoneMeasurementsService = "0";
    private String resourceId = "";
    private String contract = "";
    private String additionalTechnicians = "";
    private String minimumPayment = "";

    private String ProjectLat = "0";
    private String ProjectLon = "";
    private String ProjectAddress = "";
    private String ProjectCity = "";
    private String DateStart = "";
    private String DateEnd = "";
    private String LockStatusChange = "";
    private String correlatedStatuses = "";

    private JSONArray mandatoryTasks = new JSONArray();
    private List<AttachmentModel> attachments = new ArrayList<>();

    @NonNull
    @Override
    public String toString() {
        return
                "\"projectDescription\": \"" + projectDescription + "\",\n" +
                        "\"productDescription\": \"" + productDescription + "\",\n" +
                        "\"serviceDescription\": \"" + serviceDescription + "\",\n" +
                        "\"startDateTime\": \"" + startDateTime + "\",\n" +
                        "\"startDate\": \"" + startDate + "\",\n" +
                        "\"assignmentTime\": \"" + assignmentTime + "\",\n" +
                        "\"timeTo\": \"" + timeTo + "\",\n" +
                        "\"contact\": \"" + contact + "\",\n" +
                        "\"address\": \"" + address + "\",\n" +
                        "\"distance\": \"" + distance + "\",\n" +
                        "\"cabLat\": \"" + cabLat + "\",\n" +
                        "\"cabLng\": \"" + cabLng + "\",\n" +
                        "\"projectId\": \"" + projectId + "\",\n" +
                        "\"assignmentId\": \"" + assignmentId + "\",\n" +
                        "\"masterAssignment\": \"" + masterAssignment + "\",\n" +
                        "\"problem\": \"" + problem + "\",\n" +
                        "\"phone\": \"" + phone + "\",\n" +
                        "\"mobile\": \"" + mobile + "\",\n" +
                        "\"products\": \"" + products + "\",\n" +
                        "\"assignmentType\": \"" + assignmentType + "\",\n" +
                        "\"customerName\": \"" + customerName + "\",\n" +

                        "\"ProjectLat\": \"" + ProjectLat + "\",\n" +
                        "\"ProjectLon\": \"" + ProjectLon + "\",\n" +
                        "\"ProjectAddress\": \"" + ProjectAddress + "\",\n" +
                        "\"ProjectCity\": \"" + ProjectCity + "\",\n" +
                        "\"DateStart\": \"" + DateStart + "\",\n" +
                        "\"DateEnd\": \"" + DateEnd + "\",\n" +

                        "\"customerBusiness\": \"" + customerBusiness + "\",\n" +
                        "\"CustomerVatNumber\": \"" + customerVatNumber + "\",\n" +
                        "\"CustomerRevenue\": \"" + customerRevenue + "\",\n" +
                        "\"sorting\": \"" + sorting + "\",\n" +
                        "\"statusName\": \"" + statusName + "\",\n" +
                        "\"statusId\": \"" + statusId + "\",\n" +
                        "\"StatusColor\": \"" + StatusColor + "\",\n" +
                        "\"CustomFields\": \"" + customFields + "\",\n" +
                        "\"pickingList\": \"" + pickingList + "\",\n" +
                        "\"ProposedCheckOutStatus\": \"" + ProposedCheckOutStatus + "\",\n" +
                        "\"ProjectInstallationDescription\": \"" + projectInstallationDescription + "\",\n" +
                        "\"MandatoryZoneMeasurementsService\": \"" + mandatoryZoneMeasurementsService + "\",\n" +
                        "\"InstallationWarning\": \"" + installationWarning + "\",\n" +
                        "\"SignatureName\": \"" + SignatureName + "\",\n" +
                        "\"additionalTechnicians\": \"" + additionalTechnicians + "\",\n" +
                        "\"ResourceId\": \"" + resourceId + "\",\n" +
                        "\"Contract\": \"" + contract + "\",\n" +
                        "\"commentsSolution\": \"" + commentsSolution + "\",\n" +
                        "\"LockStatusChange\": \"" + LockStatusChange + "\",\n" +
                        "\"CorrelatedStatuses\": \"" + correlatedStatuses + "\",\n" +
                        "\"MinimumPayment\": \"" + minimumPayment + "\",\n" +
                        "\"Attachments\": " + attachments + "\n"
                ;
    }


    public String getLockStatusChange() {
        return LockStatusChange;
    }

    public void setLockStatusChange(String LockStatusChange) {
        this.LockStatusChange = LockStatusChange;
    }

    public String getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(String MinimumPayment) {
        this.minimumPayment = MinimumPayment;
    }

    public String getProjectLat() {
        return ProjectLat;
    }

    public void setProjectLat(String ProjectLat) {
        this.ProjectLat = ProjectLat;
    }

    public String getProjectLon() {
        return ProjectLon;
    }

    public void setProjectLon(String ProjectLon) {
        this.ProjectLon = ProjectLon;
    }

    public String getProjectAddress() {
        return ProjectAddress;
    }

    public void setProjectAddress(String ProjectAddress) {
        this.ProjectAddress = ProjectAddress;
    }

    public String getProjectCity() {
        return ProjectCity;
    }

    public void setProjectCity(String ProjectCity) {
        this.ProjectCity = ProjectCity;
    }

    public String getDateStart() {
        return DateStart;
    }

    public void setDateStart(String DateStart) {
        this.DateStart = DateStart;
    }

    public String getDateEnd() {
        return DateEnd;
    }

    public void setDateEnd(String DateEnd) {
        this.DateEnd = DateEnd;
    }

    ///

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getInstallationWarning() {
        return installationWarning;
    }

    public void setInstallationWarning(String InstallationWarning) {
        this.installationWarning = InstallationWarning;
    }

    public String getProjectInstallationDescription() {
        return projectInstallationDescription;
    }

    public void setProjectInstallationDescription(String ProjectInstallationDescription) {
        this.projectInstallationDescription = ProjectInstallationDescription;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getAssignmentTime() {
        return assignmentTime;
    }

    public void setAssignmentTime(String assignmentTime) {
        this.assignmentTime = assignmentTime;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getCabLat() {
        return cabLat;
    }

    public void setCabLat(String cabLat) {
        this.cabLat = cabLat;
    }

    public String getCabLng() {
        return cabLng;
    }

    public void setCabLng(String cabLng) {
        this.cabLng = cabLng;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getMasterAssignment() {
        return masterAssignment;
    }

    public void setMasterAssignment(String masterAssignment) {
        this.masterAssignment = masterAssignment;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerBusiness() {
        return customerBusiness;
    }

    public void setCustomerBusiness(String customerBusiness) {
        this.customerBusiness = customerBusiness;
    }

    public String getCustomerVatNumber() {
        return customerVatNumber;
    }

    public void setCustomerVatNumber(String customerVatNumber) {
        this.customerVatNumber = customerVatNumber;
    }

    public String getCustomerRevenue() {
        return customerRevenue;
    }

    public void setCustomerRevenue(String customerRevenue) {
        this.customerRevenue = customerRevenue;
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusColor() {
        return StatusColor;
    }

    public void setStatusColor(String StatusColor) {
        this.StatusColor = StatusColor;
    }

    public String getSignatureName() { return SignatureName; }

    public void setSignatureName(String SignatureName) { this.SignatureName = SignatureName; }

    public String getAdditionalTechnicians() { return additionalTechnicians; }

    public void setAdditionalTechnicians(String AdditionalTechnicians) { this.additionalTechnicians = AdditionalTechnicians; }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getCustomFields() {
        return customFields;
    }

    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }

    public String getPickingList() {
        return pickingList;
    }

    public void setPickingList(String pickingList) {
        this.pickingList = pickingList;
    }

    public String getCommentsSolution() {
        return commentsSolution;
    }

    public void setCommentsSolution(String commentsSolution) {
        this.commentsSolution = commentsSolution;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getChargedAmount() {
        return chargedAmount;
    }

    public void setChargedAmount(String chargedAmount) {
        this.chargedAmount = chargedAmount;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public JSONArray getMandatoryTasks() {
        return mandatoryTasks;
    }

    public void setMandatoryTasks(JSONArray mandatoryTasks) {
        this.mandatoryTasks = mandatoryTasks;
    }

    public String getProposedCheckOutStatus() {
        return ProposedCheckOutStatus;
    }

    public void setProposedCheckOutStatus(String ProposedCheckOutStatus) {this.ProposedCheckOutStatus = ProposedCheckOutStatus;}


    public String getMandatoryZoneMeasurementsService() {
        return mandatoryZoneMeasurementsService;
    }

    public void setMandatoryZoneMeasurementsService(String MandatoryZoneMeasurementsService) {this.mandatoryZoneMeasurementsService = MandatoryZoneMeasurementsService;}


    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String ResourceId) {
        this.resourceId = ResourceId;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String Contract) {
        this.contract = Contract;
    }

    public List<AttachmentModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentModel> Attachments) {
        this.attachments = Attachments;
    }

    public String getCorrelatedStatuses() {
        return correlatedStatuses;
    }

    public void setCorrelatedStatuses(String CorrelatedStatuses) {
        this.correlatedStatuses = CorrelatedStatuses;
    }

}
