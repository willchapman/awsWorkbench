package com.raxware.awsworkbench.model.menu;

import javafx.application.Platform;
import javafx.event.ActionEvent;

/**
 * Created by will on 4/7/2016.
 */
public class ExitMenuItem extends SimpleMenuItem {

    public ExitMenuItem() {
        super("Exit");
    }

    @Override
    protected void invoke(ActionEvent actionEvent) {
        Platform.exit();
    }
}
