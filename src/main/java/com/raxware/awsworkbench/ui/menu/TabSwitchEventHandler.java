package com.raxware.awsworkbench.ui.menu;

import com.raxware.awsworkbench.ui.AwsTabView;
import com.raxware.awsworkbench.ui.AwsWorkbenchShell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Event handler for switching tabs when selected in the main menu bar.
 *
 * Created by Will on 3/27/2016.
 */
public class TabSwitchEventHandler implements EventHandler<ActionEvent> {

    private final AwsWorkbenchShell awsWorkbenchShell;
    private final AwsTabView awsTabView;

    public TabSwitchEventHandler(AwsWorkbenchShell awsWorkbenchShell, AwsTabView awsTabView) {
        this.awsWorkbenchShell = awsWorkbenchShell;
        this.awsTabView = awsTabView;
    }

    @Override
    public void handle(ActionEvent event) {
        awsWorkbenchShell.setActiveTab(awsTabView);
        event.consume();
    }
}
