package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.common.collect.Maps;


import java.util.Map;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioStatisticManager;

/**
 * @author Daniel Tuerk
 */
@Singleton
class StationBoardFactory {

    private final Map<Long, StationBoard> stationBoardsOfStationId = Maps.newConcurrentMap();
    private final ScenarioStatisticManager scenarioStatisticManager;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    StationBoardFactory(
        ScenarioStatisticManager scenarioStatisticManager,
        EventBroadcaster eventBroadcaster) {
        this.scenarioStatisticManager = scenarioStatisticManager;
        this.eventBroadcaster = eventBroadcaster;
    }

    synchronized StationBoard getStationBoard(long stationId) {
        if (!stationBoardsOfStationId.containsKey(stationId)) {
            stationBoardsOfStationId.put(stationId, new StationBoard(stationId, scenarioStatisticManager, eventBroadcaster));
        }
        return stationBoardsOfStationId.get(stationId);
    }
}
