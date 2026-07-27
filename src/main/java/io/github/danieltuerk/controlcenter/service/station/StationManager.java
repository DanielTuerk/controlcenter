package io.github.danieltuerk.controlcenter.service.station;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.api.station.StationDto;
import io.github.danieltuerk.controlcenter.shared.station.Station;
import io.github.danieltuerk.controlcenter.shared.station.StationDataChangedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class StationManager {

    private final EventBroadcaster eventBroadcaster;
    private final StationDataProvider dataProvider;

    public StationManager(EventBroadcaster eventBroadcaster, StationDataProvider dataProvider) {
        this.eventBroadcaster = eventBroadcaster;
        this.dataProvider = dataProvider;
    }

    public Station create(StationDto station) {
        var id = dataProvider.createStation(station);

        eventBroadcaster.fireEvent(new StationDataChangedEvent(id));

        return dataProvider.getStation(id).orElseThrow(() ->
            new IllegalStateException("Station with id " + id + " not found after create"));
    }

    public Station update(Long id, StationDto updated) {
        dataProvider.updateStation(id, updated);
        eventBroadcaster.fireEvent(new StationDataChangedEvent(id));
        return dataProvider.getStation(id).orElseThrow(() ->
            new IllegalStateException("Station with id " + id + " not found after update"));
    }

    public List<Station> load() {
        return dataProvider.getStations();
    }

    public boolean deleteById(Long id) {
        var state = dataProvider.deleteStation(id);
        eventBroadcaster.fireEvent(new StationDataChangedEvent(id));
        return state;
    }

    public boolean existsById(Long id) {
        return dataProvider.getStation(id).isPresent();
    }

    public Optional<Station> getById(Long id) {
        return dataProvider.getStation(id);
    }
}
