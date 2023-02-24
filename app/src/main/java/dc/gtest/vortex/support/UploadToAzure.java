package dc.gtest.vortex.support;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.util.UUID;
import java.io.File;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;

import static dc.gtest.vortex.support.MyGlobals.globalCurrentAttachmentPath;

public class UploadToAzure extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;
    private String prefKey;

    public UploadToAzure(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... arg0) {

        try {



            // Setup the cloud storage account.
            CloudStorageAccount account = CloudStorageAccount
                    .parse("");

            // Create a blob service client
            CloudBlobClient blobClient = account.createCloudBlobClient();

            // Get a reference to a container
            // The container name must be lower case
            // Append a random UUID to the end of the container name so that
            // this sample can be run more than once in quick succession.
            CloudBlobContainer container = blobClient.getContainerReference("detattachments");

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
            prefKey = arg0[0];
            String fileInfo = MyPrefs.getStringWithFileName(MyPrefs.PREF_FILE_ATTACHMENT_FOR_SYNC, prefKey, "");
            String assignmentId = fileInfo.split("|")[0];
            String filePath = fileInfo.split("|")[1];
            int lastIndex = filePath.lastIndexOf("/");
            String _file = filePath.substring(lastIndex + 1);
            String ext = _file.split(".")[1];
            String cloudBlobName = String.format("{0}_{1}.{2}.{3}", assignmentId, _file.split(".")[0], UUID.randomUUID().toString().substring(0,8).replace("-", ""), ext);
            CloudBlockBlob blob1 = container
                    .getBlockBlobReference(cloudBlobName);

            // Upload text to the blob
            File source = new File(filePath);
            blob1.upload(new FileInputStream(source), 0);
            // Delete the blobs
            //blob1.deleteIfExists();

        } catch (Throwable t) {
            t.printStackTrace();
        }

        //act.printSampleCompleteInfo("BlobBasics");

        return null;
    }
}