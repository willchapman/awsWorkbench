package com.raxware.awsworkbench.ui.tabs.s3;

import com.raxware.awsworkbench.model.s3.S3KeyEntry;
import com.raxware.awsworkbench.ui.viewlets.s3.S3DownloadItemViewlet;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

/**
 * Tab that shows the currently downloads.  Its basically a list of S3DownloadItemViewlets.
 *
 * Created by will on 3/24/2016.
 */
public class S3DownloadTab extends S3TabView {

    /// Shows the current downloads
    private ListView<S3DownloadItemViewlet> currentDownloads = new ListView<>();

    /// The general layout
    private BorderPane layout = new BorderPane(currentDownloads);

    /// The tab text
    public static final String TAB_TEXT = "S3 Download";

    /**
     * Returns the BorderPane layout
     * @return
     */
    @Override
    protected Node getTabContent() {
        return layout;
    }

    /**
     * Adds a new download to the list, and creates the S3DownloadItemViewlet for the UI
     *
     * @param bucket
     * @param downloadMe
     */
    public void addDownload(String bucket, S3KeyEntry downloadMe) {
        S3DownloadItemViewlet itemViewlet = new S3DownloadItemViewlet(this);
        itemViewlet.destinationProperty().set("D:\\temp");
        itemViewlet.setTargetName(downloadMe.getDisplayName(true));
        itemViewlet.setBucket(bucket);
        itemViewlet.setPath(downloadMe.getPath());

        currentDownloads.getItems().add(0, itemViewlet );
        itemViewlet.download();
    }

    /**
     * Removes the specified item from the list.
     *
     * @param downloadItemViewlet
     */
    public void removeDownloadItem(S3DownloadItemViewlet downloadItemViewlet) {
        Platform.runLater(() -> currentDownloads.getItems().removeAll(downloadItemViewlet));
    }
}
