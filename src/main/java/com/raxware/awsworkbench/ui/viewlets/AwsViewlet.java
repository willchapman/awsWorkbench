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

package com.raxware.awsworkbench.ui.viewlets;

import com.amazonaws.services.s3.AmazonS3;
import com.raxware.awsworkbench.ui.AwsTabView;
import com.raxware.awsworkbench.ui.AwsWorkbenchShell;
import javafx.scene.layout.BorderPane;

/**
 * Common parent to all viewlets.  A viewlet is a small section of the UI that performs one specific function.
 *
 * Created by Will Chapman on 1/10/2016.
 */
public abstract class AwsViewlet extends BorderPane {

    private final AwsTabView tab;

    /**
     * Creates the viewlet, but we need to know where we belong so the Viewlet is required to tell us the Tab instance
     * they belong too.
     *
     * @param awsTabView
     */
    public AwsViewlet(AwsTabView awsTabView) {
        tab = awsTabView;
    }

    /**
     * Reference to the main workbench shell if needed
     * @return
     */
    protected final AwsWorkbenchShell getShell() {
        return tab.getShell();
    }

    /**
     * Returns a reference to the containing tab provided when the object was created.
     *
     * @return An object reference to the tab where this viewlet lives.
     */
    public final AwsTabView getTab() {
        return tab;
    }

    /**
     * Convienence method of getting an AmazonS3 client object by using the AwsProxy object
     * on the primary application.
     *
     * @return An AmazonS3 client object.
     */
    protected final AmazonS3 s3() {
        return getShell().getAwsProxy().s3();
    }
}
