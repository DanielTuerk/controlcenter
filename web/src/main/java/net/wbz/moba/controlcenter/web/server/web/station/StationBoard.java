package net.wbz.moba.controlcenter.web.server.web.station;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioStatisticManager;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioUtil;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatistic;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import net.wbz.moba.controlcenter.web.shared.station.StationBoardChangedEvent;
import net.wbz.moba.controlcenter.web.shared.station.StationBoardChangedEvent.TYPE;
import net.wbz.moba.controlcenter.web.shared.station.StationBoardEntry;
import net.wbz.moba.controlcenter.web.shared.station.StationPlatform;

/**
 * @author Daniel Tuerk
 */
class StationBoard {

    private final List<StationBoardEntry> arrivalEntries = new ArrayList<>();
    private final List<StationBoardEntry> departureEntries = new ArrayList<>();
    private final ScenarioStatisticManager scenarioStatisticManager;
    private final EventBroadcaster eventBroadcaster;
    private final long stationId;

    StationBoard(
        long stationId, ScenarioStatisticManager scenarioStatisticManager,
        EventBroadcaster eventBroadcaster) {
        this.stationId = stationId;
        this.scenarioStatisticManager = scenarioStatisticManager;
        this.eventBroadcaster = eventBroadcaster;
    }

    List<StationBoardEntry> getArrivalEntries() {
        return arrivalEntries;
    }

    List<StationBoardEntry> getDepartureEntries() {
        return departureEntries;
    }

    private String getArrivalTimeText(Scenario scenario) {
        Optional<ScenarioStatistic> scenarioStatistic = scenarioStatisticManager.load(scenario.getId());
        String arrivalTimeText;
        if (scenarioStatistic.isPresent()) {
            arrivalTimeText = ScenarioUtil.arrivalTimeOfNextExecution(scenario,
                (long) scenarioStatistic.get().getAverageRunTimeInMillis());
        } else {
            arrivalTimeText = "--:--";
        }
        return arrivalTimeText;
    }

    void addDeparture(Scenario scenario, Station stationEnd,
        StationPlatform stationPlatformStart) {
        departureEntries.add(new StationBoardEntry(
            scenario.getId(), ScenarioUtil.nextExecutionTime(scenario), scenario.getTrain().getName(),
            stationEnd.getName(),
            stationPlatformStart.getName()));
        fireChangedEvent(TYPE.DEPARTURE);
    }

    void addArrival(Scenario scenario, Station stationStart, StationPlatform stationPlatformEnd) {
        String arrivalTimeText = getArrivalTimeText(scenario);
        arrivalEntries.add(new StationBoardEntry(scenario.getId(), arrivalTimeText,
            scenario.getTrain().getName(), stationStart.getName(), stationPlatformEnd.getName()));
        fireChangedEvent(TYPE.ARRIVAL);
    }

    void updateDeparture(Scenario scenario, String information) {
        updateInformation(departureEntries, scenario, information);
        fireChangedEvent(TYPE.DEPARTURE);
    }

    void updateArrival(Scenario scenario, String information) {
        updateInformation(arrivalEntries, scenario, information);
        fireChangedEvent(TYPE.ARRIVAL);
    }

    void removeDeparture(Scenario scenario) {
        departureEntries.removeIf(equalScenarioPredicate(scenario));
        fireChangedEvent(TYPE.DEPARTURE);
    }

    void removeArrival(Scenario scenario) {
        arrivalEntries.removeIf(equalScenarioPredicate(scenario));
        fireChangedEvent(TYPE.ARRIVAL);
    }

    private Predicate<StationBoardEntry> equalScenarioPredicate(Scenario scenario) {
        return stationBoardEntry -> Objects.equals(stationBoardEntry.getScenarioId(), scenario.getId());
    }

    private void updateInformation(List<StationBoardEntry> entries, Scenario scenario, String canceledInformation) {
        entries.stream()
            .filter(equalScenarioPredicate(scenario))
            .forEach(action -> action.setInformation(canceledInformation));
    }

    private void fireChangedEvent(TYPE type) {
        eventBroadcaster.fireEvent(new StationBoardChangedEvent(type, stationId));
    }
}
