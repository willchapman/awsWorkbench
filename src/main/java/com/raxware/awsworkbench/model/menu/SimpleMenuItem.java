package com.raxware.awsworkbench.model.menu;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * A simplified MenuItem object.
 *
 * Created by will on 3/24/2016.
 */
public abstract class SimpleMenuItem extends MenuItem {

    public static final SeparatorMenuItem SEPERATOR_MENU_ITEM = new SeparatorMenuItem();

    public SimpleMenuItem() {
        init();
    }

    public SimpleMenuItem(String text) {
        super(text);
        init();
    }

    public SimpleMenuItem(String text, Node graphic) {
        super(text, graphic);
        init();
    }

    private void init() {
        setOnAction(event -> invoke(event));
    }

    protected abstract void invoke(ActionEvent actionEvent);
}
