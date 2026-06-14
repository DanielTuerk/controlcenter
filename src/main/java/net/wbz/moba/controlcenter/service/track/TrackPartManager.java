package net.wbz.moba.controlcenter.service.track;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.api.track.ChangeTrackDto;
import net.wbz.moba.controlcenter.service.constrution.ConstructionService;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.TrackChangedEvent;
import net.wbz.moba.controlcenter.shared.track.model.TrackPartDataChangedEvent;

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
