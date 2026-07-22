package io.github.danieltuerk.controlcenter.service.train;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.api.train.TrainDto;
import io.github.danieltuerk.controlcenter.persist.entity.TrainEntity;
import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import io.github.danieltuerk.controlcenter.shared.train.Train;
import io.github.danieltuerk.controlcenter.shared.train.TrainDataChangedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * Manager to access the {@link TrainEntity}s from database.
 *
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrainManager {

    private final EventBroadcaster eventBroadcaster;
    private final Event<TrainDataChangedEvent> trainDataEvent;
    private final TrainDataProvider dataProvider;

    @Inject
    public TrainManager(EventBroadcaster eventBroadcaster, Event<TrainDataChangedEvent> trainDataEvent,
                        TrainDataProvider dataProvider) {
        this.eventBroadcaster = eventBroadcaster;
        this.trainDataEvent = trainDataEvent;
        this.dataProvider = dataProvider;
    }

    /**
     * Fetch all {@link Train}s.
     *
     * @return list of {@link Train}s
     */
    public List<Train> load() {
        return dataProvider.getTrains();
    }

    /**
     * Create a new {@link Train} with the given data and persist it.
     * After creation the train is loaded and cached.
     *
     * @param dto data to create the {@link Train}
     * @return created {@link Train}
     */
    public Train create(TrainDto dto) {
        var id = dataProvider.createTrain(dto);
        fireEvent(id, AbstractItemEvent.ACTION_TYPE.CREATE);
        return dataProvider.getById(id).orElseThrow(() ->
            new IllegalArgumentException("created train not found: " + id));
    }

    /**
     * Update the {@link Train} with the given id.
     *
     * @param id      id of the {@link Train}
     * @param updated data to update
     * @return updated {@link TrainEntity}
     */
    public Train update(Long id, TrainDto updated) {
        dataProvider.updateTrain(id, updated);
        fireEvent(id, AbstractItemEvent.ACTION_TYPE.UPDATE);
        return dataProvider.getById(id).orElseThrow(() ->
            new IllegalArgumentException("updated train not found: " + id));
    }

    /**
     * Delete the {@link Train} with the given id.
     *
     * @param id id of the {@link Train}
     * @return {@link boolean} true if deleted, otherwise false
     */
    public boolean deleteById(Long id) {
        var state = dataProvider.deleteTrain(id);
        fireEvent(id, AbstractItemEvent.ACTION_TYPE.DELETE);
        return state;
    }

    /**
     * Check that a {@link Train} with the given id exists.
     *
     * @param id id of the {@link Train}
     * @return {@link boolean} true if exists, otherwise false
     */
    public boolean existsById(Long id) {
        return getById(id).isPresent();
    }

    /**
     * Return the {@link Train} for the given id.
     *
     * @param id id of {@link Train}
     * @return {@link Train} of given id
     */
    public Optional<Train> getById(long id) {
        return load().stream().filter(train -> train.getId().equals(id)).findFirst();
    }

    /**
     * Return the {@link Train} for the given address.
     *
     * @param address address of {@link Train}
     * @return {@link Train} of given address
     */
    public Optional<Train> getByAddress(int address) {
        return load().stream().filter(train -> train.getAddress().equals(address)).findFirst();
    }

    private void fireEvent(Long id, AbstractItemEvent.ACTION_TYPE type) {
        var event = new TrainDataChangedEvent(id, type);
        trainDataEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
