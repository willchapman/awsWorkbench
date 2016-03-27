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

package com.raxware.awsworkbench.ui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Provides a means of showing an error message, and optionally a stack trace if one is provided.  Depending on the
 * severity, we can kill the app from here (using System.exit())
 *
 * Created by Will Chapman on 1/12/2016.
 */
public class ErrorDialog {

    public static void show(String msg, Throwable t) {
        show(msg, t, false);
    }

    public static void show(String msg, Throwable t, boolean killApp) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(80);
        textArea.setText(stringWriter.toString());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(textArea);

        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("ERROR");
        dialog.setHeaderText(msg);
        //
        //TODO: Need to figure out how to re-position the dialog when its expanded.
        dialog.getDialogPane().setExpandableContent(scrollPane);
        dialog.showAndWait();

        if (killApp)
            System.exit(-1);
    }
}
