package com.raxware.awsworkbench.ui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Convience methods for getting feedback from the user
 *
 * Created by will on 3/25/2016.
 */
public class Dialogs {
    /**
     * Shows a confirm dialog (Yes/No) option.  This method will block and return the the button selected (or null).
     *
     * @param msg The message to present to the user
     * @return What the user selected, ButtonType.YES or ButtonType.NO, or null.
     */
    public static ButtonType confirm(String msg) {
        Alert dialog = new Alert(Alert.AlertType.WARNING, msg, ButtonType.YES, ButtonType.NO);
        ButtonType buttonType = dialog.showAndWait().orElse(null);
        return buttonType;
    }
}
