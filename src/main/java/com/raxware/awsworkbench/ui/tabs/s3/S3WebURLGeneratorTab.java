package com.raxware.awsworkbench.ui.tabs.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.raxware.awsworkbench.ui.AwsTabView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

/**
 * Generates a URL that is accessible for keys in the bucket.  Right now, it only works on files.
 * <p>
 * TODO: Add support for directories in Web URL Generator
 * Created by will on 4/5/2016.
 */
public class S3WebURLGeneratorTab extends AwsTabView {

    //
    // holds the structure
    private GridPane layout = null;

    //
    // the overall container, used to auto-size the grid pane
    private BorderPane container = new BorderPane();

    //
    // do we use the bucket as the host name?  if not it will use the <bucketname>.s3.amazonaws.com format
    private CheckBox useBucketAsHostname;

    //
    // do we create a signed URL or just a regular URL?
    private CheckBox createSignedUrl;

    //
    // if we want a signed URL, do we include an expiration of how long it will be valid for?
    private TextField expiration;

    //
    // The space to show our generated URL
    private TextArea generatedURLArea;

    private StringProperty bucketProperty = new SimpleStringProperty("?");
    private StringProperty keyProperty = new SimpleStringProperty("?");


    public S3WebURLGeneratorTab() {
    }

    public void refresh() {
        sync();
    }

    @Override
    public void init() {
        super.init();

        layout = new GridPane();

        int row = 0;

        //
        // Section 1 - Details
        Label section1Label = new Label("Details");
        section1Label.getStyleClass().add("sectionHeader");
        layout.add(section1Label, 0, row, 2, 1);

        row++;
        //
        // shows the active bucket
        Label bucketLabel = new Label("Bucket");
        TextField bucketValue = new TextField();
        bucketValue.textProperty().bindBidirectional(bucketProperty);
        bucketValue.setEditable(false);
        layout.add(bucketLabel, 0, row);
        layout.add(bucketValue, 1, row);

        row++;
        //
        // shows the active key
        Label keyLabel = new Label("Key");
        TextField keyValue = new TextField();
        keyValue.textProperty().bindBidirectional(keyProperty);
        keyValue.setEditable(false);
        layout.add(keyLabel, 0, row);
        layout.add(keyValue, 1, row);

        row++;
        //
        // Section 2 - Signed URL
        Label section2Label = new Label("Signed URL");
        section2Label.getStyleClass().add("sectionHeader");
        layout.add(section2Label, 0, row, 2, 1);

        row++;
        //
        // Expiration signed URL?
        Label expireLabel = new Label("Expiration? (hours)");
        expiration = new TextField("-1");
        layout.add(expireLabel, 0, row);
        layout.add(expiration, 1, row);
        expiration.textProperty().addListener((observable, oldValue, newValue) -> sync());

        row++;
        //
        // Section 3 - Signed URL
        Label section3Label = new Label("Generated URL");
        section3Label.getStyleClass().add("sectionHeader");
        layout.add(section3Label, 0, row, 2, 1);

        row++;
        //
        // do we even look at the settings here?
        createSignedUrl = new CheckBox();
        createSignedUrl.setText("Create signed URL?");
        createSignedUrl.selectedProperty().addListener((observable, oldValue, newValue) -> sync());
        layout.add(createSignedUrl, 0, row, 2, 1);

        row++;
        //
        // use the aws host name or the bucket as the hostname
        useBucketAsHostname = new CheckBox();
        useBucketAsHostname.setText("Use bucket name as hostname?");
        useBucketAsHostname.selectedProperty().addListener((observable, oldValue, newValue) -> sync());
        layout.add(useBucketAsHostname, 0, row, 2, 1);

        row++;
        //
        // The geneated URL
        generatedURLArea = new TextArea("Not yet generated");
        generatedURLArea.setEditable(false);
        generatedURLArea.setWrapText(true);
        layout.add(generatedURLArea, 0, row, 2, 2);

        //
        // general layout stuff
        layout.setHgap(10.0);
        layout.setVgap(5.0);
        layout.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        ColumnConstraints col1Hgrow = new ColumnConstraints(100, 250, Double.MAX_VALUE);
        col1Hgrow.setHgrow(Priority.ALWAYS);
        layout.getColumnConstraints().addAll(col1, col1Hgrow);

        container.setPrefWidth(500);
        container.setCenter(layout);

    }

    /**
     * The overall container for our form
     *
     * @return
     */
    @Override
    protected Node getTabContent() {

        return container;
    }

    /**
     * Called when something on the form changes and we need to re-generate the URL
     */
    private void sync() {
        updateUrl();
    }

    /**
     * Called to update the form, and either calls updateSignedUrl() or updateRegularUrl() depending on
     * the configuration.
     */
    private void updateUrl() {
        if (createSignedUrl.isSelected()) {
            updateSignedUrl();
        } else {
            updateRegularUrl();
        }
    }

    /**
     * Generates a standard URL with the requested protocol (http/https) and the &lt;bucketname&gt;.s3.amazonaws.com
     * format.
     */
    private void updateRegularUrl() {
        try {
            URL url = new URL(
                    getRequestedProtocol(),
                    getBucketProperty() + ".s3.amazonaws.com",
                    "/" + getKeyProperty()
            );
            updateUrl(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showError(e);
        }
    }

    /**
     * If something goes wrong, we will show the user that something happened by replacing the contents of
     * the generated URL area.
     *
     * @param e
     */
    private void showError(Throwable e) {
        generatedURLArea.setText("ERROR: " + e.getMessage());
    }

    /**
     * Right now just assume HTTP, but will give an option later on for generating SSL.  Note this only applies
     * for normal URLs.  The signed URLs use HTTPS automatically.
     *
     * @return
     */
    private String getRequestedProtocol() {
        return "http";
    }

    /**
     * Generates a signed URL with an optional expiration in the URL.
     */
    private void updateSignedUrl() {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(getBucketProperty(), getKeyProperty());
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        try {
            String txt = expiration.getText();
            if (txt.length() > 0) {
                int hours = Integer.parseInt(expiration.getText());
                if (hours > 0) {

                    Instant instant = Instant.now().plusMillis(hours * 60 * 60 * 1000L);
                    System.out.println(instant.toString());

                    long effTs = (hours * 60 * 60 * 1000L) + (new Date()).getTime();
                    Date expireTimestamp = new Date(effTs);
                    System.out.println("Expire: " + expireTimestamp.toString());
                    generatePresignedUrlRequest.setExpiration(expireTimestamp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        // generating the url
        URL url = getShell().getAwsProxy().s3().generatePresignedUrl(generatePresignedUrlRequest);

        updateUrl(url);
    }

    /**
     * Will check for settings that apply to both types of URL and make any final modifications here, then
     * show what we came up with
     *
     * @param url The URL generated either from a presigned URL or a normal URL
     */
    private void updateUrl(URL url) {
        //
        // create a new URL with the bucket as the hostname if the checkbox is selected
        if (useBucketAsHostname.isSelected()) {
            try {
                url = new URL(url.getProtocol(), getBucketProperty(), url.getPort(), url.getFile());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        generatedURLArea.setText(url.toString());
    }

    public String getKeyProperty() {
        return keyProperty.get();
    }

    public StringProperty keyPropertyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(String keyProperty) {
        this.keyProperty.set(keyProperty);
    }

    public String getBucketProperty() {
        return bucketProperty.get();
    }

    public StringProperty bucketPropertyProperty() {
        return bucketProperty;
    }

    public void setBucketProperty(String bucketProperty) {
        this.bucketProperty.set(bucketProperty);
    }
}
