package net.wbz.moba.controlcenter.db;

import com.db4o.ObjectContainer;
import com.db4o.config.ObjectConstructor;
import org.joda.time.DateTime;

/**
 * Translator for DB40 to store and load objects with <code>joda.DateTime</code> members.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class DateTimeTranslator implements ObjectConstructor {

    @Override
    public Object onStore(ObjectContainer container, Object applicationObject) {
        return ((DateTime) applicationObject).getMillis();
    }

    @Override
    public void onActivate(ObjectContainer container, Object applicationObject, Object storedObject) {
    }

    @Override
    public Class storedClass() {
        return Long.class;
    }

    @Override
    public Object onInstantiate(ObjectContainer container, Object storedObject) {
        return new DateTime(((Long)storedObject).longValue());
    }

}
