package com.raxware.awsworkbench.ui.viewlets.s3;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.raxware.awsworkbench.model.menu.SimpleMenuItem;
import com.raxware.awsworkbench.ui.AwsTabView;
import com.raxware.awsworkbench.ui.tabs.s3.S3TransferTab;
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
public class S3DownloadItemViewlet extends S3TransferItemViewlet implements ProgressListener {

    /// Where we are downloading the item too
    private StringProperty destination = new SimpleStringProperty();

    /// File reference to the destination property
    private File saveDestination = null;

    /// Download object from the AWS TransferManager
    Download thisDownload;

    public S3DownloadItemViewlet(AwsTabView awsTabView) {
        super(awsTabView);

    }

    /**
     * Invokes download()
     */
    @Override
    public void processRequest() {
        download();
    }

    /**
     * We are most likely in a S3TransferTab instance, so for convienence we will provide this method
     * as an easy check.  This will throw an IllegalStateException if this is not the case.
     *
     * @return The containing tab as a S3TransferTab if it is an instaoce of this type
     */
    private S3TransferTab getDownloadTab() {
        if (getTab() instanceof S3TransferTab)
            return (S3TransferTab) getTab();
        throw new IllegalStateException("Not inside a S3Download tab");
    }

    /**
     * Starts the download
     */
    private void download() {

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
        GetObjectRequest objectRequest = new GetObjectRequest(getBucket(), getPath());
        objectRequest.setKey(getPath());
        thisDownload = transferManager.download(objectRequest, new File(dirDestination, getTargetName()));

        thisDownload.addProgressListener(this);
    }

    /**
     * Returns the save destination, and will prompt the user for where to save it if it does not already have it set.
     *
     * @return
     */
    private File getSaveFolder() {
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
    protected ContextMenu buildContextMenu() {
        if(contextMenu== null) {

            contextMenu = new ContextMenu(
                    new OpenFolder(getTargetName(), getSaveFolder()),
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
