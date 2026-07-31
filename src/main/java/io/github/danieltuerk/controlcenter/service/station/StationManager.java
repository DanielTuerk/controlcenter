package io.github.danieltuerk.controlcenter.service.station;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.api.station.StationDto;
import io.github.danieltuerk.controlcenter.shared.station.Station;
import io.github.danieltuerk.controlcenter.shared.station.StationDataChangedEvent;
import io.github.danieltuerk.controlcenter.shared.station.StationPlatform;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
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
    private final Event<StationDataChangedEvent> stationDataChangedEvent;

    public StationManager(EventBroadcaster eventBroadcaster, StationDataProvider dataProvider, Event<StationDataChangedEvent> stationDataChangedEvent) {
        this.eventBroadcaster = eventBroadcaster;
        this.dataProvider = dataProvider;
        this.stationDataChangedEvent = stationDataChangedEvent;
    }

    public Station create(StationDto station) {
        var id = dataProvider.createStation(station);

        fireDataChanged(id);

        return dataProvider.getStation(id).orElseThrow(() ->
            new IllegalStateException("Station with id " + id + " not found after create"));
    }

    public Station update(Long id, StationDto updated) {
        dataProvider.updateStation(id, updated);
        fireDataChanged(id);
        return dataProvider.getStation(id).orElseThrow(() ->
            new IllegalStateException("Station with id " + id + " not found after update"));
    }

    public List<Station> load() {
        return dataProvider.getStations();
    }

    public boolean deleteById(Long id) {
        var state = dataProvider.deleteStation(id);
        fireDataChanged(id);
        return state;
    }

    public boolean existsById(Long id) {
        return dataProvider.getStation(id).isPresent();
    }

    public Optional<Station> getById(Long id) {
        return dataProvider.getStation(id);
    }

    public Optional<Station> getByPlatform(StationPlatform stationPlatform) {
        return load().stream()
                .filter(s -> s.getPlatforms().contains(stationPlatform))
                .findFirst();
    }

    private void fireDataChanged(Long id) {
        final var event = new StationDataChangedEvent(id);
        stationDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }
}
