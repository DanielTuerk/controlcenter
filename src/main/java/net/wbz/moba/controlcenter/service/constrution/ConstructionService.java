package net.wbz.moba.controlcenter.service.constrution;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import org.jboss.logging.Logger;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ConstructionService {
    private static final Logger LOG = Logger.getLogger(ConstructionService.class);

    // TODO migrate to events?
    private final List<ConstructionChangeListener> listeners = new ArrayList<>();

    private final AtomicReference<Construction> currentConstruction = new AtomicReference<>(null);
    @Inject
    EventBroadcaster eventBroadcaster;

    public Optional<Construction> getCurrentConstruction() {
        return Optional.ofNullable(currentConstruction.get());
    }

    public synchronized void setCurrentConstruction(Construction construction) {
        currentConstruction.set(construction);
        LOG.infof("current construction changed to: %s", construction);
        // TODO: that should be used by server and clients
        eventBroadcaster.fireEvent(new CurrentConstructionChangeEvent(construction));
        // TODO: not needed if events are propagated to server listeners
        listeners.forEach(listener -> listener.currentConstructionChanged(construction));
    }


    public void addListener(ConstructionChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConstructionChangeListener listener) {
        listeners.remove(listener);
    }
}
