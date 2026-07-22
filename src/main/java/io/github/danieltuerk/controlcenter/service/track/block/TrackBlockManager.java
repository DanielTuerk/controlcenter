package io.github.danieltuerk.controlcenter.service.track.block;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlock;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlockDataChangedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackBlockManager {

    @Inject
    EventBroadcaster eventBroadcaster;
    @Inject
    TrackBlockDataProvider dataProvider;
    @Inject
    TrackBlockMapper trackBlockMapper;
    @Inject
    Event<TrackBlockDataChangedEvent> tackBlockDataEvent;

    public List<TrackBlock> fetch(Long constructionId) {
        return dataProvider.load(constructionId).stream()
            .map(trackBlockMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public TrackBlock create(Long constructionId, TrackBlock dto) {
        var entity = dataProvider.create(constructionId, dto);
        fireEvent(entity.id, AbstractItemEvent.ACTION_TYPE.CREATE);
        return trackBlockMapper.toDto(entity);
    }

    @Transactional
    public TrackBlock update(Long id, TrackBlock updated) {
        final var entity = dataProvider.update(id, updated);
        fireEvent(id, AbstractItemEvent.ACTION_TYPE.UPDATE);
        return trackBlockMapper.toDto(entity);
    }

    @Transactional
    public boolean deleteById(Long id) {
        var state = dataProvider.deleteById(id);
        fireEvent(id, AbstractItemEvent.ACTION_TYPE.DELETE);
        return state;
    }

    public boolean existsById(Long id) {
        return getById(id).isPresent();
    }

    public Optional<TrackBlock> getById(Long trackBlockId) {
        return dataProvider.getById(trackBlockId)
            .map(trackBlockMapper::toDto);
    }

    private void fireEvent(Long id, AbstractItemEvent.ACTION_TYPE type) {
        var event = new TrackBlockDataChangedEvent(id, type);
        tackBlockDataEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
