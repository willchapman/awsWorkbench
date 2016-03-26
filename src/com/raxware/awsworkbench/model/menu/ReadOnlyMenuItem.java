package com.raxware.awsworkbench.model.menu;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

/**
 * A MenuItem object that is set to read-only by default.
 *
 * Created by will on 3/24/2016.
 */
public class ReadOnlyMenuItem extends MenuItem {
    public ReadOnlyMenuItem() {
        init();
    }

    public ReadOnlyMenuItem(String text) {
        super(text);
        init();
    }

    public ReadOnlyMenuItem(String text, Node graphic) {
        super(text, graphic);
        init();
    }

    private void init() {
        setDisable(true);
        setOnAction(event -> invoke(event));
    }

    protected void invoke(ActionEvent actionEvent) {}
}
