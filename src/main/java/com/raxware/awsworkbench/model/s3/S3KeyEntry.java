package com.raxware.awsworkbench.model.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.raxware.awsworkbench.utils.FileUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * A bean type object used for an entry into the object explorer, that describes the S3 entry.  It could be a directory
 * or file.
 *
 * Created by will on 3/20/2016.
 */
public class S3KeyEntry {
    /// Flags for indicating this is a directory
    public static final int TYPE_DIRECTORY = 1;
    /// Flag fr indicating this is a file
    public static final int TYPE_FILE = 2;

    /// What is the actual name of the object, separate from the path
    private String displayName = "?";
    /// What is the full path of the object
    private String path = "";
    /// What is the storage class of the file
    private String storageClass = "UNKNOWN";
    /// When was it last modified
    private long lastModified = 0;
    /// What is the raw size of the file (in bytes)
    private long size = 0;

    /// Directory or file?  Value stored here
    private int type = 0;
    /// The prefix part of the path
    private String currentPrefix;

    private S3KeyEntry() { }

    /**
     * Creates a directory object based on the name (which should come from the CommonPrefixes)
     *
     * @param name
     * @return
     */
    public static S3KeyEntry makeDirectory(String name) {
        S3KeyEntry entry = new S3KeyEntry();
        entry.displayName = name;
        entry.type = TYPE_DIRECTORY;
        entry.storageClass = " ";
        return entry;
    }

    /**
     * Creates a file based on the S3ObjectSummary returned from the ListObjects command
     *
     * @param summary
     * @return
     */
    public static S3KeyEntry makeFile(S3ObjectSummary summary) {
        if(summary == null || summary.getKey().substring(summary.getKey().lastIndexOf('/')+1).equals(""))
            return null;


        S3KeyEntry entry = new S3KeyEntry();
        entry.path = summary.getKey();
        entry.displayName = entry.path.substring(entry.path.lastIndexOf('/')+1);
        entry.type = TYPE_FILE;
        entry.lastModified = summary.getLastModified().getTime();
        entry.size = summary.getSize();
        
        return entry;
    }


    /**
     * Returns the display name of the object in question.  Either this will be a simple name, or the full
     * path.
     *
     * @param relativize true to remove the currentPrefix if it exists then return the name, false to just return the name
     * @return
     */
    public String getDisplayName(boolean relativize) {
        if(!relativize || currentPrefix == null || currentPrefix.length() == 0) {
            //System.out.println(displayName + " ["+currentPrefix+"]");
            return displayName;
        }

        if(displayName.startsWith(currentPrefix))
            return displayName.substring(currentPrefix.length());
        else
            return displayName;
    }

    /**
     * Returns a LocalDateTime of the lastModified paramter, with the default TimeZone.
     *
     * @return
     */
    public LocalDateTime getLastModified() {
        return LocalDateTime.ofEpochSecond(lastModified / 1000, 0, ZoneOffset.ofTotalSeconds(TimeZone.getDefault().getRawOffset() / 1000));
    }

    /**
     * Returns the raw value of the last modified parameter.
     * @return
     */
    public long getLastModifiedRaw() {
        return lastModified;

    }

    /**
     * If this is a file, we will return the storageClass property.  If not, then we return an empty string.
     *
     * @return
     */
    public String getStorageClass() {
        return type == TYPE_FILE ? storageClass : "";
    }

    /**
     * Returns the raw size of the file (directories should return 0)
     * @return
     */
    public long getSize() { return size; }

    /**
     * Converts the raw size into a human readable file size.
     * @return
     */
    public String getSizeDisplay() {

        return type == TYPE_FILE ? FileUtils.bytesToSize(size) : "";
    }

    /**
     * Sets the current prefix of the object.
     *
     * @param currentPrefix
     */
    public void setCurrentPrefix(String currentPrefix) {
        this.currentPrefix = currentPrefix;
    }

    /**
     * Test for if this is a directory.
     *
     * @return
     */
    public boolean isDirectory() {
        return type == TYPE_DIRECTORY;
    }

    /**
     * Test for if this is a file
     *
     * @return
     */
    public boolean isFile() {
        return type == TYPE_FILE;
    }

    /**
     * Returns the full path of the object
     *
     * @return
     */
    public String getPath() {
        return path;
    }
}
