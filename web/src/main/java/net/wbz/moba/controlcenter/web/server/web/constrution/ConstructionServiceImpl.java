package net.wbz.moba.controlcenter.web.server.web.constrution;

import java.util.Collection;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionDao;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.server.web.editor.TrackManager;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.shared.constrution.CurrentConstructionChangeEvent;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ConstructionServiceImpl extends RemoteServiceServlet implements ConstructionService {

    private static final Logger LOG = LoggerFactory.getLogger(ConstructionServiceImpl.class);

    private final ConstructionDao dao;
    private final DataMapper<Construction, ConstructionEntity> mapper;
    private final TrackManager trackManager;
    private final EventBroadcaster eventBroadcaster;

    private Construction currentConstruction = null;

    @Inject
    public ConstructionServiceImpl(ConstructionDao dao, TrackManager trackManager, EventBroadcaster eventBroadcaster) {
        this.dao = dao;
        this.trackManager = trackManager;
        this.eventBroadcaster = eventBroadcaster;
        mapper = new DataMapper<>(Construction.class, ConstructionEntity.class);
    }

    @Override
    public Construction getCurrentConstruction() {
        return currentConstruction;
    }

    @Override
    public synchronized void setCurrentConstruction(Construction construction) {
        currentConstruction = construction;
        trackManager.constructionChanged(construction);
        LOG.info("current construction changed to: {}", construction);
        eventBroadcaster.fireEvent(new CurrentConstructionChangeEvent(construction));
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
