package net.wbz.moba.controlcenter.service.constrution;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import org.jboss.logging.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ConstructionService {
    private static final Logger LOG = Logger.getLogger(ConstructionService.class);

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
        LOG.infof("current construction changed to: %s", construction);

        final var event = new CurrentConstructionChangeEvent(construction);
        currentConstructionChangeEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

}
