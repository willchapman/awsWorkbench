package com.raxware.awsworkbench.exceptions;

/**
 * Thrown if we run into problems with adding a tab to the UI
 *
 * Created by will on 3/19/2016.
 */
public class TabException extends RuntimeException {

    public TabException(String s, Throwable e) {
        super(s,e);
    }
}
