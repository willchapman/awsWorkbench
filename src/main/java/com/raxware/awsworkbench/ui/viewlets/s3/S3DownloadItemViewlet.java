package com.raxware.awsworkbench.ui.viewlets.s3;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.raxware.awsworkbench.model.menu.SimpleMenuItem;
import com.raxware.awsworkbench.ui.AwsTabView;
import com.raxware.awsworkbench.ui.tabs.s3.S3DownloadTab;
import com.raxware.awsworkbench.ui.viewlets.AwsViewlet;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Single entry for each download we start from S3.
 *
 * Created by will on 3/24/2016.
 */
public class S3DownloadItemViewlet extends AwsViewlet implements ProgressListener {

    /// The name of the item we are downloading
    private StringProperty targetName = new SimpleStringProperty();

    /// Where we are downloading the item too
    private StringProperty destination = new SimpleStringProperty();

    /// Current progress
    private DoubleProperty percentage = new SimpleDoubleProperty();

    /// The bucket we are downloading from
    private String bucket;

    /// Object path (full path of the object in the bucket)
    private String path;

    //
    // UI components
    private Label nameLabel = new Label("Unknown?");
    private ProgressBar progressBar = new ProgressBar();
    private ContextMenu contextMenu = null;
    private File saveDestination = null;

    //
    // AWS Download Objects
    TransferManager transferManager = new TransferManager(getShell().getAwsProxy().getCredentialsChain());
    Download thisDownload;

    //
    // Misc data
    private long totalBytes = 0;
    private long downloadedBytes = 0;

    public S3DownloadItemViewlet(AwsTabView awsTabView) {
        super(awsTabView);
        init();
    }

    /**
     * We are most likely in a S3DownloadTab instance, so for convienence we will provide this method
     * as an easy check.  This will throw an IllegalStateException if this is not the case.
     *
     * @return The containing tab as a S3DownloadTab if it is an instaoce of this type
     */
    private S3DownloadTab getDownloadTab() {
        if(getTab() instanceof S3DownloadTab)
            return (S3DownloadTab) getTab();
        throw new IllegalStateException("Not inside a S3Download tab");
    }

    /**
     * Sets up basic UI elements and bindings, context menu, etc.
     */
    private void init() {
        //
        // the name of the item
        nameLabel.textProperty().bindBidirectional(targetName);
        progressBar.progressProperty().bind(percentage);


        setLeft(nameLabel);
        setMargin(nameLabel, new Insets(0.0, 10.0, 0.0, 2.0));
        setCenter(progressBar);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        setOnContextMenuRequested(event -> showContextMenu(event));

    }

    /**
     * Call this to build and show the context menu for this download item.
     *
     * @param menuEvent The ContextMenuEvent object
     */
    private void showContextMenu(ContextMenuEvent menuEvent) {
        buildContextMenu().show(this, menuEvent.getScreenX(), menuEvent.getScreenY());
    }


    /**
     * Public interface to start the download.
     */
    public void download() {

        // maybe we should save this as the initial directory for the
        // next directory chooser?  Figure out global settings later

        startDownload(getSaveFolder());
    }

    /**
     * Form the S3 GetObjectRequest and start the download with the transfer manager.
     *
     * @param dirDestination The directory where whatever we are downloading will be saved.
     */
    private void startDownload(File dirDestination) {
        GetObjectRequest objectRequest = new GetObjectRequest(bucket, path);
        objectRequest.setKey(path);
        Download thisDownload = transferManager.download(objectRequest, new File(dirDestination,targetName.get()));

        thisDownload.addProgressListener(this);
    }

    /**
     * Returns the save destination, and will prompt the user for where to save it if it does not already have it set.
     *
     * @return
     */
    protected File getSaveFolder() {
        if(saveDestination == null) {
            saveDestination = new File(destination.getValueSafe());
            if(!saveDestination.exists() || !saveDestination.canWrite() || !saveDestination.isDirectory()) {
                saveDestination = askForDestinationFile();
            }
        }
        return saveDestination;
    }

    /**
     * Builds a simple ContextMenu for this download item.  This is lazy.
     *
     * @return
     */
    private ContextMenu buildContextMenu() {
        if(contextMenu== null) {

            contextMenu = new ContextMenu(
                new OpenFolder(nameLabel.getText(), getSaveFolder()),
                SimpleMenuItem.SEPERATOR_MENU_ITEM,
                new RemoveThyself()
            );

        }

        return contextMenu;
    }

    /**
     * Provides the user with a dialog of where to save the download item.
     *
     * @return Where to store the saved files.
     */
    private File askForDestinationFile() {
        File fileDestination = null;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose a destination...");
        fileDestination = directoryChooser.showDialog(null);
        if(fileDestination == null) {
            throw new IllegalStateException("Invalid directory choosen for download destination");
        }
        return fileDestination;
    }

    /**
     * Gets the value of the targetName property
     * @return
     */
    public String getTargetName() {
        return targetName.get();
    }

    /**
     * Accessor method for the targetName property - the name of the file we are saving
     * @return
     */
    public StringProperty targetNameProperty() {
        return targetName;
    }

    /**
     * Public setter for the targetName property
     * @param targetName
     */
    public void setTargetName(String targetName) {
        this.targetName.set(targetName);
    }

    /**
     * Gets the value for the destintion property
     * @return
     */
    public String getDestination() {
        return destination.get();
    }

    /**
     * Accessor method for the destination property - where we are saving the file to be downloaded
     * @return
     */
    public StringProperty destinationProperty() {
        return destination;
    }

    /**
     * Public setter for the destination property
     * @param destination
     */
    public void setDestination(String destination) {
        this.destination.set(destination);
    }

    /**
     * Gets the value of the percentage property
     * @return
     */
    public double getPercentage() {
        return percentage.get();
    }

    /**
     * The percentage prooperty - how far along are we in the download.
     * @return
     */
    public DoubleProperty percentageProperty() {
        return percentage;
    }

    /**
     * Sets the bucket field so we know how to build the GetObject request
     * @param bucket
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * This is the full path of the key in the bucket for the GetObject request
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Updated as the download progresses.  This will update the progress bar.
     * @param progressEvent
     */
    @Override
    public void progressChanged(ProgressEvent progressEvent) {

        if(progressEvent.getEventType() == ProgressEventType.RESPONSE_CONTENT_LENGTH_EVENT) {
            totalBytes = progressEvent.getBytes();
        } else if(progressEvent.getEventType() == ProgressEventType.RESPONSE_BYTE_TRANSFER_EVENT) {
            downloadedBytes += progressEvent.getBytes();
            percentageProperty().set((double)downloadedBytes/totalBytes);
        } else if(progressEvent.getEventType() == ProgressEventType.TRANSFER_COMPLETED_EVENT) {
            downloadedBytes = totalBytes; // just incase
            percentageProperty().set((double)downloadedBytes/totalBytes);
            nameLabel.setTextFill(Paint.valueOf("#0000AA"));
            transferManager.shutdownNow();
        } else {
            System.out.println(String.format("ProgressChanged: %s [%s/%s/%s] %d",
                    progressEvent.toString(),
                    progressEvent.getEventType().isTransferEvent(),
                    progressEvent.getEventType().isByteCountEvent(),
                    progressEvent.getEventType().isRequestCycleEvent(),
                    progressEvent.getBytesTransferred()
            ));
        }
    }


    /**
     * Context Menu Item for removing the item from the list
     */
    class RemoveThyself extends SimpleMenuItem {
        public RemoveThyself() {
            super("Remove");
        }

        protected void invoke(ActionEvent evt) {
            getDownloadTab().removeDownloadItem(S3DownloadItemViewlet.this);
        }
    }

    /**
     * Context Menu Item for opening the explorer to where this file was saved.  Right now uses the built-in Desktop API
     */
    class OpenFolder extends SimpleMenuItem {
        private final File destinationFolder;

        public OpenFolder(String name, File destinationFolder) {
            super(name);
            this.destinationFolder = destinationFolder;
        }

        @Override
        protected void invoke(ActionEvent actionEvent) {
            Desktop desktop = Desktop.getDesktop();

            //
            // in the future, we can handle this more gracefully, but for now we are just
            // going to throw an error
            if(!Desktop.isDesktopSupported()) {
                throw new UnsupportedOperationException("Desktop API is not supported");
            }

            if(destinationFolder.isDirectory()) {
                try {
                    desktop.open(destinationFolder);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to open folder", e);
                }
            }
        }
    }
}
