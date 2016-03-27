package com.raxware.awsworkbench.res.icons;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to get the resources in the jar (icons for now)
 *
 * Created by will on 3/25/2016.
 */
public final class Resources {
    /// Provides an internal cache for commonly requested items are
    private static Map<String,Image> cache = new HashMap<>();

    /**
     * Returns an icon that has a PNG extension.
     *
     * @param name The name  the file
     * @param size The dimensions (which should be part of the name)
     * @return The image resource if found, null otherwise
     */
    public static Image getPngIcon(String name, String size) {
        return getIcon(name, size, "png");
    }

    /**
     * Returns the requested icon using the following convention
     *
     * <p>file_24.png</p>
     * <ul>
     *     <li>name = file</li>
     *     <li>size = 24</li>
     *     <li>extension = png</li>
     * </ul>
     *
     * @param name The name of the file (see above)
     * @param size The size of the file (see above)
     * @param extension The file extension (see above)
     * @return The image resource, or null
     */
    public static Image getIcon(String name, String size, String extension) {
        String key = String.format("%s_%s.%s", name, size, extension);
        Image img = null;
        if(!cache.containsKey(key)) {
            img = new Image(String.format("icons/%s", key));
            cache.put(key, img);
        } else {
            img = cache.get(key);
        }

        return img;
    }
}
