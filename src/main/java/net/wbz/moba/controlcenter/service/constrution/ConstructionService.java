package net.wbz.moba.controlcenter.service.constrution;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class ConstructionService {

    private final AtomicReference<Construction> currentConstruction = new AtomicReference<>(null);
    private final EventBroadcaster eventBroadcaster;

    private final Event<CurrentConstructionChangeEvent> currentConstructionChangeEvent;

    @Inject
    public ConstructionService(EventBroadcaster eventBroadcaster, Event<CurrentConstructionChangeEvent> currentConstructionChangeEvent) {
        this.eventBroadcaster = eventBroadcaster;
        this.currentConstructionChangeEvent = currentConstructionChangeEvent;
    }

    public Optional<Construction> getCurrentConstruction() {
        return Optional.ofNullable(currentConstruction.get());
    }

    public synchronized void setCurrentConstruction(Construction construction) {
        currentConstruction.set(construction);
        log.info("current construction changed to: {}", construction);

        final var event = new CurrentConstructionChangeEvent(construction);
        currentConstructionChangeEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

}
