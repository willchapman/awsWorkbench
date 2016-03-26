package com.raxware.awsworkbench;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Created by will on 3/19/2016.
 */
public class AwsProxy {

    public AmazonS3 s3(){
        return new AmazonS3Client(getCredentialsChain());
    }

    /**
     * Supplements the default provider chain by looking for credentials using the profile provider, with the
     * profile name of "s3workbench".
     *
     * @return
     */
    public AWSCredentialsProviderChain getCredentialsChain() {
        return new AWSCredentialsProviderChain(
                new ProfileCredentialsProvider("s3workbench"),
                new DefaultAWSCredentialsProviderChain()
        );
    }
}
