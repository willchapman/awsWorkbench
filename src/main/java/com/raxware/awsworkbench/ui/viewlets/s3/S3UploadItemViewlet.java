package com.raxware.awsworkbench.ui.viewlets.s3;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.raxware.awsworkbench.ui.AwsTabView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;

/**
 * Created by will on 3/29/2016.
 */
public class S3UploadItemViewlet extends S3TransferItemViewlet {

    /// Path of the file (or directory) we are uploading
    public StringProperty filePath = new SimpleStringProperty("?");


    private Upload thisUpload = null;
    private MultipleFileUpload multipleFileUpload = null;

    /**
     * Creates the viewlet, but we need to know where we belong so the Viewlet is required to tell us the Tab instance
     * they belong too.
     *
     * @param awsTabView
     */
    public S3UploadItemViewlet(AwsTabView awsTabView) {
        super(awsTabView);
    }

    /**
     * Calls upload()
     */
    @Override
    public void processRequest() {
        try {
            File toUpload = new File(filePath.get());
            getProgressBar().setStyle("-fx-accent: forestgreen;");
            upload(toUpload);
        } catch (Exception e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    /**
     * Starts the upload process.  Does some santy checking, then calls uploadFile or uploadDirectory depending
     * on which is appropiate.
     * s
     */
    private void upload(File toUpload) throws IOException {
        if (toUpload == null)
            throw new NullPointerException("Unable to upload null");

        if (!toUpload.exists())
            throw new IOException("File/Directory does not exist, unable to upload");

        if (!toUpload.canRead())
            throw new IOException("Unable to read file/directory, unable to upload");

        if (toUpload.isDirectory()) {
            uploadDirectory(toUpload);
        } else if (toUpload.isFile()) {
            uploadFile(toUpload);
        } else {
            throw new IllegalArgumentException("Can only upload files or directories");
        }
    }

    /**
     * Construct the PutObjectRequest to upload a file
     *
     * @param file The file to upload
     */
    private void uploadFile(File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(getBucket(), getPath(), file);
        thisUpload = transferManager.upload(putObjectRequest);
        thisUpload.addProgressListener(this);
    }

    private void uploadDirectory(File dir) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getFilePath() {
        return filePath.get();
    }

    public StringProperty filePathProperty() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }
}


