package com.raxware.awsworkbench.ui;

/**
 * Created by will on 4/5/2016.
 */
public class Utils {
    private Utils() {
    }

    /**
     * Checks the value is in the requested bounds (inclusive) and returns either
     * the value back, or the high if its above, or the low if the value is below.
     *
     * @param low  The smallest value possible
     * @param high The highest value possible
     * @param val  The value to check
     * @return Either the value supplied, or low or high
     */
    public static int boundsCheck(int low, int high, int val) {
        if (val >= low && val <= high)
            return val;
        else if (val < low)
            return low;
        else
            return high;
    }
}
