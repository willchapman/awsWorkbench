package com.raxware.awsworkbench.model.s3;

import com.amazonaws.services.s3.model.Bucket;

/**
 * The object that contains the necessary information for the bucket list (right now just the bucket name)
 *
 * Created by will on 3/20/2016.
 */
public class S3BucketListEntry {
    /// The name of the bucket
    private final String bucketName;

    public S3BucketListEntry(Bucket bucket) {
        this.bucketName = bucket.getName();
    }

    /**
     * Accessor method for the bucketName field
     *
     * @return
     */
    public String getBucketName() { return bucketName; }

}
