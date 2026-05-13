package net.wbz.moba.controlcenter.service.train;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.api.train.TrainDto;
import net.wbz.moba.controlcenter.persist.entity.TrainEntity;
import net.wbz.moba.controlcenter.persist.repository.TrainRepository;
import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.moba.controlcenter.shared.train.TrainDataChangedEvent;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manager to access the {@link TrainEntity}s from database.
 *
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrainManager {

    private static final Logger LOG = Logger.getLogger(TrainManager.class);

    private final List<Train> cachedTrains = new ArrayList<>();
    private final TrainRepository trainRepository;
    private final TrainMapper trainMapper;
    private final EventBroadcaster eventBroadcaster;
    private final Event<TrainDataChangedEvent> trainDataEvent;

    @Inject
    public TrainManager(TrainRepository trainRepository, TrainMapper trainMapper, EventBroadcaster eventBroadcaster,
                        Event<TrainDataChangedEvent> trainDataEvent) {
        this.trainRepository = trainRepository;
        this.trainMapper = trainMapper;
        this.eventBroadcaster = eventBroadcaster;
        this.trainDataEvent = trainDataEvent;
    }

    /**
     * Fetch all {@link Train}s.
     *
     * @return list of {@link Train}s
     */
    public List<Train> load() {
        return getTrains();
    }

    /**
     * Create a new {@link Train} with the given data and persist it.
     * After creation the train is loaded and cached.
     *
     * @param dto data to create the {@link Train}
     * @return created {@link Train}
     */
    @Transactional
    public Train create(TrainDto dto) {
        TrainEntity entity = new TrainEntity();
        entity.name = dto.name();
        entity.address = dto.address();

        // TODO train functions

        trainRepository.persist(entity);
        fetchCreated(entity.id);
        return trainMapper.toDto(entity);
    }

    /**
     * Update the {@link Train} with the given id.
     *
     * @param id      id of the {@link Train}
     * @param updated data to update
     */
    @Transactional
    public void update(Long id, TrainDto updated) {
        TrainEntity existing = trainRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.name();
        existing.address = updated.address();
        // TODO train functions

        reloadUpdated(id);
    }

    /**
     * Delete the {@link Train} with the given id.
     *
     * @param id id of the {@link Train}
     * @return {@link boolean} true if deleted, otherwise false
     */
    @Transactional
    public boolean deleteById(Long id) {
        var state = trainRepository.deleteById(id);
        removeDeleted(id);
        return state;
    }

    /**
     * Check that a {@link Train} with the given id exists.
     *
     * @param id id of the {@link Train}
     * @return {@link boolean} true if exists, otherwise false
     */
    public boolean existsById(Long id) {
        return cachedTrains.stream().anyMatch(train -> train.getId().equals(id));
    }

    /**
     * Return the {@link Train} for the given id.
     *
     * @param id id of {@link Train}
     * @return {@link Train} of given id
     */
    public Optional<Train> getById(long id) {
        return getTrains().stream().filter(train -> train.getId().equals(id)).findFirst();
    }

    /**
     * Return the {@link Train} for the given address.
     *
     * @param address address of {@link Train}
     * @return {@link Train} of given address
     */
    public Optional<Train> getByAddress(int address) {
        return getTrains().stream().filter(train -> train.getAddress().equals(address)).findFirst();
    }

    private List<Train> getTrains() {
        if (cachedTrains.isEmpty()) {
            LOG.debugf("load from database");
            cachedTrains.addAll(
                trainRepository.getTrains().stream()
                    .map(trainMapper::toDto)
                    .toList()
            );
        }
        return cachedTrains;
    }

    private void fetchCreated(Long id) {
        LOG.debugf("fetch created train with id %d", id);
        trainRepository.getTrainById(id).map(trainMapper::toDto)
            .ifPresent(cachedTrains::add);

        fireEvent(id, AbstractItemEvent.ACTION_TYPE.CREATE);
    }

    private void removeDeleted(Long id) {
        LOG.debugf("remove deleted train with id %d", id);
        cachedTrains.stream().filter(train -> train.getId().equals(id)).findFirst()
            .ifPresent(cachedTrains::remove);
        fireEvent(id, AbstractItemEvent.ACTION_TYPE.DELETE);
    }

    private void reloadUpdated(Long id) {
        LOG.debugf("reload updated train with id %d", id);
        cachedTrains.stream()
            .filter(train -> train.getId().equals(id)).findFirst()
            .ifPresent(cachedTrains::remove);
        trainRepository.getTrainById(id).map(trainMapper::toDto)
            .ifPresent(cachedTrains::add);

        fireEvent(id, AbstractItemEvent.ACTION_TYPE.UPDATE);
    }

    private void fireEvent(Long id, AbstractItemEvent.ACTION_TYPE type) {
        var event = new TrainDataChangedEvent(id, type);
        trainDataEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
