package net.wbz.moba.controlcenter.db;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class DatabaseFactory {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFactory.class);

    private final List<Database> databases = new ArrayList<Database>();

    /**
     * Location of the root directory for the storages.
     * Is configured in spring context.
     */
    private final File storageLocationPath;

    private final static String DB_SUFFIX = ".db";

    public DatabaseFactory(File storageLocationPath) {
        this.storageLocationPath = storageLocationPath;
        startUp();
    }

    private void startUp() {
        log.info("load existing databases from " + storageLocationPath.toString());
        for (File databaseFile : storageLocationPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(DB_SUFFIX);
            }
        })) {
            Database db = new Database(databaseFile.getName().substring(0, databaseFile.getName().length() - DB_SUFFIX.length()), databaseFile);
            databases.add(db);
        }
    }

    /**
     * Create a new configuration for the storage.
     *
     * @return new embedded job storage configuration
     */
    private EmbeddedConfiguration getNewConfiguration() {
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().updateDepth(2); // store first level collections in object
        // add translators for classes which can't be translate automaticly be DB4O
        configuration.common().objectClass(DateTime.class).translate(new DateTimeTranslator());
        return configuration;
    }

    /**
     * Returns the storage of the given key.
     *
     * @param key key of storage
     * @return storage of the given key
     * @throws StorageException storage not exisiting
     */
    public Database getStorage(String key) throws StorageException {
        Database db = getDatabaseByKey(key);
        if (db.getObjectContainer() == null) {
            ObjectContainer objectContainer = Db4oEmbedded.openFile(getNewConfiguration(), db.getDbFile().getAbsolutePath());
            db.setObjectContainer(objectContainer);
        }
        return db;
    }

    private Database getDatabaseByKey(String dbKey) throws StorageException {
        for (Database db : databases) {
            if (db.getKey().equals(dbKey)) {
                return db;
            }
        }
        throw new StorageException("no database for key " + dbKey + " present");
    }

    /**
     * Add a new storage to the factory.
     * Binary will be created if not existing.
     *
     * @param key name of the storage
     * @return created {@link net.wbz.moba.controlcenter.db.Database}
     */
    public synchronized Database addDatabase(String key) throws IOException {
        File storageDir = storageLocationPath;
        if (!storageDir.exists() && storageDir.isDirectory()) {
            if (!storageDir.mkdirs()) {
                log.warn("can't make directories for the storage key " + key);
            }
        }
        Database db = new Database(key, new File(storageDir.getAbsolutePath() + "/" + key + ".db"));
        ObjectContainer objectContainer = Db4oEmbedded.openFile(getNewConfiguration(), db.getDbFile().getAbsolutePath());
        db.setObjectContainer(objectContainer);
        databases.add(db);
        return db;
    }

    public void deleteDatabase(String key) {
        //TODO
        throw new RuntimeException("not implemented");
    }

    public List<String> getExistingDatabaseNames() {
        return Lists.newArrayList(Collections2.transform(databases, new Function<Database, String>() {
            @Override
            public String apply(@Nullable Database database) {
                return database.getKey();
            }
        }));
    }

//    @PreDestroy
//    public void cleanUp() {
//        log.info("close all storages");
//        for (Database db : databases) {
//            if (db.getObjectContainer() != null) {
//                db.getObjectContainer().close();
//            }
//        }
//    }

    /**
     * Load the {@link net.wbz.moba.controlcenter.db.Database} for the given key.
     * If not existing, the database will be created.
     *
     * @param dbKey {@link java.lang.String} key of database
     * @return {@link net.wbz.moba.controlcenter.db.Database}
     */
    public Database getOrCreateDatabase(String dbKey) {
        Database database;
        if (!getExistingDatabaseNames().contains(dbKey)) {
            try {
                database = addDatabase(dbKey);
            } catch (IOException e) {
                throw new RuntimeException("can't init database for the 'bus' settings", e);
            }
        } else {
            try {
                database = getStorage(dbKey);
            } catch (StorageException e) {
                throw new RuntimeException("no DB found for BUS key: " + dbKey);
            }
        }
        return database;
    }
}
