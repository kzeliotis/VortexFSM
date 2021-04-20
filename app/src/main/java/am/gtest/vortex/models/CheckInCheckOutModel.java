package am.gtest.vortex.models;

import androidx.annotation.NonNull;

public class CheckInCheckOutModel {

    private String checkInTime = "";
    private String checkOutTime = "";
    private String startTravelTime = "";
    private String startWorkTime = "";
    private String solution = "";
    private String notes = "";
    private String chargedAmount = "";
    private String paidAmount = "";
    private String status = "";
    private String assignmentId = "";
    private String signatureName = "";
    private String encodedSignature = "";
    private String signatureEmail = "";
    private String startLat = "";
    private String startLng = "";
    private String checkInLat = "";
    private String checkInLng = "";
    private String checkOutLat = "";
    private String checkOutLng = "";
    private String photos = "";
    private final String assignmentSource = "Mobile";
    private String returnToBase = "";
    private String statusCode = "";


    @NonNull
    @Override
    public String toString() {
        String checkInCheckOutModel = "{\n" +
                "  \"CheckIn\": \"" + checkInTime + "\",\n" +
                "  \"CheckOut\": \"" + checkOutTime + "\",\n" +
                "  \"StartTravel\": \"" + startTravelTime + "\",\n" +
                "  \"StartWork\": \"" + startWorkTime + "\",\n" +
                "  \"Solution\": \"" + solution + "\",\n" +
                "  \"Notes\": \"" + notes + "\",\n" +
                "  \"Charge\": \"" + chargedAmount + "\",\n" +
                "  \"Payment\": \"" + paidAmount + "\",\n" +
                "  \"Status\": \"" + status + "\",\n" +
                "  \"StatusCode\": \"" + statusCode + "\",\n" +
                "  \"AssignmentId\": \"" + assignmentId + "\",\n" +
                "  \"SignatureName\": \"" + signatureName + "\",\n" +
                "  \"SignatureEmail\": \"" + signatureEmail + "\",\n" +
                "  \"AssignmentSource\": \"" + assignmentSource + "\",\n" +
                "  \"StartLat\": \"" + startLat + "\",\n" +
                "  \"StartLng\": \"" + startLng + "\",\n" +
                "  \"CheckInLat\": \"" + checkInLat + "\",\n" +
                "  \"CheckInLng\": \"" + checkInLng + "\",\n" +
                "  \"CheckOutLat\": \"" + checkOutLat + "\",\n" +
                "  \"CheckOutLng\": \"" + checkOutLng + "\",\n" +
                "  \"ReturnToBase\": \"" + returnToBase + "\",\n" +
                "  \"Signature\": \"" + encodedSignature + "\",\n" +
                "  \"Photos\": [" + photos + "]" + "\n" +
                "}";

        checkInCheckOutModel = checkInCheckOutModel.replace("}]", "}\n  ]");
        return checkInCheckOutModel;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getStartTravelTime() {
        return startTravelTime;
    }

    public void setStartTravelTime(String startTravelTime) {
        this.startTravelTime = startTravelTime;
    }

    public String getStartWorkTime() {
        return startWorkTime;
    }

    public void setStartWorkTime(String startWorkTime) {
        this.startWorkTime = startWorkTime;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getSignatureName() {
        return signatureName;
    }

    public void setSignatureName(String signatureName) {
        this.signatureName = signatureName;
    }

    public String getSignatureEmail(){return signatureEmail;}

    public void setSignatureEmail(String signatureEmail) {this.signatureEmail = signatureEmail;}

    public String getEncodedSignature() {
        return encodedSignature;
    }

    public void setEncodedSignature(String encodedSignature) {
        this.encodedSignature = encodedSignature;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLng() {
        return startLng;
    }

    public void setStartLng(String startLng) {
        this.startLng = startLng;
    }

    public String getCheckInLat() {
        return checkInLat;
    }

    public void setCheckInLat(String checkInLat) {
        this.checkInLat = checkInLat;
    }

    public String getCheckInLng() {
        return checkInLng;
    }

    public void setCheckInLng(String checkInLng) {
        this.checkInLng = checkInLng;
    }

    public String getCheckOutLat() {
        return checkOutLat;
    }

    public void setCheckOutLat(String checkOutLat) {
        this.checkOutLat = checkOutLat;
    }

    public String getCheckOutLng() {
        return checkOutLng;
    }

    public void setCheckOutLng(String checkOutLng) {
        this.checkOutLng = checkOutLng;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public void setReturnToBaseTime(String returnToBaseTime) {
        this.returnToBase = returnToBaseTime;
    }

    public String getReturnToBaseTime() {
        return returnToBase;
    }

}
