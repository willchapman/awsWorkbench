package com.raxware.awsworkbench.model.menu;

import com.raxware.awsworkbench.ui.AwsTabView;
import com.raxware.awsworkbench.ui.dialogs.DialogSettings;
import com.raxware.awsworkbench.ui.dialogs.Dialogs;
import javafx.event.ActionEvent;
import javafx.scene.Node;

/**
 * Created by will on 4/7/2016.
 */
public class SimpleTabMenuItem extends SimpleMenuItem {

    private final AwsTabView awsTabView;
    private DialogSettings dialogSettings = null;

    public SimpleTabMenuItem(String label, Node iconGraphic, AwsTabView awsTabView) {
        super(label, iconGraphic);
        this.awsTabView = awsTabView;
    }

    public void setDialogSettings(DialogSettings dialogSettings) {
        this.dialogSettings = dialogSettings;
    }

    @Override
    protected void invoke(ActionEvent actionEvent) {
        Dialogs.tabDialog(awsTabView, dialogSettings);
    }
}
