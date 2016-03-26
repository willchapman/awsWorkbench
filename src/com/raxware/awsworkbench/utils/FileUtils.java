package com.raxware.awsworkbench.utils;

/**
 * Utilities for working with files
 *
 * Created by will on 3/20/2016.
 */
public class FileUtils {

    private static final String[] SIZE_SUFFIX = {"B", "KB", "MB", "GB", "TB"};

    /**
     * Converts the raw bytes to something human-readable with a precision of 2
     *
     * @param bytes The raw bytes
     * @return
     */
    public static final String bytesToSize(long bytes) {
        return bytesToSize(bytes, 2);
    }

    /**
     * Converts the raw bytes into something human readable.  The precision will show how many decimal places
     * to include in the final answer.
     *
     * TODO: The precision part does not work yet
     *
     * @param bytes The raw size in bytes
     * @param precision How many decimal places
     * @return A human-readable file size
     */
    public static final String bytesToSize(long bytes, int precision) {
        precision = precision <= 1 ? 2 : precision;
        int pos = 0;
        if(bytes == 0)
            return "0 B";

        while(bytes >= 1024) {
            pos++;
            bytes = bytes / 1024;
        }

        return bytes + " " + SIZE_SUFFIX[pos];
    }
}
