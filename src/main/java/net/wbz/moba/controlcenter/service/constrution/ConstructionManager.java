package net.wbz.moba.controlcenter.service.constrution;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.api.construction.ConstructionDto;
import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
import net.wbz.moba.controlcenter.persist.repository.ConstructionRepository;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.constrution.ConstructionDataChangedEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class ConstructionManager {

    @Inject
    ConstructionRepository constructionRepository; // kept for other services that might use it directly
    @Inject
    ConstructionMapper constructionMapper;
    @Inject
    EventBroadcaster eventBroadcaster;
    @Inject
    ConstructionDataProvider dataProvider;

    public Construction create(ConstructionDto construction) {
        ConstructionEntity entity = dataProvider.createConstruction(construction);

        eventBroadcaster.fireEvent(new ConstructionDataChangedEvent(entity.id));

        return constructionMapper.toDto(entity);
    }

    public ConstructionEntity update(Long id, ConstructionDto updated) {
        final var entity = dataProvider.updateConstruction(id, updated);

        eventBroadcaster.fireEvent(new ConstructionDataChangedEvent(id));
        return entity;
    }

    public List<Construction> load() {
        return dataProvider.getConstructions().stream()
            .map(constructionMapper::toDto)
            .collect(Collectors.toList());
    }

    public Optional<Construction> getById(Long id) {
        return load().stream()
            .filter(construction -> id.equals(construction.getId()))
            .findFirst();
    }

    public boolean deleteById(Long id) {
        var state = dataProvider.deleteConstruction(id);
        eventBroadcaster.fireEvent(new ConstructionDataChangedEvent(id));
        return state;
    }

    public boolean existsById(Long id) {
        return getById(id).isPresent();
    }

}
