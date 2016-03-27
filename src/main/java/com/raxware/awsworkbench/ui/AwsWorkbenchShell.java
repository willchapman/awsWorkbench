package com.raxware.awsworkbench.ui;

import com.raxware.awsworkbench.AwsProxy;
import com.raxware.awsworkbench.ui.dialogs.Dialogs;
import com.raxware.awsworkbench.ui.dialogs.ErrorDialog;
import com.raxware.awsworkbench.ui.menu.MenuBarRegistry;
import com.raxware.awsworkbench.ui.tabs.s3.S3ExplorerTab;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Objects;

/**
 * The main application shell for this app.  It will hold the primary tab pane and provide support for other
 * parts of the UI, such as the MenuBar.
 *
 * Created by will on 3/19/2016.
 */
public class AwsWorkbenchShell extends BorderPane  {
    private final TabPane tabPane;
    private final AwsProxy awsProxy = new AwsProxy();
    private AwsWorkbenchFxApplication application;
    private static Log log = LogFactory.getLog(AwsWorkbenchShell.class);

    /**
     * Constructor
     */
    public AwsWorkbenchShell() {
        tabPane = new TabPane();
        tabPane.setPrefHeight(500);
        tabPane.setPrefWidth(1000);

        setTop(MenuBarRegistry.getInstance().getMenuBar());
        setCenter(tabPane);
    }

    /**
     * Lets us know the application is now starting up, time to perform any pre-work tasks.
     */
    public void applicationStartup() {
        log.info("Application is starting");

        populateTabs();
    }

    /**
     * Temporary method to populate our only working portion of the UI, the S3 browser.
     */
    private void populateTabs() {
        addTab(S3ExplorerTab.class, "S3");
    }

    /**
     * Provides a means of getting access to the AWS library.
     *
     * @return Instance of the AwsProxy
     */
    public AwsProxy getAwsProxy() {
        return awsProxy;
    }

    /**
     * Adds a tab, but does not auto select it.  Convenience for <code>addTab(cls, tabText, false)</code>.
     * @param cls The class to instantiate
     * @param tabText What text to put into the tab
     * @return The instance of the tab that was added to the UI
     */
    public AwsTabView addTab(Class<? extends AwsTabView> cls, String tabText) {
        return addTab(cls, tabText, false );
    }

    /**
     * Removes a tab from the main pane.  THe tab must match both for the class and tab text.  The class type is
     * required, the tab text is optional.  If the tab text is null, then it is not considered.
     *
     * Note that only a single tab will ever be removed from calling this method.
     *
     * @param cls The class type for the tab we are looking for
     * @param tabText The tab text to use when searching (optional).  null to not use.
     * @return true if a tab is removed, false if it is not.
     */
    public boolean removeTab(Class<? extends AwsTabView> cls, String tabText) {
        boolean ret = false;
        AwsTabView tabView = (AwsTabView) tabPane.getTabs()
                                            .stream()
                                            .filter(tab -> tab.getClass().isAssignableFrom(cls))
                                            .findFirst().orElse(null);
        if(tabView != null) {
            ret = tabPane.getTabs().remove(tabView);
        }
        return ret;
    }

    /**
     * Creates a new instance, sets up the tab and adds it to the UI.  Then returns a reference to this tab.
     *
     * If anything goes wrong, we will throw a TabException
     *
     * @param cls The class type to instantiate
     * @param tabText What text to put into the tab
     * @param autoSelect trrue to automatically select the tab after being added, false to just add and not select.
     * @return A refernece to the tab added to the UI
     */
    public AwsTabView addTab(Class<? extends AwsTabView> cls, String tabText, boolean autoSelect) {
        Objects.requireNonNull(cls, "Cannot add null tab");
        AwsTabView tab = null;
        try {
            tab = cls.newInstance();
            tab.setText(tabText);
            tab.awsWorkbenchShell = this;
            tab.init();
            tab.start();
            tabPane.getTabs().add(tab);
            tab.added();

            MenuItem menuItem = getMenuItem("Window/"+tabText);
            menuItem.setOnAction(event -> System.out.println(event.toString()));

            if(autoSelect)
                tabPane.getSelectionModel().select(tab);

            tab.setOnCloseRequest(event -> {
                tabClosingEvent(event);
            });

            log.trace(String.format("Tab Added -> %s :: %s", tabText, cls.toString()));
            return tab;
        } catch (Exception e) {
            if(tab != null) {
                tabPane.getTabs().remove(tab);
            }
            checkForEmptyPane();
            ErrorDialog.show("Error adding "+tabText + " tab: "+e.getMessage(), e);

            return null;
        }
    }

    /**
     * If we have nothing to show, present something to the user instead of an empty window.
     */
    private void checkForEmptyPane() {
        if(tabPane.getTabs().size() == 0) {
            // This will be improved upon later
            Label errLabel = new Label("No tabs have been added");
            errLabel.setFont(Font.font("Arial", 32.0));
            errLabel.setTextFill(Paint.valueOf("#BBBBBB"));
            errLabel.setMaxHeight(Double.MAX_VALUE);
            errLabel.setMinHeight(Double.MIN_VALUE);
            setCenter(errLabel);
        } else {
            setCenter(tabPane);
        }
    }

    /**
     * Called when we get a request to close the tab, so we can see how many tabs are left.  If there is only 1 tab
     * left when the request comes in, we confirm they want to leave the application.
     *
     * This also calls the closing() method on the Tab itself.  See {@see AwsTabView}.
     * @param evt The event passed in from JavaFX
     */
    private void tabClosingEvent(Event evt) {
        AwsTabView tab = (AwsTabView) evt.getSource();
        int size = tabPane.getTabs().size();
        if(size == 1 && Dialogs.confirm("This will exit the application.  Are you sure?") == ButtonType.NO) {
            log.trace("CLose request came in, user declined closing the application");
            evt.consume();
        } else if(size == 1) {
            log.info("Closing last tab, exiting application");
            tab.closing();
            Platform.exit();
        } else {
            log.trace(String.format("Closing %s %s", tab.getText(), tab.getClass().toString()));
            tab.closing();
        }

    }

    /**
     * Attempts to find a tab based on the class type provided.  We will either return a reference to the first tab
     * found or null.
     *
     * @param cls The class type to look for
     * @return The tab refernece or null if not found.
     */
    public AwsTabView getTab(Class<? extends AwsTabView> cls) {
        if(cls == null) return null;

        Tab tab = tabPane.getTabs().parallelStream().filter(t -> t.getClass().isAssignableFrom(cls)).findFirst().orElse(null);
        return tab == null ? null : (AwsTabView) tab;
    }

    /**
     * Returns the menu item from the MenuBar or creates the path if it does not already exist.  This parses the
     * string as a path, so for example "File/Open" will look for the "File" in the MenuBar itself, then a child element
     * of "Open".  If this path does not exist, it will automatically be created.
     *
     * @param path The path, with the root being the label in the menu bar, and each element in the path being a sub-menu
     * @return
     */
    public MenuItem getMenuItem(String path) {
        return MenuBarRegistry.getInstance().getMenuItem(path, true);
    }

    /**
     * Sets a reference to the application running this shell for future refernece.
     *
     * @param application The Fx application
     */
    public void setApplication(AwsWorkbenchFxApplication application) {
        this.application = application;
    }

    /**
     * Accessor method for the Fx application reference
     *
     * @return The Fx application
     */
    public AwsWorkbenchFxApplication getApplication() {
        return application;
    }

}
