package com.raxware.awsworkbench.ui.viewlets.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.raxware.awsworkbench.model.s3.S3KeyEntry;
import com.raxware.awsworkbench.model.s3.S3KeyTableCell;
import com.raxware.awsworkbench.ui.AwsTabView;
import com.raxware.awsworkbench.ui.dialogs.ErrorDialog;
import com.raxware.awsworkbench.ui.viewlets.AwsViewlet;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Given a particular bucket, this will provide a browser-type interface for navigating the contents of the bucket.
 *
 * Created by will on 3/20/2016.
 */
public class S3BucketBrowserViewlet extends AwsViewlet implements ChangeListener<String>,EventHandler<MouseEvent> {
    /// The actual UI component
    private TableView<S3KeyEntry> bucketBrowserListView = new TableView<>();

    /// String property for the active bucket which we are querying
    private StringProperty activeBucket = new SimpleStringProperty("?");

    /// String property for the current 'prefix' (or path) inside of the bucket
    private StringProperty currentPrefix = new SimpleStringProperty("");

    /// The list of keys we are showing on the UI
    private final ObservableList<S3KeyEntry> keyList = FXCollections.observableArrayList();

    public S3BucketBrowserViewlet(AwsTabView awsTabView) {
        super(awsTabView);

        setCenter(bucketBrowserListView);
        bucketBrowserListView.setItems(keyList);
        bucketBrowserListView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupColumns();

        activeBucket.addListener(this);
        bucketBrowserListView.setOnMouseClicked(this);

        currentPrefix.addListener(this);
    }

    /**
     * Called when the bucket has changed or when the prefix (or path) has changed.  The 'directories' uses the CommonPrefixes
     * attribute of the ListObjects request.  The keys are then filled in as files.
     */
    private synchronized void syncObjectList() {
        bucketBrowserListView.getItems().clear();
        AmazonS3 s3 = s3();

        String prefix = getCurrentPrefix();
        if(prefix == null || prefix.length() <= 1)
            prefix = "";
        else if(prefix.startsWith("/"))
            prefix = prefix.substring(1);

        ListObjectsRequest listObjectsRequest= null;

        try {
            listObjectsRequest = new ListObjectsRequest(getActiveBucket(), URLDecoder.decode(prefix, "UTF-8"), "", "/", 1000);
        }catch(UnsupportedEncodingException e) {
            ErrorDialog.show("Failed to decode prefix", e);
            return;
        }

        //
        // get the new directories
        ObjectListing objectListing = s3.listObjects(listObjectsRequest);
        if(prefix.length() > 0 && !prefix.equals("/")) {
            keyList.add(S3KeyEntry.makeDirectory(".."));
        }

        //
        // Directories
        List<String> commonPrefixes = objectListing.getCommonPrefixes();
        for(String commonPrefix : commonPrefixes) {
            S3KeyEntry entry = S3KeyEntry.makeDirectory(commonPrefix);
            if(entry != null) {
                entry.setCurrentPrefix(getCurrentPrefix());
                keyList.add(entry);
            }
        }

        //
        // The files
        do {
            for(S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
                S3KeyEntry entry = S3KeyEntry.makeFile(s3ObjectSummary);
                if(entry != null) {

                    entry.setCurrentPrefix(objectListing.getPrefix());
                    keyList.add(entry);
                }

            }
            objectListing = s3.listNextBatchOfObjects(objectListing);
        }while(objectListing.isTruncated());

        currentPrefixProperty().set(objectListing.getPrefix());
    }

    /**
     * Gets the value of the currently active bucket from the activeBucket property
     * @return
     */
    public String getActiveBucket() {
        return activeBucket.get();
    }

    /**
     * Public accessor for the activeBucket property
     * @return
     */
    public StringProperty activeBucketProperty() {
        return activeBucket;
    }

    /**
     * Gets the value of the currentPrefix property showing where we are in the bucket.
     * @return
     */
    public String getCurrentPrefix() {
        return currentPrefix.get();
    }

    /**
     * Public accessor for the currentPrefix property
     * @return
     */
    public StringProperty currentPrefixProperty() {
        return currentPrefix;
    }

    /**
     * Configures the column used in the TableView
     */
    private void setupColumns() {
        bucketBrowserListView.getColumns().addAll(
                new NameColumn(),
                new FileSizeColumn(),
                new LastModifiedColumn()
        );
    }


    /**
     * Called when something has changed with the UI, so we can re-synchronize the TableView with the new information.
     * This could be if the bucket was changed, or if a directory was double clicked... anything that will change
     * what we should be looking at.
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        System.out.println(String.format("Updating.  Old: '%s'  New: '%s'", oldValue, newValue));
        if(newValue != null)
            syncObjectList();
    }

    /**
     * Handles the mouse interaction with the UI.  It will look for double clicks to change the path and re-sync the
     * UI or start a download.
     *
     * @param event
     */
    @Override
    public void handle(MouseEvent event) {
        //System.out.println(event.toString());
        if(event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            String prefix = getCurrentPrefix();
            if(prefix != null && isDirectorySelected()) {
                prefix = prefix + selectedItem();
                if(prefix.endsWith("/..")) {
                    prefix = prefix.substring(0, prefix.length()-3); // removes the /..
                    prefix = prefix.substring(0, prefix.lastIndexOf('/')+1); // removes the dir name
                }
            } else if(isFileSelected()) {
                S3KeyEntry entry = bucketBrowserListView.getSelectionModel().getSelectedItem();
                if (getTab() instanceof S3Downloader) {
                    S3Downloader downloader = (S3Downloader) getTab();
                    downloader.downloadItem(entry);
                }
                return;
            } else
                prefix = selectedItem();
            currentPrefixProperty().set(prefix);
            event.consume();
        }
    }

    /**
     * Determines if the currently selected object in the TableView is a directory.  Null is returned if nothing
     * is selected.
     * @return
     */
    private boolean isDirectorySelected() {
        S3KeyEntry sel = bucketBrowserListView.getSelectionModel().getSelectedItem();
        return sel != null && sel.isDirectory();
    }

    /**
     * Determines if the currently selected object in the TableView is a file.  Null is returned if nothing is selected.
     * @return
     */
    private boolean isFileSelected() {
        S3KeyEntry sel = bucketBrowserListView.getSelectionModel().getSelectedItem();
        return sel != null && sel.isFile();
    }

    /**
     * Convenience method for getting the name of the currently selected item.
     * @return
     */
    private String selectedItem() {
        S3KeyEntry selected = bucketBrowserListView.getSelectionModel().getSelectedItem();
        return selected.getDisplayName(true);
    }

    /**
     * The name column, shows the name of the file or the prefix
     */
    class NameColumn extends TableColumn<S3KeyEntry, S3KeyEntry> {
        public NameColumn() {
            setText("Name");
            setCellValueFactory(p -> new ReadOnlyObjectWrapper<S3KeyEntry>(p.getValue()));
            setCellFactory(p -> S3KeyTableCell.makeName());
        }
    }

    /**
     * The last modified column.  Shows the lastModified for files, or a blank/empty cell if its a directory.
     */
    class LastModifiedColumn extends TableColumn<S3KeyEntry,S3KeyEntry> {
        public LastModifiedColumn() {
            setText("Last Modified");
            setCellValueFactory(p -> new ReadOnlyObjectWrapper<S3KeyEntry>(p.getValue()));
            setCellFactory(p -> S3KeyTableCell.makeLastModified());
        }
    }

    /**
     * The file size column.  Will show either a human-friendly version of the file size or a blank/empty cell.
     */
    class FileSizeColumn extends TableColumn<S3KeyEntry,S3KeyEntry> {
        public FileSizeColumn() {
            setText("Size");
            setCellValueFactory(p -> new ReadOnlyObjectWrapper<S3KeyEntry>(p.getValue()));
            setCellFactory(p -> S3KeyTableCell.makeSize());
        }
    }
}
