package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioStateListener;
import net.wbz.moba.controlcenter.web.server.web.scenario.execution.ScenarioExecutor;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatisticService;
import net.wbz.moba.controlcenter.web.shared.station.StationBoardEntry;
import net.wbz.moba.controlcenter.web.shared.station.StationsBoardService;

/**
 * Implementation of {@link ScenarioStatisticService}.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class StationsBoardServiceImpl extends RemoteServiceServlet implements StationsBoardService {

    private final StationBoardFactory stationBoardFactory;

    @Inject
    public StationsBoardServiceImpl(StationManager stationManager, ScenarioExecutor scenarioExecutor,
        StationBoardFactory stationBoardFactory,
        QueuedScenarioBoardConsumer queuedScenarioBoardConsumer,
        RemoveScenarioBoardConsumer removeScenarioBoardConsumer,
        UpdateScenarioBoardConsumer updateScenarioBoardConsumer) {
        this.stationBoardFactory = stationBoardFactory;

        scenarioExecutor.addScenarioStateListener(new ScenarioStateListener() {

            @Override
            public void scenarioStarted(Scenario scenario) {
            }

            @Override
            public void scenarioStopped(Scenario scenario) {
            }

            @Override
            public void scenarioExecuteWithError(Scenario scenario) {
                new BoardAction(scenario, stationManager).apply(updateScenarioBoardConsumer);
            }

            @Override
            public void scenarioSuccessfullyExecuted(Scenario scenario) {
            }

            @Override
            public void scenarioFinished(Scenario scenario) {
                new BoardAction(scenario, stationManager).apply(removeScenarioBoardConsumer);
            }

            @Override
            public void scenarioQueued(Scenario scenario) {
                new BoardAction(scenario, stationManager).apply(queuedScenarioBoardConsumer);
            }

            @Override
            public void scenarioPaused(Scenario scenario) {
            }
        });
    }

    @Override
    public List<StationBoardEntry> loadArrivalBoard(long stationId) {
        return getStationBoard(stationId).getArrivalEntries();
    }

    @Override
    public List<StationBoardEntry> loadDepartureBoard(long stationId) {
        return getStationBoard(stationId).getDepartureEntries();
    }

    private StationBoard getStationBoard(Long stationId) {
        return stationBoardFactory.getStationBoard(stationId);
    }

}
