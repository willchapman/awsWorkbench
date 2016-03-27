package com.raxware.awsworkbench.ui.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.util.Arrays;

/**
 * Provides a central repository for the menubar.  This will allow individual tabs or even viewlets to add their own entries
 * into the menubar.  This is configured as a Singleton for global access.
 *
 * To make it easier, it will take a string path (using the '/' character) to construct each menuitem as needed.  The
 * first item will be the entry in the menubar.
 *
 *
 * Created by will on 3/20/2016.
 */
public class MenuBarRegistry {

    /// The main menubar.
    private MenuBar menuBar = new MenuBar();

    /// What we are using for a seperator
    private static final String separator = "/";

    /// The one and only instance
    private static MenuBarRegistry ourInstance = new MenuBarRegistry();

    /// Accessor method for the single instance
    public static MenuBarRegistry getInstance() {
        return ourInstance;
    }

    private MenuBarRegistry() {
    }

    /**
     * Returns the main menubar for the app
     * @return
     */
    public MenuBar getMenuBar() { return menuBar; }

    /**
     * Attempts to find the MenuItem if it already exists, and optionally adds it if it does not.
     *
     * @param path The path of the menu items, starting from the main menubar.
     * @param autoAdd true will add the menu path if it doesn't exist.
     * @return The added (or found) MenuItem reference from the menu.
     */
    public MenuItem getMenuItem(String path, boolean autoAdd) {
        if(path == null || path.length() == 0) {
            return null;
        }

        MenuItem menuItem = null;
        if( path.indexOf(separator) > 0 ) {
            menuItem = getMenuItem(path.split(separator), autoAdd);
        } else {
            menuItem = getMenuItem(new String[] {path}, autoAdd);
        }

        return menuItem;

    }

    /**
     * Takes the path, which was split in the public method and performs the real work here.
     *
     * @param strings The MenuItems
     * @param autoAdd true will auto add the menu item if it doesn't exist
     * @return The added (or found) MenuItem reference from the menu
     */
    private MenuItem getMenuItem(String[] strings, boolean autoAdd) {
        MenuItem menu = null;
        System.out.println("getMenuItem("+ Arrays.toString(strings)+")");
        for(int i = 0; i < strings.length; i++) {
            MenuItem parent = menu;
            menu = get(parent, strings[i]);
            if(menu == null && autoAdd) {
                if(strings.length-1 == i) {
                    System.out.println("Adding MeuItem: "+strings[i]);
                    menu = new MenuItem(strings[i]);
                } else {
                    System.out.println("Adding Menu: "+strings[i]);
                    menu = new Menu(strings[i]);
                }
                add(parent, menu);
            } else if(menu == null) {
                throw new IllegalStateException("Path does not exist - failed at "+strings[i]);
            }
        }

        return menu;
    }

    /**
     * Convienence method to add items to the menu
     *
     * @param parent
     * @param child
     */
    private void add(MenuItem parent, MenuItem child) {
        if(parent == null && child instanceof Menu) {
            menuBar.getMenus().add((Menu) child);
            return;
        }

        if(parent instanceof Menu) {
            ((Menu)parent).getItems().add(child);
            return;
        }

        if(parent == null) {
            String type = child == null ? "null" : child.getClass().getTypeName();
            throw new IllegalStateException("Only menus can be added to the menubar, not "+type);
        }

        throw new IllegalStateException("The parent has to be a menu, not a "+parent.getClass().getName());
    }

    /**
     * Look for a particular menu item inside of the supplied menu
     *
     * @param parent
     * @param label
     * @return
     */
    private MenuItem get(MenuItem parent, String label) {
        MenuItem item = null;

        if(parent == null) {
            item = menuBar.getMenus().parallelStream().filter(menu -> menu.getText().equals(label)).findFirst().orElse(null);
        } else if(parent instanceof Menu) {
            Menu parentMenu = (Menu) parent;
            item = parentMenu.getItems().parallelStream().filter(menu -> menu.getText().equals(label)).findFirst().orElse(null);
        }
        return item;
    }
}
