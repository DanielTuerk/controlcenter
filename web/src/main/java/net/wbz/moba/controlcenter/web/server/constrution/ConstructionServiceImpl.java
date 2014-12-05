package net.wbz.moba.controlcenter.web.server.constrution;

import com.db4o.ObjectContainer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.wbz.moba.controlcenter.db.Database;
import net.wbz.moba.controlcenter.db.DatabaseFactory;
import net.wbz.moba.controlcenter.db.StorageException;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Singleton
public class ConstructionServiceImpl extends RemoteServiceServlet implements ConstructionService {

    private static final Logger log = LoggerFactory.getLogger(ConstructionServiceImpl.class);

    private final DatabaseFactory databaseFactory;

    private Construction currentConstruction = null;

    @Inject
    public ConstructionServiceImpl(@Named("construction") DatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public ObjectContainer getObjectContainerOfCurrentConstruction() {
        return getDatabase(getCurrentConstruction()).getObjectContainer();
    }

    @Override
    public Construction getCurrentConstruction() {
        return currentConstruction;
    }

    public Database getDatabase(Construction construction) {
        try {
            return databaseFactory.getStorage(construction.getName());
        } catch (StorageException e) {
            log.error("can't load database " + construction.getName(), e);
        }
        return null;
    }

    @Override
    public void createConstruction(Construction construction) {
        try {
            databaseFactory.addDatabase(construction.getName());
            ObjectContainer database = getDatabase(construction).getObjectContainer();
            database.store(currentConstruction);
            database.commit();
        } catch (IOException e) {
            log.error("create construction failed", e);
        }
    }

    @Override
    public Construction[] loadConstructions() {
        log.info("load construction");
        List<Construction> existingConstructions = new ArrayList<Construction>();
        for (String databaseKey : databaseFactory.getExistingDatabaseNames()) {
            log.info("read construction: " + databaseKey);
            Construction existingConstruction = new Construction();
            existingConstruction.setName(databaseKey);
            existingConstructions.add(existingConstruction);
        }
        return existingConstructions.toArray(new Construction[existingConstructions.size()]);
    }

    @Override
    public void setCurrentConstruction(Construction construction) {
        currentConstruction = construction;
    }
}
