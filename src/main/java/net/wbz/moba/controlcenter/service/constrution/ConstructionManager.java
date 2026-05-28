package net.wbz.moba.controlcenter.service.constrution;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.api.construction.ConstructionDto;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.constrution.ConstructionDataChangedEvent;

import java.util.List;
import java.util.Optional;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class ConstructionManager {

    private final EventBroadcaster eventBroadcaster;
    private final ConstructionDataProvider dataProvider;

    public ConstructionManager(EventBroadcaster eventBroadcaster, ConstructionDataProvider dataProvider) {
        this.eventBroadcaster = eventBroadcaster;
        this.dataProvider = dataProvider;
    }

    public Construction create(ConstructionDto construction) {
        var id = dataProvider.createConstruction(construction);

        eventBroadcaster.fireEvent(new ConstructionDataChangedEvent(id));

        return dataProvider.getConstruction(id).orElseThrow(() ->
            new IllegalStateException("Construction with id " + id + " not found after create"));
    }

    public Construction update(Long id, ConstructionDto updated) {
        dataProvider.updateConstruction(id, updated);
        eventBroadcaster.fireEvent(new ConstructionDataChangedEvent(id));
        return dataProvider.getConstruction(id).orElseThrow(() ->
            new IllegalStateException("Construction with id " + id + " not found after update"));
    }

    public List<Construction> load() {
        return dataProvider.getConstructions();
    }

    public boolean deleteById(Long id) {
        var state = dataProvider.deleteConstruction(id);
        eventBroadcaster.fireEvent(new ConstructionDataChangedEvent(id));
        return state;
    }

    public boolean existsById(Long id) {
        return dataProvider.getConstruction(id).isPresent();
    }

    public Optional<Construction> getById(Long id) {
        return dataProvider.getConstruction(id);
    }
}
