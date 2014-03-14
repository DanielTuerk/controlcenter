package net.wbz.moba.controlcenter.web.server.train;

import com.db4o.ObjectSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.wbz.moba.controlcenter.db.Database;
import net.wbz.moba.controlcenter.db.DatabaseFactory;
import net.wbz.moba.controlcenter.db.StorageException;
import net.wbz.moba.controlcenter.web.shared.train.Train;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 08.03.14.
 */
@Singleton
public class TrainManager {

    private final Map<Long, Train> trains = Maps.newConcurrentMap();
    private static final String TRAINS_DB_KEY = "trains";

    private final Database database;

    @Inject
    public TrainManager(@Named(TRAINS_DB_KEY) DatabaseFactory databaseFactory) {
        if (!databaseFactory.getExistingDatabaseNames().contains(TRAINS_DB_KEY)) {
            try {
                database = databaseFactory.addDatabase(TRAINS_DB_KEY);
            } catch (IOException e) {
                throw new RuntimeException("can't init database for the 'bus' settings", e);
            }
        } else {
            try {
                database = databaseFactory.getStorage(TRAINS_DB_KEY);
                loadFromDatabase();
            } catch (StorageException e) {
                throw new RuntimeException("no DB found for trains key: " + TRAINS_DB_KEY);
            }
        }
    }

    public Train getTrain(long id) {
        if (trains.containsKey(id)) {
            return trains.get(id);
        }
        throw new RuntimeException(String.format("no train found for id %d", id));
    }

    public void storeTrain(Train train) throws Exception {
        database.getObjectContainer().store(train);
        database.getObjectContainer().commit();

        loadFromDatabase();
    }

    private void loadFromDatabase() {
        trains.clear();
        ObjectSet<Train> storedDevices = database.getObjectContainer().query(Train.class);
        for (Train train : storedDevices) {
            trains.put(train.getId(), train);
        }
    }

    public List<Train> getTrains() {
        return Lists.newArrayList(trains.values());
    }
}