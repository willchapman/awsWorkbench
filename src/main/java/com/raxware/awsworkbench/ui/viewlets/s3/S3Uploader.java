package com.raxware.awsworkbench.ui.viewlets.s3;

import java.io.File;

/**
 * Identifies any object that is able to upload a file to S3.
 * <p>
 * Created by will on 3/29/2016.
 */
public interface S3Uploader {
    public void uploadItem(File file);
}
