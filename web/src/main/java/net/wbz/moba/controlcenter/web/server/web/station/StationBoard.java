package net.wbz.moba.controlcenter.web.server.web.station;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioStatisticManager;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioUtil;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStatistic;
import net.wbz.moba.controlcenter.shared.station.Station;
import net.wbz.moba.controlcenter.shared.station.StationBoardChangedEvent;
import net.wbz.moba.controlcenter.shared.station.StationBoardChangedEvent.TYPE;
import net.wbz.moba.controlcenter.shared.station.StationBoardEntry;
import net.wbz.moba.controlcenter.shared.station.StationPlatform;

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

    void addDeparture(Scenario scenario, Station stationEnd, StationPlatform stationPlatformStart,
        List<String> viaStations) {
        departureEntries.add(new StationBoardEntry(
            scenario.getId(), ScenarioUtil.nextExecutionTime(scenario), scenario.getTrain().getName(),
            stationEnd.getName(),
            stationPlatformStart.getName(), viaStations));

        sortEntries(departureEntries);
        fireChangedEvent(TYPE.DEPARTURE);
    }

    void addArrival(Scenario scenario, Station stationStart, StationPlatform stationPlatformEnd,
        List<String> viaStations) {
        String arrivalTimeText = getArrivalTimeText(scenario);
        arrivalEntries.add(new StationBoardEntry(scenario.getId(), arrivalTimeText,
            scenario.getTrain().getName(), stationStart.getName(), stationPlatformEnd.getName(), viaStations));
        sortEntries(arrivalEntries);
        fireChangedEvent(TYPE.ARRIVAL);
    }

    private void sortEntries(List<StationBoardEntry> boardEntries) {
        boardEntries.sort((o1, o2) -> {
            Date o1Date = ScenarioUtil.getDateFromTimeText(o1.getTimeText());
            if (o1Date == null) {
                return -1;
            }
            Date o2Date = ScenarioUtil.getDateFromTimeText(o2.getTimeText());
            if (o2Date == null) {
                return 1;
            }
            return o1Date.compareTo(o2Date);
        });
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
        removeOldestEntryForScenario(scenario, departureEntries);
        fireChangedEvent(TYPE.DEPARTURE);
    }

    void removeArrival(Scenario scenario) {
        removeOldestEntryForScenario(scenario, arrivalEntries);
        fireChangedEvent(TYPE.ARRIVAL);
    }

    private void removeOldestEntryForScenario(Scenario scenario, List<StationBoardEntry> stationBoardEntries) {
        stationBoardEntries.stream()
            .filter(equalScenarioPredicate(scenario))
            .min(Comparator.comparingLong(StationBoardEntry::getCreatedTimestamp))
            .ifPresent(stationBoardEntries::remove);
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
