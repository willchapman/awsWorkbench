/*
 * Copyright 2016 Will Chapman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.raxware.awsworkbench.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

/**
 * Common parent for all tab views.  Each tab view should contain a series
 * of AwsViewlets laid out however they want.
 *
 * Created by Will Chapman on 1/10/2016.
 */
public abstract class AwsTabView extends Tab {

    protected AwsWorkbenchShell awsWorkbenchShell;

    public AwsWorkbenchShell getShell() {
        return awsWorkbenchShell;
    }

    /**
     * Called before start()
     */
    public void init() {

    }

    /**
     * Called after the tab has been added to the tabpane
     */
    public void added() {

    }

    /**
     * Called on start up, sets the content of the tab to getTabContent()
     */
    public void start() {
        Node content = getTabContent();
        setContent(content);
    }

    /**
     * Closed right before the tab is removed from service.
     */
    public void closing() {

    }

    /**
     * Return the Node that represents the content for this tab
     *
     * @return The root node for the UI
     */
    protected abstract Node getTabContent();
}
