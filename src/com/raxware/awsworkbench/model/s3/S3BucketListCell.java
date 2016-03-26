package com.raxware.awsworkbench.model.s3;

import javafx.scene.control.ListCell;

/**
 * The Cell object for the ListView for the bucket list.
 *
 * Created by will on 3/20/2016.
 */
public class S3BucketListCell extends ListCell<S3BucketListEntry> {
    /**
     * Sets the cell to the name of the bucket
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(S3BucketListEntry item, boolean empty) {
        super.updateItem(item,empty);

        if(item == null || empty) {
            setText(null);
        } else {
            setText(item.getBucketName());

        }
    }
}
