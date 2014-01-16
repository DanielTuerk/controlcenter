package net.wbz.moba.controlcenter.web.server.editor;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
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

    private static final Logger log = LoggerFactory.getLogger(TrackEditorServiceImpl.class);

    private final ConstructionServiceImpl constructionService;

    @Inject
    public TrackEditorServiceImpl(ConstructionServiceImpl constructionService) {
        this.constructionService = constructionService;
    }

    @Override
    public void saveTrack(TrackPart[] trackParts) {
        ObjectContainer database = constructionService.getObjectContainerOfCurrentConstruction();
        DataContainer dataContainer = new DataContainer(DateTime.now().getMillis(), trackParts);
        database.store(dataContainer);
        database.commit();
    }


    @Override
    public TrackPart[] loadTrack() {
        log.info("load db");
        ObjectContainer database = constructionService.getObjectContainerOfCurrentConstruction();

        log.info("query db");
        Query query = database.query();
        query.constrain(DataContainer.class);
        query.descend("dateTime").orderDescending();
        ObjectSet<DataContainer> result = query.execute();

        if (!result.isEmpty()) {
            log.info("return track parts");
            return (TrackPart[]) result.get(0).getData();
        } else {
            log.warn("the current construction is empty");
            return new TrackPart[0];
        }
    }

    //    @PreDestroy
    public void cleanUp() {
        //TODO
    }
}
