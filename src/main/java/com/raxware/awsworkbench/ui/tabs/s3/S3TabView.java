package com.raxware.awsworkbench.ui.tabs.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.raxware.awsworkbench.ui.AwsTabView;

/**
 * A tab that works with S3.
 *
 * Created by will on 3/19/2016.
 */
public abstract class S3TabView extends AwsTabView {
    /**
     * Convience method for getting a new AmazonS3 client from the AwsProxy.
     *
     * @return
     */
    protected AmazonS3 s3() {
        return awsWorkbenchShell.getAwsProxy().s3();
    }
}
