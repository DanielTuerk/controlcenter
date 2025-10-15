package net.wbz.moba.controlcenter.shared;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Daniel Tuerk
 */
@Deprecated
public class StringUtils {

    public static String join(Collection<?> elements, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = elements.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (iter.hasNext()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }
}