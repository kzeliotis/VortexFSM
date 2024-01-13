package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

public class ManualModel {

    private String manualName = "";
    private String manualUrl = "";
    private String manualKeywords = "";
    private String blobAttachmentId = "";
    private String fileName = "";
    private String manualId = "";


    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"manualName\": \"" + manualName + "\",\n" +
                        "  \"manualUrl\": \"" + manualUrl + "\",\n" +
                        "  \"fileName\": \"" + fileName + "\",\n" +
                        "  \"ManualId\": \"" + manualId + "\",\n" +
                        "  \"BlobAttachmentId\": \"" + blobAttachmentId + "\",\n" +
                        "  \"manualKeywords\": \"" + manualKeywords + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getManualName() {
        return manualName;
    }

    public void setManualName(String manualName) {
        this.manualName = manualName;
    }

    public String getfileName() {
        return fileName;
    }

    public void setfileName(String FileName) {
        this.fileName = FileName;
    }

    public String getManualUrl() {
        return manualUrl;
    }

    public void setManualUrl(String manualUrl) {
        this.manualUrl = manualUrl;
    }

    public String getblobAttachmentId() {
        return blobAttachmentId;
    }

    public void setblobAttachmentId(String blobAttachmentId) {
        this.blobAttachmentId = blobAttachmentId;
    }

    public String getManualKeywords() {
        return manualKeywords;
    }

    public void setManualKeywords(String manualKeywords) {
        this.manualKeywords = manualKeywords;
    }

    public String getManualId() {
        return manualId;
    }

    public void setManualId(String ManualId) {
        this.manualId = ManualId;
    }

}
