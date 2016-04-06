package com.raxware.awsworkbench.ui.menu;

import javafx.scene.control.MenuItem;

/**
 * Implemented when the object has something it may want to add to the context menu
 * <p>
 * Created by will on 4/5/2016.
 */
public interface ContextMenuNode {
    MenuItem[] getItems();
}
