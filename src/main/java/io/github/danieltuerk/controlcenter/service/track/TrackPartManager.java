package io.github.danieltuerk.controlcenter.service.track;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.api.track.ChangeTrackDto;
import io.github.danieltuerk.controlcenter.service.constrution.ConstructionService;
import io.github.danieltuerk.controlcenter.shared.track.model.AbstractTrackPart;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackChangedEvent;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackPartDataChangedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackPartManager {

    private final EventBroadcaster eventBroadcaster;
    private final ConstructionService constructionService;
    private final TrackDataProvider trackDataProvider;
    private final Event<TrackChangedEvent> trackChangedEvent;
    private final Event<TrackPartDataChangedEvent> trackPartDataChangedEvent;

    public TrackPartManager(EventBroadcaster eventBroadcaster, ConstructionService constructionService,
                            TrackDataProvider trackDataProvider, Event<TrackChangedEvent> trackChangedEvent,
                            Event<TrackPartDataChangedEvent> trackPartDataChangedEvent) {
        this.eventBroadcaster = eventBroadcaster;
        this.constructionService = constructionService;
        this.trackDataProvider = trackDataProvider;
        this.trackChangedEvent = trackChangedEvent;
        this.trackPartDataChangedEvent = trackPartDataChangedEvent;
    }

    public void update(Long id, AbstractTrackPart updated) {
        trackDataProvider.update(id, updated);
        fireTrackPartDataChangedEvent(id);
    }

    public void change(ChangeTrackDto dto) {
        if (dto.isEmpty()) {
            return;
        }

        final var constructionId = constructionService.getCurrentConstruction()
            .orElseThrow(() -> new IllegalStateException("no current construction"))
            .getId();

        dto.addActions().forEach(action -> trackDataProvider.add(action, constructionId));
        dto.moveActions().forEach(action -> trackDataProvider.move(action.trackPartId(), action.gridPosition()));
        dto.rotateActions().forEach(action -> trackDataProvider.rotate(action.trackPartId(), action.value()));

        fireTrackChangedEvent(true);
    }

    public boolean deleteById(Long id) {
        var state = trackDataProvider.deleteById(id);
        fireTrackChangedEvent(true);
        return state;
    }

    private void fireTrackPartDataChangedEvent(Long id) {
        final var event = new TrackPartDataChangedEvent(id);
        trackPartDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

    private void fireTrackChangedEvent(boolean dirty) {
        final var event = new TrackChangedEvent(dirty);
        trackChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
