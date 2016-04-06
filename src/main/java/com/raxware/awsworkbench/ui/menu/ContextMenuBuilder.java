package com.raxware.awsworkbench.ui.menu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by will on 4/5/2016.
 */
public class ContextMenuBuilder {
    private List<ContextMenuNode> builders = new LinkedList<>();

    public ContextMenuBuilder() {

    }

    public ContextMenuBuilder(ContextMenuNode node) {
        addBuilder(node);
    }

    public boolean addBuilder(ContextMenuNode node) {
        if (node != null)
            return builders.add(node);
        else
            return false;
    }

    public ContextMenu buildMenu(boolean seperate) {
        ContextMenu menu = new ContextMenu();
        Iterator<ContextMenuNode> nodeIterator = builders.iterator();
        while (nodeIterator.hasNext()) {
            MenuItem[] menuItems = nodeIterator.next().getItems();
            if (menuItems != null && menuItems.length > 0)
                menu.getItems().addAll(menuItems);

            if (seperate)
                menu.getItems().add(new SeparatorMenuItem());
        }

        return menu;
    }
}
