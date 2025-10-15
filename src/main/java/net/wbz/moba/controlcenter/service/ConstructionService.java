package net.wbz.moba.controlcenter.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.api.ConstructionDto;
import net.wbz.moba.controlcenter.model.ConstructionMapper;
import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
import net.wbz.moba.controlcenter.persist.repository.ConstructionRepository;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import org.jboss.logging.Logger;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ConstructionService {

    private static final Logger LOG = Logger.getLogger(ConstructionService.class);
    private final List<ConstructionChangeListener> listeners = new ArrayList<>();
    private final AtomicReference<Construction> currentConstruction = new AtomicReference<>(null);
    @Inject
    ConstructionRepository constructionRepository;
    @Inject
    ConstructionMapper constructionMapper;
    @Inject
    EventBroadcaster eventBroadcaster;

//    @Inject
//    public ConstructionService(ConstructionDao dao, TrackManager trackManager, EventBroadcaster eventBroadcaster) {
//        this.dao = dao;
//        this.eventBroadcaster = eventBroadcaster;
//        mapper = new DataMapper<>(Construction.class, ConstructionEntity.class);
//
//    TODO: for what was that? We also rely on events on server side, so we need an abstraction for server and client events
//        addListener(trackManager);
//    }

//    public void addListener(ConstructionChangeListener listener) {
//        listeners.add(listener);
//    }
//
//    public void removeListener(ConstructionChangeListener listener) {
//        listeners.remove(listener);
//    }

    public Optional<Construction> getCurrentConstruction() {
        return Optional.ofNullable(currentConstruction.get());
    }

    public synchronized void setCurrentConstruction(Construction construction) {
        currentConstruction.set(construction);
        LOG.infof("current construction changed to: {}", construction);
        // TODO: that should be used by server and clients
        eventBroadcaster.fireEvent(new CurrentConstructionChangeEvent(construction));
        // TODO: not needed if events are propagated to server listeners
        listeners.forEach(listener -> listener.currentConstructionChanged(construction));
    }

    @Transactional
    public Construction createConstruction(ConstructionDto construction) {
        ConstructionEntity entity = new ConstructionEntity();
        entity.name = construction.name();
        constructionRepository.persist(entity);
        return constructionMapper.toDto(entity);
    }

    @Transactional
    public void updateConstruction(Long id, ConstructionDto updated) {
        ConstructionEntity existing = constructionRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.name();
        // TODO throw change event
    }

    public List<Construction> loadConstructions() {
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
