package com.raxware.awsworkbench.ui.viewlets.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.raxware.awsworkbench.model.s3.S3BucketListCell;
import com.raxware.awsworkbench.model.s3.S3BucketListEntry;
import com.raxware.awsworkbench.ui.AwsTabView;
import com.raxware.awsworkbench.ui.viewlets.AwsViewlet;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.stream.Collectors;

/**
 * The list that shows the available buckets
 *
 * Created by will on 3/20/2016.
 */
public class S3BucketListViewlet extends AwsViewlet implements ListChangeListener<S3BucketListEntry> {
    /// Is there a bucket selected, which is our 'active' bucket
    private Label activeBucket = new Label("No active bucket");

    /// The actual UI component
    private ListView<S3BucketListEntry> bucketList = new ListView<>();

    /// The list of buckets
    private ObservableList<S3BucketListEntry> buckets = FXCollections.observableArrayList();

    /// Public property of the currently selected bucket
    public StringProperty selectedBucket = new ReadOnlyStringWrapper();

    public S3BucketListViewlet(AwsTabView s3ExplorerTab) {
        super(s3ExplorerTab);
        setCenter(bucketList);
        bucketList.setCellFactory(param -> new S3BucketListCell());
        bucketList.getSelectionModel().getSelectedItems().addListener(this);
    }

    /**
     * This will clear the list of buckets, and re-send the ListBuckets command to populate the list view
     * with whatever comes back.
     */
    public void syncBuckets() {
        buckets.clear();

        AmazonS3 s3Client = s3();
        ListBucketsRequest listBucketRequest = new ListBucketsRequest();

        buckets.addAll(
                s3Client.listBuckets(listBucketRequest)
                        .stream().map(S3BucketListEntry::new).collect(Collectors.toList())
        );

        bucketList.setItems(buckets);
    }

    /**
     * Public accessor for the selected bucket
     * @return
     */
    public String getSelectedBucket() {
        return selectedBucket.get();
    }

    /**
     * The public property for the active bucket selected in the list view.
     * @return
     */
    public StringProperty selectedBucketProperty() {
        return selectedBucket;
    }

    /**
     * Called when a user clicks on a bucket, updates the active bucket property
     * @param c
     */
    @Override
    public void onChanged(Change c) {
        S3BucketListEntry t = (S3BucketListEntry) c.getList().get(0);
        selectedBucket.set( t.getBucketName() );
    }
}
