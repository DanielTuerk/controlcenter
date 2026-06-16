package net.wbz.moba.controlcenter.service.constrution;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.service.config.ConfigService;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
@Startup
public class ConstructionService {

    private final AtomicReference<Construction> currentConstruction = new AtomicReference<>(null);
    private final EventBroadcaster eventBroadcaster;
    private final ConfigService configService;
    private final Event<CurrentConstructionChangeEvent> currentConstructionChangeEvent;
    private final ConstructionManager constructionManager;

    @Inject
    public ConstructionService(EventBroadcaster eventBroadcaster, ConfigService configService, Event<CurrentConstructionChangeEvent> currentConstructionChangeEvent, ConstructionManager constructionManager) {
        this.eventBroadcaster = eventBroadcaster;
        this.configService = configService;
        this.currentConstructionChangeEvent = currentConstructionChangeEvent;
        this.constructionManager = constructionManager;
    }

    void onStart(@Observes StartupEvent ev) {
        log.debug("check for current construction from config");
        if (getCurrentConstruction().isEmpty()) {
            configService.getDefaultConstructionId()
                .flatMap(constructionManager::getById)
                .ifPresent(this::setCurrentConstruction);
        }
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
