package com.raxware.awsworkbench.ui.tabs.s3;

import com.raxware.awsworkbench.model.s3.S3KeyEntry;
import com.raxware.awsworkbench.ui.viewlets.s3.S3BucketBrowserViewlet;
import com.raxware.awsworkbench.ui.viewlets.s3.S3BucketListViewlet;
import com.raxware.awsworkbench.ui.viewlets.s3.S3Downloader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

/**
 * This tab provides a means of browsing (using a semi-explorer type layout) the S3 contents.
 *
 * Created by will on 3/20/2016.
 */
public class S3ExplorerTab extends S3TabView  implements S3Downloader {

    /// The general layout pane
    private BorderPane explorerPane = new BorderPane();

    /// The current bucket, selected from the bucket list.
    private Label activeBucketLabel = new Label("No bucket selected");

    /// Shows the current prefix (or path) of what where we are in the bucket.
    private TextField pathBar = new TextField("?");

    /// Allows for scrolling
    private ScrollPane scrollPane = new ScrollPane();

    /// The viewlet for showing the buckets.
    private S3BucketListViewlet bucketListViewlet = new S3BucketListViewlet(this);

    /// The viewet for the object browser
    private S3BucketBrowserViewlet bucketBrowserViewlet = new S3BucketBrowserViewlet(this);

    public S3ExplorerTab() {

        BorderPane topTemp = new BorderPane();
        topTemp.setLeft(activeBucketLabel);
        BorderPane.setMargin(activeBucketLabel, new Insets(3.0, 6.0, 3.0, 3.0));
        topTemp.setCenter(pathBar);
        explorerPane.setTop(topTemp);

        explorerPane.setLeft(bucketListViewlet);
        explorerPane.setCenter(bucketBrowserViewlet);

        activeBucketLabel.textProperty().bind(bucketListViewlet.selectedBucket);
        bucketBrowserViewlet.activeBucketProperty().bind(bucketListViewlet.selectedBucket);
        pathBar.textProperty().bindBidirectional(bucketBrowserViewlet.currentPrefixProperty());

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(explorerPane);
    }

    /**
     * Starts a download.
     *
     * @param s3KeyEntry
     */
    public void downloadItem(S3KeyEntry s3KeyEntry) {
        Objects.requireNonNull(s3KeyEntry, "Unable to download null object");

        S3DownloadTab s3DownloadTab = (S3DownloadTab) getShell().getTab(S3DownloadTab.class);
        if(s3DownloadTab == null) {
            s3DownloadTab = (S3DownloadTab) getShell().addTab(S3DownloadTab.class, S3DownloadTab.TAB_TEXT, true);
        }

        s3DownloadTab.addDownload(activeBucketLabel.getText(), s3KeyEntry);
    }

    /**
     * Synchronizes the buckets
     */
    @Override
    public void added() {
        bucketListViewlet.syncBuckets();
    }

    /**
     * The scroll pane.
     *
     * @return
     */
    @Override
    protected Node getTabContent() {
        return scrollPane;
    }
}
