package net.wbz.moba.controlcenter.web.server.web.constrution;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionDao;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.server.web.DtoMapper;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ConstructionServiceImpl extends RemoteServiceServlet implements ConstructionService {

    private static final Logger LOG = LoggerFactory.getLogger(ConstructionServiceImpl.class);

    private final ConstructionDao dao;
    private final DtoMapper<Construction, ConstructionEntity> mapper;

    private Construction currentConstruction = null;

    @Inject
    public ConstructionServiceImpl(ConstructionDao dao) {
        this.dao = dao;
        mapper = new DtoMapper<>();
    }

    public Construction getCurrentConstruction() {
        return currentConstruction;
    }

    public void setCurrentConstruction(Construction construction) {
        currentConstruction = construction;
    }

    @Transactional
    public void createConstruction(Construction construction) {
        ConstructionEntity trackPart = new ConstructionEntity();
        trackPart.setName(construction.getName());
        dao.create(trackPart);
    }

    @Transactional
    public synchronized List<Construction> loadConstructions() {
        return mapper.transform(dao.listConstructions());
    }

}
