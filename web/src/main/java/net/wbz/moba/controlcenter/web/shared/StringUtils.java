package net.wbz.moba.controlcenter.web.shared;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class StringUtils {

    public static String join(Collection<String> strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = strings.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (iter.hasNext()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }
}