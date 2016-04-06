package com.raxware.awsworkbench.ui.viewlets.s3;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.raxware.awsworkbench.ui.AwsTabView;
import com.raxware.awsworkbench.ui.tabs.s3.S3ExplorerTab;
import com.raxware.awsworkbench.ui.viewlets.AwsViewlet;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.paint.Paint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by will on 3/27/2016.
 */
public abstract class S3TransferItemViewlet extends AwsViewlet implements ProgressListener, TransferCompletedListener {

    /// The name of the item we are transferring (ie: a file name)
    private StringProperty targetName = new SimpleStringProperty();

    /// Current progress
    private DoubleProperty percentage = new SimpleDoubleProperty();

    /// The bucket we are downloading from
    private String bucket;

    /// Object path (full path of the object in the bucket)
    private String path;

    //
    // AWS Download Objects
    TransferManager transferManager = new TransferManager(getShell().getAwsProxy().getCredentialsChain());

    //
    // UI components
    private Label nameLabel = new Label("Unknown?");
    private ProgressBar progressBar = new ProgressBar();
    protected ContextMenu contextMenu = null;

    //
    // Misc data
    protected long totalBytes = 0;
    protected long downloadedBytes = 0;

    public List<TransferCompletedListener> transferCompletedListenerList = new LinkedList<>();


    /**
     * Creates the viewlet, but we need to know where we belong so the Viewlet is required to tell us the Tab instance
     * they belong too.
     *
     * @param awsTabView
     */
    public S3TransferItemViewlet(AwsTabView awsTabView) {
        super(awsTabView);
        init();
        addTransferCompletedListener(this);
    }

    public boolean addTransferCompletedListener(TransferCompletedListener transferCompletedListener) {
        if (transferCompletedListener != null)
            return transferCompletedListenerList.add(transferCompletedListener);
        else
            return false;
    }

    protected synchronized void fireTransferCompleted() {
        Iterator<TransferCompletedListener> listenerIterator = transferCompletedListenerList.iterator();
        while (listenerIterator.hasNext()) {
            try {
                listenerIterator.next().transferCompleted();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void transferCompleted() {
        AwsTabView explorerTab = getTab().getShell().getTab(S3ExplorerTab.class);
        if (explorerTab != null && explorerTab instanceof S3ExplorerTab) {
            explorerTab.refresh();
        }
    }

    protected ContextMenu buildContextMenu() {
        return null;
    }

    /**
     * Sets up basic UI elements and bindings, context menu, etc.
     */
    private void init() {
        //
        // the name of the item
        nameLabel.textProperty().bindBidirectional(targetNameProperty());
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
        ContextMenu menu = buildContextMenu();
        if (menu != null) {
            menu.show(this, menuEvent.getScreenX(), menuEvent.getScreenY());
        }

    }

    /**
     * Process the request for whatever this object needs to do.
     */
    public abstract void processRequest();

    /**
     * Gets the value of the targetName property
     *
     * @return
     */
    public String getTargetName() {
        return targetName.get();
    }

    /**
     * Accessor method for the targetName property - the name of the file we are saving
     *
     * @return
     */
    public StringProperty targetNameProperty() {
        return targetName;
    }

    /**
     * Public setter for the targetName property
     *
     * @param targetName
     */
    public void setTargetName(String targetName) {
        this.targetName.set(targetName);
    }

    /**
     * Gets the value of the percentage property
     *
     * @return
     */
    public double getPercentage() {
        return percentage.get();
    }

    /**
     * The percentage prooperty - how far along are we in the download.
     *
     * @return
     */
    public DoubleProperty percentageProperty() {
        return percentage;
    }

    /**
     * Sets the bucket field so we know how to build the GetObject request
     *
     * @param bucket
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * This is the full path of the key in the bucket for the GetObject request
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Updated as the download progresses.  This will update the progress bar.
     *
     * @param progressEvent
     */
    @Override
    public void progressChanged(ProgressEvent progressEvent) {

        if (progressEvent.getEventType() == ProgressEventType.RESPONSE_CONTENT_LENGTH_EVENT) {
            totalBytes = progressEvent.getBytes();
        } else if (progressEvent.getEventType() == ProgressEventType.RESPONSE_BYTE_TRANSFER_EVENT) {
            downloadedBytes += progressEvent.getBytes();
            percentageProperty().set((double) downloadedBytes / totalBytes);
        } else if (progressEvent.getEventType() == ProgressEventType.TRANSFER_COMPLETED_EVENT) {
            downloadedBytes = totalBytes; // just incase
            percentageProperty().set((double) downloadedBytes / totalBytes);
            nameLabel.setTextFill(Paint.valueOf("#0000AA"));
            transferManager.shutdownNow();
            fireTransferCompleted();
        } else {
//            System.out.println(String.format("ProgressChanged: %s [%s/%s/%s] %d",
//                    progressEvent.toString(),
//                    progressEvent.getEventType().isTransferEvent(),
//                    progressEvent.getEventType().isByteCountEvent(),
//                    progressEvent.getEventType().isRequestCycleEvent(),
//                    progressEvent.getBytesTransferred()
//            ));
        }
    }

    public String getPath() {
        return path;
    }

    public String getBucket() {
        return bucket;
    }

    protected ProgressBar getProgressBar() {
        return progressBar;
    }
}
