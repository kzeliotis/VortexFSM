package am.gtest.vortex.models;

import androidx.annotation.NonNull;

public class StatusModel {

    private String statusDescription = "";
    private String statusId = "";
    private int isPending = -1;
    private int isRollback = -1;
    private int mandatorySteps = -1;

    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"StatusDescription\": \"" + statusDescription + "\",\n" +
                        "  \"StatusId\": \"" + statusId + "\",\n" +
                        "  \"IsPending\": " + isPending + ",\n" +
                        "  \"MandatorySteps\": " + mandatorySteps + ",\n" +
                        "  \"IsRollback\": " + isRollback + "\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public int getIsPending() {
        return isPending;
    }

    public void setIsPending(int isPending) {
        this.isPending = isPending;
    }

    public int getIsRollback() {
        return isRollback;
    }

    public void setIsRollback(int isRollback) {
        this.isRollback = isRollback;
    }

    public int getMandatorySteps() {
        return mandatorySteps;
    }

    public void setMandatorySteps(int mandatorySteps) {
        this.mandatorySteps = mandatorySteps;
    }
}
