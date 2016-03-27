package com.raxware.awsworkbench.ui.viewlets.s3;

import com.raxware.awsworkbench.model.s3.S3KeyEntry;

/**
 * Created by will on 3/24/2016.
 */
public interface S3Downloader {
    void downloadItem(S3KeyEntry s3KeyEntry);
}
