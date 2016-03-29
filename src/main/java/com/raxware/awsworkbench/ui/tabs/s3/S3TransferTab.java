package com.raxware.awsworkbench.ui.tabs.s3;

import com.raxware.awsworkbench.model.s3.S3KeyEntry;
import com.raxware.awsworkbench.ui.viewlets.s3.S3DownloadItemViewlet;
import com.raxware.awsworkbench.ui.viewlets.s3.S3TransferItemViewlet;
import com.raxware.awsworkbench.ui.viewlets.s3.S3UploadItemViewlet;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.io.File;

/**
 * Tab that shows the current transfers.  Its basically a list of S3DownloadItemViewlets and S3UploadItemViewlets
 * <p>
 * Created by will on 3/24/2016.
 */
public class S3TransferTab extends S3TabView {

    /// Shows the current downloads
    private ListView<S3TransferItemViewlet> currentTransfers = new ListView<>();

    /// The general layout
    private BorderPane layout = new BorderPane(currentTransfers);

    /// The tab text
    public static final String TAB_TEXT = "S3 Transfers";

    /**
     * Returns the BorderPane layout
     *
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

        currentTransfers.getItems().add(0, itemViewlet);
        itemViewlet.processRequest();  // We should maybe move this to an external transfer manager.
    }

    /**
     * Adds a new download to the list, and creates the S3UploadItemViewlet for the UI
     *
     * @param bucket
     * @param prefix
     * @param src
     */
    public void addUpload(String bucket, String prefix, File src) {
        S3UploadItemViewlet itemViewlet = new S3UploadItemViewlet(this);
        itemViewlet.setBucket(bucket);
        if (prefix.length() > 0)
            itemViewlet.setPath(prefix + File.separator + src.getName());
        else
            itemViewlet.setPath(src.getName());
        itemViewlet.setFilePath(src.getAbsolutePath());
        itemViewlet.setTargetName(src.getName());

        currentTransfers.getItems().add(0, itemViewlet);
        itemViewlet.processRequest(); // We should maybe move this to an external transfer manager
    }

    /**
     * Removes the specified item from the list.
     *
     * @param downloadItemViewlet
     */
    public void removeDownloadItem(S3DownloadItemViewlet downloadItemViewlet) {
        Platform.runLater(() -> currentTransfers.getItems().removeAll(downloadItemViewlet));
    }

    /**
     * Removes the specified upload item from the list
     *
     * @param uploadItemViewlet
     */
    public void removeUploadItem(S3UploadItemViewlet uploadItemViewlet) {
        Platform.runLater(() -> currentTransfers.getItems().removeAll(uploadItemViewlet));
    }
}
