package dc.gtest.vortex.support;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.net.URI;
import java.util.UUID;
import java.io.File;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;

import dc.gtest.vortex.api.SendAttachment;

import static dc.gtest.vortex.support.MyGlobals.globalCurrentAttachmentPath;
import static dc.gtest.vortex.support.MyPrefs.PREF_AZURE_CONNECTION_STRING;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ATTACHMENT_FOR_SYNC;

public class UploadToAzure extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;
    private String prefKey;

    public UploadToAzure(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... arg0) {

        CloudBlockBlob blob1 = null;
        String postBody = "";
        prefKey = arg0[0];

        String fileInfo = MyPrefs.getStringWithFileName(MyPrefs.PREF_FILE_ATTACHMENT_FOR_SYNC, prefKey, "");

        if (!fileInfo.contains("CloudFilename")){
            try {
                // Setup the cloud storage account.
                String conStr = MyPrefs.getString(PREF_AZURE_CONNECTION_STRING, "");
                CloudStorageAccount account = CloudStorageAccount
                        .parse(conStr);

                // Create a blob service client
                CloudBlobClient blobClient = account.createCloudBlobClient();

                // Get a reference to a container
                // The container name must be lower case
                // Append a random UUID to the end of the container name so that
                // this sample can be run more than once in quick succession.
                String containerName = prefKey.startsWith("-") ? "calendarevents" : "detattachments";
                CloudBlobContainer container = blobClient.getContainerReference(containerName);

                // Create the container if it does not exist
                container.createIfNotExists();

                // Make the container public
                // Create a permissions object
                BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

                // Include public access in the permissions object
                containerPermissions
                        .setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

                // Set the permissions on the container
                container.uploadPermissions(containerPermissions);

                // Upload 3 blobs
                // Get a reference to a blob in the container

                String assignmentId = fileInfo.split("\\|")[0];
                String filePath = fileInfo.split("\\|")[1];
                String filesize = fileInfo.split("\\|")[2];
                int lastIndex = filePath.lastIndexOf("/");
                String attachmentFileName = filePath.substring(lastIndex + 1);
                String _file = filePath.substring(lastIndex + 1);
                String ext = _file.substring(_file.lastIndexOf('.') + 1); //_file.split("\\.");
                String cloudBlobName = String.format("%s_%s.%s.%s", assignmentId.replace("-", ""), _file.split("\\.")[0], UUID.randomUUID().toString().substring(0,8).replace("-", ""), ext);
                blob1 = container
                        .getBlockBlobReference(cloudBlobName);

                // Upload text to the blob
                File source = new File(filePath);
                blob1.upload(new FileInputStream(source), Long.parseLong(filesize));
                URI fileuri = blob1.getUri();
                String fileURL = fileuri.toURL().toString();
                // Delete the blobs
                //blob1.deleteIfExists();
                String objectType = assignmentId.contains("-") ? "CalendarEvents" : "DetAttachments";
                String objectTable = assignmentId.contains("-") ? "CalendarEventAttachments" : "DetAttachments";
                String ObjectIdField = assignmentId.contains("-") ? "CalendarEventId" : "DetId";
                String ObjectFilenameField = assignmentId.contains("-") ? "FileName" : "Attachment";

                postBody = "{\n" +
                        "  \"ObjectId\": \"" + assignmentId.replace("-", "") + "\",\n" +
                        "  \"Filename\": \"" + attachmentFileName + "\",\n" +
                        "  \"CloudFilename\": \"" + cloudBlobName + "\",\n" +
                        "  \"CloudFileURL\": \"" + fileURL + "\",\n" +
                        "  \"ObjectType\": \"" + objectType + "\",\n" +
                        "  \"ObjectTable\": \"" + objectTable + "\",\n" +
                        "  \"ObjectIdField\": \"" + ObjectIdField + "\",\n" +
                        "  \"ObjectFilenameField\": \"" + ObjectFilenameField + "\",\n" +
                        "  \"InsertToDatabase\": true,\n" +
                        "  \"CompanyId\": 1,\n" +
                        "  \"FileSize\": \"" + filesize + "\"\n" +
                        "}";

                MyPrefs.setStringWithFileName(PREF_FILE_ATTACHMENT_FOR_SYNC, prefKey, postBody);


            } catch (Throwable t) {
                t.printStackTrace();
                try{
                    if(blob1 != null){
                        blob1.deleteIfExists();
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        } else {
            postBody = fileInfo;
        }

        if(postBody.length()>0){
            SendAttachment sendAttachment = new SendAttachment(ctx);
            sendAttachment.execute(prefKey);
        }

        //act.printSampleCompleteInfo("BlobBasics");

        return null;
    }
}