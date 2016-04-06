package com.raxware.awsworkbench.ui.tabs.s3;

import com.raxware.awsworkbench.model.s3.S3KeyEntry;
import com.raxware.awsworkbench.ui.viewlets.s3.S3BucketBrowserViewlet;
import com.raxware.awsworkbench.ui.viewlets.s3.S3BucketListViewlet;
import com.raxware.awsworkbench.ui.viewlets.s3.S3Downloader;
import com.raxware.awsworkbench.ui.viewlets.s3.S3Uploader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.util.Objects;

/**
 * This tab provides a means of browsing (using a semi-explorer type layout) the S3 contents.
 *
 * Created by will on 3/20/2016.
 */
public class S3ExplorerTab extends S3TabView
        implements S3Downloader, S3Uploader, EventHandler<KeyEvent>, ChangeListener<String> {

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

    private StringProperty currentPathProp = new SimpleStringProperty("?");

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

        currentPathProp.bindBidirectional(bucketBrowserViewlet.currentPrefixProperty());
        // nice idea, but I can't type in the text field if its bound.
        //pathBar.textProperty().bind(currentPathProp);
        currentPathProp.addListener(this);
        pathBar.setOnKeyReleased(this);

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

        getTransferTab().addDownload(activeBucketLabel.getText(), s3KeyEntry);
    }

    public void uploadItem(File toUpload) {
        Objects.requireNonNull(toUpload, "Unable to upload null object");
        String path = pathBar.getText() == null ? "" : pathBar.getText();
        getTransferTab().addUpload(activeBucketLabel.getText(), path, toUpload);
    }

    /**
     * Convenience method to get the containing tab as a S3TransferTab and to add it if it doesn't exist.
     *
     * @return
     */
    private S3TransferTab getTransferTab() {
        S3TransferTab s3TransferTab = (S3TransferTab) getShell().getTab(S3TransferTab.class);
        if (s3TransferTab == null) {
            s3TransferTab = (S3TransferTab) getShell().addTab(S3TransferTab.class, S3TransferTab.TAB_TEXT, true);
        }
        return s3TransferTab;
    }

    @Override
    public void refresh() {
        super.refresh();

        bucketBrowserViewlet.refresh();
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

    /**
     * Looks for the enter key to know when we want to set the path manually
     *
     * @param event
     */
    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            updateCurrentPrefix(pathBar.getText() == null ? "" : pathBar.getText());
            event.consume();
        }

    }

    /**
     * Updates the current prefix from whatever the source, either a manual typing in the path bar
     * or a double click in th browser window
     *
     * @param txt
     */
    private void updateCurrentPrefix(String txt) {
        //String txt = pathBar.getText() == null ? "" : pathBar.getText();
        if (!txt.endsWith("/") && txt.length() > 1)
            txt += "/";

        currentPathProp.set(txt);
        pathBar.setText(txt);
    }

    /**
     * Called when the currentPathProp is changed, so we can synchronize the pathbar.
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        updateCurrentPrefix(newValue == null ? "" : newValue);
    }
}
