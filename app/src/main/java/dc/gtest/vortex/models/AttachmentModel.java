package dc.gtest.vortex.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;

public class AttachmentModel {

    private String objectId = "";
    private String objectType = "";
    private String filename = "";
    private String cloudFileURL = "";
    private String attachmentId = "";
    private String blobAttachmentId = "";

    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "\n  {\n" +
                        "  \"ObjectId\": \"" + objectId + "\",\n" +
                        "  \"ObjectType\": \"" + objectType + "\",\n" +
                        "  \"Filename\": \"" + filename + "\",\n" +
                        "  \"CloudFileURL\": \"" + cloudFileURL + "\",\n" +
                        "  \"AttachmentId\": \"" + attachmentId + "\",\n" +
                        "  \"BlobAttachmentId\": \"" + blobAttachmentId + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String ObjectId) {
        this.objectId = ObjectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String ObjectType) {
        this.objectType = ObjectType;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String Filename) {
        this.filename = Filename;
    }

    public String getCloudFileURL() {
        return cloudFileURL;
    }

    public void setCloudFileURL(String CloudFileURL) {
        this.cloudFileURL = CloudFileURL;
    }


    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String AttachmentId) {
        this.attachmentId = AttachmentId;
    }

    public String getBlobAttachmentId() {
        return blobAttachmentId;
    }

    public void setBlobAttachmentId(String BlobAttachmentId) {
        this.blobAttachmentId = BlobAttachmentId;
    }

}
