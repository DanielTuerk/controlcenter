package net.wbz.moba.controlcenter.db;

import com.db4o.ObjectContainer;

import java.io.File;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class Database {

    private final String key;
    private final File dbFile;
    private ObjectContainer objectContainer;

    public Database(String key, File dbFile) {
        this.key = key;
        this.dbFile = dbFile;
    }

    public String getKey() {
        return key;
    }

    public File getDbFile() {
        return dbFile;
    }

    public ObjectContainer getObjectContainer() {
        return objectContainer;
    }

    public void setObjectContainer(ObjectContainer objectContainer) {
        this.objectContainer = objectContainer;
    }
}
