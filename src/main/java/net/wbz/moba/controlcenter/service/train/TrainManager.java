package net.wbz.moba.controlcenter.service.train;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.api.train.TrainDto;
import net.wbz.moba.controlcenter.persist.entity.TrainEntity;
import net.wbz.moba.controlcenter.persist.repository.TrainRepository;
import net.wbz.moba.controlcenter.service.constrution.ConstructionManager;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.moba.controlcenter.shared.train.TrainDataChangedEvent;
import org.jboss.logging.Logger;

/**
 * Manager to access the {@link TrainEntity}s from database. Each {@link TrainEntity} register an {@link
 * net.wbz.selectrix4java.train.TrainDataListener} and throw the state changes by the {@link
 * net.wbz.moba.controlcenter.EventBroadcaster} as train events to the client.
 *
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrainManager {

    private static final Logger LOG = Logger.getLogger(ConstructionManager.class);

    private final List<Train> cachedTrains = new ArrayList<>();
    private final TrainRepository trainRepository;
    //    private final EventBroadcaster eventBroadcaster;
    private final TrainMapper trainMapper;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public TrainManager(TrainRepository trainRepository, TrainMapper trainMapper, EventBroadcaster eventBroadcaster) {
        this.trainRepository = trainRepository;
//        this.eventBroadcaster = eventBroadcaster;
        this.trainMapper = trainMapper;
        this.eventBroadcaster = eventBroadcaster;
    }

    public List<Train> load() {
        // TODO overall use a cache lib?
        return getTrains();
    }

    @Transactional
    public Train create(TrainDto dto) {
        TrainEntity entity = new TrainEntity();
        entity.name = dto.name();
        entity.address = dto.address();

        trainRepository.persist(entity);
        reloadData(entity.id);
        return trainMapper.toDto(entity);
    }

    private void reloadData(Long id) {
        // TODO
        cachedTrains.clear();
        getTrains();
        eventBroadcaster.fireEvent(new TrainDataChangedEvent(id));
    }

    @Transactional
    public void update(Long id, TrainDto updated) {
        TrainEntity existing = trainRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.name();
        existing.address = updated.address();
        // TODO throw change event
        reloadData(id);
    }

    @Transactional
    public boolean deleteById(Long id) {
        reloadData(id);
        return trainRepository.deleteById(id);
        // TODO reload trains
    }

    public boolean existsById(Long id) {
        return cachedTrains.stream().anyMatch(train -> train.getId().equals(id));
    }

    private List<Train> getTrains() {
        if (cachedTrains.isEmpty()) {
            cachedTrains.addAll(
                trainRepository.getTrains().stream()
                    .map(trainMapper::toDto)
                    .toList()
            );
        }
        return cachedTrains;
    }

// TODO
//    private synchronized void reloadTrains() {
//        cachedTrains.clear();
//        Collection<Train> trains = getTrains();
//        if (deviceManager.isConnected()) {
//            trains.forEach(train -> {
//                try {
//                    reregisterConsumer(train, deviceManager);
//                } catch (DeviceAccessException e) {
//                    LOG.error("can't register consumer for train ({})", train, e);
//                }
//            });
//        }
//    }



    /**
     * Return the {@link Train} for the given id.
     *
     * @param id id of {@link Train}
     * @return {@link Train} of given id or {@code null} if not found
     */
    public Optional<Train> getById(long id) {
        return getTrains().stream().filter(train -> train.getId().equals(id)).findFirst();
    }

    /**
     * Return the {@link Train} for the given address.
     *
     * @param address address of {@link Train}
     * @return {@link Train} of given address or {@code null} if not found
     */
    public Optional<Train> getByAddress(int address) {
        return getTrains().stream().filter(train -> train.getAddress().equals(address)).findFirst();
    }

}
