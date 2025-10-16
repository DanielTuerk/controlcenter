package net.wbz.moba.controlcenter.service.constrution;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.api.construction.ConstructionDto;
import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
import net.wbz.moba.controlcenter.persist.repository.ConstructionRepository;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import org.jboss.logging.Logger;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ConstructionManager {

    private static final Logger LOG = Logger.getLogger(ConstructionManager.class);

    @Inject
    ConstructionRepository constructionRepository;
    @Inject
    ConstructionMapper constructionMapper;

//    @Inject
//    public ConstructionService(ConstructionDao dao, TrackManager trackManager, EventBroadcaster eventBroadcaster) {
//        this.dao = dao;
//        this.eventBroadcaster = eventBroadcaster;
//        mapper = new DataMapper<>(Construction.class, ConstructionEntity.class);
//
//    TODO: for what was that? We also rely on events on server side, so we need an abstraction for server and client events
//        addListener(trackManager);
//    }

    @Transactional
    public Construction create(ConstructionDto construction) {
        ConstructionEntity entity = new ConstructionEntity();
        entity.name = construction.name();
        constructionRepository.persist(entity);
        return constructionMapper.toDto(entity);
    }

    @Transactional
    public void update(Long id, ConstructionDto updated) {
        ConstructionEntity existing = constructionRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.name();
        // TODO throw change event
    }

    public List<Construction> load() {
        return constructionRepository.streamAll()
            .map(constructionMapper::toDto)
            .collect(Collectors.toList());
    }

    public Optional<Construction> getById(Long id) {
        return constructionRepository.findByIdOptional(id).map(constructionMapper::toDto);
    }

    @Transactional
    public boolean deleteById(Long id) {
        return constructionRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return constructionRepository.findByIdOptional(id).isPresent();
    }
}
