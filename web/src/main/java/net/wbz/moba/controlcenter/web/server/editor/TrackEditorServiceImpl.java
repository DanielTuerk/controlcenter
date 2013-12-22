package net.wbz.moba.controlcenter.web.server.editor;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.QueryComparator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.db.model.DataContainer;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Singleton
public class TrackEditorServiceImpl extends RemoteServiceServlet implements TrackEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorServiceImpl.class);

    private final ConstructionServiceImpl constructionService;

    @Inject
    public TrackEditorServiceImpl(ConstructionServiceImpl constructionService) {
        this.constructionService = constructionService;
    }

    @Override
    public void saveTrack(TrackPart[] trackParts) {
        ObjectContainer database = constructionService.getDatabase(constructionService.getCurrentConstruction()).getObjectContainer();
        DataContainer dataContainer = new DataContainer(DateTime.now().getMillis(), trackParts);
        database.store(dataContainer);
        database.commit();
    }

    @Override
    public TrackPart[] loadTrack() {
        LOG.info("load db");
        ObjectContainer database = constructionService.getDatabase(constructionService.getCurrentConstruction()).getObjectContainer();

        LOG.info("query db");
        ObjectSet<DataContainer> f = database.query(
                new Predicate<DataContainer>() {
                    @Override
                    public boolean match(DataContainer dataContainer) {
                        return true;
                    }
                }, new QueryComparator<DataContainer>() {
                    @Override
                    public int compare(DataContainer dataContainer, DataContainer dataContainer1) {
                        LOG.info("compare data container");
                        return Long.valueOf(dataContainer1.getDateTime()).compareTo(dataContainer.getDateTime());
                    }
                }
        );
        if (!f.isEmpty()) {
            LOG.info("return track parts");
            return (TrackPart[]) f.get(0).getData();
        }
        return new TrackPart[0];
    }

}
