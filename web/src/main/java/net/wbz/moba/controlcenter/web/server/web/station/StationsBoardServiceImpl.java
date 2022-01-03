package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.server.web.scenario.DefaultScenarioStateListener;
import net.wbz.moba.controlcenter.web.server.web.scenario.execution.ScenarioExecutor;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatisticService;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import net.wbz.moba.controlcenter.web.shared.station.StationBoardEntry;
import net.wbz.moba.controlcenter.web.shared.station.StationPlatform;
import net.wbz.moba.controlcenter.web.shared.station.StationsBoardService;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

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
        DelayedRemoveScenarioBoardConsumer delayedRemoveScenarioBoardConsumer,
        UpdateScenarioBoardConsumer updateScenarioBoardConsumer) {
        this.stationBoardFactory = stationBoardFactory;

        scenarioExecutor.addScenarioStateListener(new DefaultScenarioStateListener() {

            @Override
            public void scenarioExecuteWithError(Scenario scenario) {
                //show info
                createBoardAction(scenario, stationManager).ifPresent(
                    boardAction -> boardAction.apply(updateScenarioBoardConsumer));
                // delete delayed
                removeBoardEntry(scenario, stationManager, delayedRemoveScenarioBoardConsumer);
            }

            @Override
            public void scenarioStopped(Scenario scenario) {
                removeBoardEntry(scenario, stationManager, removeScenarioBoardConsumer);
            }

            @Override
            public void scenarioQueued(Scenario scenario) {
                createBoardAction(scenario, stationManager).ifPresent(
                    boardAction -> boardAction.apply(queuedScenarioBoardConsumer));
            }
        });
    }

    private void removeBoardEntry(Scenario scenario, StationManager stationManager,
        RemoveScenarioBoardConsumer removeScenarioBoardConsumer) {
        createBoardAction(scenario, stationManager).ifPresent(
            boardAction -> boardAction.apply(removeScenarioBoardConsumer));
    }

    private Optional<BoardAction> createBoardAction(Scenario scenario, StationManager stationManager) {
        List<Station> stations = stationManager.getStations();

        List<StationPlatform> stationPlatforms = stations.stream()
            .flatMap((Station stationPlatform) -> stationPlatform.getPlatforms().stream()).collect(
                Collectors.toList());

        int minPosition = getMinPositionOfRouteSequences(scenario);
        int maxPosition = getMaxPositionOfRouteSequences(scenario);

        Optional<StationPlatform> startStationPlatform = Optional.empty();
        Optional<StationPlatform> endStationPlatform = Optional.empty();
        Set<Station> viaStations = new HashSet<>();

        for (RouteSequence routeSequence : scenario.getRouteSequences()) {
            Collection<TrackBlock> startTrackBlocks = routeSequence.getRoute().getStart().getAllTrackBlocks();
            Optional<StationPlatform> stationPlatformOfStartTrackBlocks = getStationPlatformForMatchingTrackBlocks(
                stationPlatforms,
                startTrackBlocks);
            if (stationPlatformOfStartTrackBlocks.isPresent()) {
                if (routeSequence.getPosition() == minPosition) {
                    startStationPlatform = stationPlatformOfStartTrackBlocks;
                } else {
                    viaStations
                        .add(stationManager.getStationOfPlatform(stationPlatformOfStartTrackBlocks.get().getId()));
                }
            }

            TrackBlock endTrackBlock = routeSequence.getRoute().getEnd();
            Optional<StationPlatform> stationPlatformOfEndTrackBlock = getStationPlatformForMatchingTrackBlock(
                stationPlatforms,
                endTrackBlock);
            if (stationPlatformOfEndTrackBlock.isPresent()) {
                if (routeSequence.getPosition() == maxPosition) {
                    endStationPlatform = stationPlatformOfEndTrackBlock;
                } else {
                    viaStations.add(stationManager.getStationOfPlatform(stationPlatformOfEndTrackBlock.get().getId()));
                }
            }


        }
        if (startStationPlatform.isPresent() && endStationPlatform.isPresent()) {
            return Optional
                .of(new BoardAction(scenario,
                    stationManager.getStationOfPlatform(startStationPlatform.get().getId()),
                    startStationPlatform.get(),
                    stationManager.getStationOfPlatform(endStationPlatform.get().getId()),
                    endStationPlatform.get(),
                    viaStations));
        } else {
            return Optional.empty();
        }
    }

    private Integer getMaxPositionOfRouteSequences(Scenario scenario) {
        return scenario.getRouteSequences()
            .stream()
            .map(RouteSequence::getPosition)
            .max(Comparator.comparingInt(o -> o))
            .orElse(Integer.MAX_VALUE);
    }

    private Integer getMinPositionOfRouteSequences(Scenario scenario) {
        return scenario.getRouteSequences()
            .stream()
            .map(RouteSequence::getPosition)
            .min(Comparator.comparingInt(o -> o))
            .orElse(Integer.MIN_VALUE);
    }

    @Override
    public List<StationBoardEntry> loadArrivalBoard(long stationId) {
        return getStationBoard(stationId).getArrivalEntries();
    }

    @Override
    public List<StationBoardEntry> loadDepartureBoard(long stationId) {
        return getStationBoard(stationId).getDepartureEntries();
    }

    private Optional<StationPlatform> getStationPlatformForMatchingTrackBlocks(List<StationPlatform> stationPlatforms,
        Collection<TrackBlock> trackBlocks) {
        for (StationPlatform stationPlatform : stationPlatforms) {
            if (!Collections.disjoint(trackBlocks, stationPlatform.getTrackBlocks())) {
                // same track block found in route and station platform
                return Optional.of(stationPlatform);
            }
        }
        return Optional.empty();
    }

    private Optional<StationPlatform> getStationPlatformForMatchingTrackBlock(List<StationPlatform> stationPlatforms,
        TrackBlock trackBlock) {
        for (StationPlatform stationPlatform : stationPlatforms) {
            if (stationPlatform.getTrackBlocks().contains(trackBlock)) {
                return Optional.of(stationPlatform);
            }
        }
        return Optional.empty();
    }

    private StationBoard getStationBoard(Long stationId) {
        return stationBoardFactory.getStationBoard(stationId);
    }

}
