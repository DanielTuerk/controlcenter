package net.wbz.moba.controlcenter.web.server.web.constrution;

import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Singleton;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionDao;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.server.web.editor.TrackManager;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.shared.constrution.CurrentConstructionChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ConstructionServiceImpl extends RemoteServiceServlet implements ConstructionService {

    private static final Logger LOG = LoggerFactory.getLogger(ConstructionServiceImpl.class);

    private final ConstructionDao dao;
    private final DataMapper<Construction, ConstructionEntity> mapper;
    private final EventBroadcaster eventBroadcaster;
    private final List<ConstructionChangeListener> listeners = new ArrayList<>();

    private Construction currentConstruction = null;

    @Inject
    public ConstructionServiceImpl(ConstructionDao dao, TrackManager trackManager, EventBroadcaster eventBroadcaster) {
        this.dao = dao;
        this.eventBroadcaster = eventBroadcaster;
        mapper = new DataMapper<>(Construction.class, ConstructionEntity.class);

        addListener(trackManager);
    }

    public void addListener(ConstructionChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConstructionChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Construction getCurrentConstruction() {
        return currentConstruction;
    }

    @Override
    public synchronized void setCurrentConstruction(Construction construction) {
        currentConstruction = construction;
        LOG.info("current construction changed to: {}", construction);
        eventBroadcaster.fireEvent(new CurrentConstructionChangeEvent(construction));
        listeners.forEach(listener -> listener.currentConstructionChanged(construction));
    }

    @Override
    @Transactional
    public void createConstruction(Construction construction) {
        ConstructionEntity entity = new ConstructionEntity();
        entity.setName(construction.getName());
        dao.create(entity);
    }

    @Override
    public Collection<Construction> loadConstructions() {
        return Lists.newArrayList(mapper.transformSource(dao.listConstructions()));
    }

}
