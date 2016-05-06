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

package com.raxware.awsworkbench;

import com.raxware.awsworkbench.ui.AwsWorkbenchFxApplication;
import javafx.application.Application;


/**
 * The main entry point to the application.
 * <p>
 * Created by Will Chapman on 1/10/2016.
 */
public class Startup {
    public static void main(String[] args) {
        //PropertyConfigurator.configure("log4j.properties");
        Application.launch(AwsWorkbenchFxApplication.class, args);
    }
}
