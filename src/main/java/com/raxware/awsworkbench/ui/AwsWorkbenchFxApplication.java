/*
 * Copyright 2016 Will Chapman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.raxware.awsworkbench.ui;

import com.raxware.awsworkbench.res.Resources;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;


/**
 * The entry point for our FX application.
 *
 * Created by Will Chapman on 1/10/2016.
 */
public class AwsWorkbenchFxApplication extends Application {

    private File homeDirectory = null;
    private static Log log  = LogFactory.getLog(AwsWorkbenchFxApplication.class);

    /**
     * Constructor - establishes home directory
     */
    public AwsWorkbenchFxApplication() {
        homeDirectory = getHome();
    }


    /**
     * The starting point of the UI
     *
     * @param primaryStage Where to put our scene
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        AwsWorkbenchShell awsWorkbenchShell = new AwsWorkbenchShell();
        awsWorkbenchShell.setApplication(this);

        Scene primaryScene = new Scene(awsWorkbenchShell);
        primaryStage.setTitle("awsWorkbench");

        primaryScene.getStylesheets().add("css/awsworkbench.css");
        primaryStage.getIcons().add(Resources.getPngIcon("wrench", "128"));
        primaryStage.setScene(primaryScene);
        setWindowBounds(primaryStage);
        primaryStage.show();
        Platform.runLater(() -> awsWorkbenchShell.applicationStartup());

        loadSettings();
    }

    /**
     * Loads the settings based on the home directory.
     */
    private void loadSettings() {
        log.info("Home Directory: "+homeDirectory.getAbsolutePath());

        //
        // does the settings directory even exist?
        File settings = new File(homeDirectory, "etc");
        if(!settings.exists())
            settings.mkdir();

        //
        // load settings
        File settingsFile = new File(settings, "awsWorkbench.properties");
        if(settingsFile.exists() && settings.canRead()) {

        }
    }

    /**
     * Performs the logic to determine what our home directory is.  Looks at specific system properties in the following
     * order:
     * <ul>
     *     <li>awsworkbench.home</li>
     *     <li>user.home (awsWorkbench subdirectory)</li>
     * </ul>
     * If neither of these are acceptable, then we will return null.
     *
     * @return The effective home directory, or null
     */
    public File getHome() {
        if(System.getProperty("awsworkbench.home", null) != null) {
            log.trace("Setting homeDirectory via awsworkbench.home");
            return new File(System.getProperty("awsworkbench.home"));
        }

        if(System.getProperty("user.home", null) != null) {
            log.trace("Setting homeDirectory via user.home");
            return new File(System.getProperty("user.home"), "awsWorkbench");
        }

        log.warn("No homeDirectory found");
        return null;
    }

    /**
     * Convenience for getWindowBounds(stage, 65)
     *
     * @return
     */
    private void setWindowBounds(Stage stage) {
        setWindowBounds(stage, 65);
    }


    /**
     * Determine the default window position, but either restoring the last saved configuration (not yet implemented0
     * or by centering on the screen and taking up the requested percentage of the screen (1 to 100).
     * <p>
     * Defaults to 65
     *
     * @return
     */
    private void setWindowBounds(Stage stage, int percentage) {
        percentage = Utils.boundsCheck(1, 100, percentage);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double percentVal = percentage / 100.0;

        double width = screen.getWidth() * percentVal;
        double x = (screen.getWidth() - width) / 2.0;

        double height = screen.getHeight() * percentVal;
        double y = (screen.getHeight() - height) / 2.0;

        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);

        //
        // perhaps we should save these settings?
    }

}
