package com.raxware.awsworkbench.ui.dialogs;

import com.raxware.awsworkbench.ui.AwsTabView;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.Collection;
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

    public static Object choiceDialog(Collection objs, String prompt, DialogSettings settings) {
        HBox box = new HBox();
        box.getChildren().add(new Label(prompt));

        ComboBox<String> list = new ComboBox<>();
        list.getItems().addAll(objs);
        box.getChildren().add(list);


        Dialog dialog = new Dialog();
        dialog.getDialogPane().getStylesheets().add("css/awsworkbench.css");

        dialog.getDialogPane().setContent(box);

        if (settings == null) {
            settings = new DialogSettings();
            settings.setHeaderText("Make your selection...");
            settings.addButton(ButtonType.OK);
            settings.addButton(ButtonType.CANCEL);
        }

        dialog.setHeaderText(settings.getHeaderText());
        dialog.getDialogPane().getButtonTypes().addAll(settings.getButtonTypes());

        dialog.setX(settings.getX());
        dialog.setY(settings.getY());
        dialog.setWidth(settings.getWidth());
        dialog.setHeight(settings.getHeight());

        Optional optional = dialog.showAndWait();
        ButtonType buttonChoice = (ButtonType) optional.orElse(null);
        if (buttonChoice == ButtonType.OK) {
            return list.getValue();
        } else {
            return null;
        }

    }

    public static void tabDialog(AwsTabView awsTabView, DialogSettings settings) {
        if (awsTabView == null) return;
        if (settings == null) {
            settings = new DialogSettings();
            settings.setHeaderText(awsTabView.getClass().getName());
            settings.calculate(awsTabView.getShell().getLayoutBounds(), 65);
        }

        Dialog dialog = new Dialog();
        dialog.getDialogPane().getStylesheets().add("css/awsworkbench.css");
        dialog.setHeaderText(settings.getHeaderText());

        dialog.getDialogPane().getButtonTypes().addAll(settings.getButtonTypes());
        dialog.getDialogPane().setContent(awsTabView.getTabGraphic());

        dialog.setX(settings.getX());
        dialog.setY(settings.getY());
        dialog.setWidth(settings.getWidth());
        dialog.setHeight(settings.getHeight());


        dialog.showAndWait();
    }


}
