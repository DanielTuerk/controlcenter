package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.wbz.moba.controlcenter.web.shared.station.Station;

/**
 * Consumer to update the entries of a {@link StationBoard} for a specific {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}.
 *
 * @author Daniel Tuerk
 */
@Singleton
class UpdateScenarioBoardConsumer implements Consumer<BoardAction> {

    private static final String CANCELED_INFORMATION = "CANCELLED";
    private final StationBoardFactory stationBoardFactory;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Inject
    UpdateScenarioBoardConsumer(StationBoardFactory stationBoardFactory) {
        this.stationBoardFactory = stationBoardFactory;
    }

    @Override
    public void accept(BoardAction boardAction) {
        updateBoardEntriesInformation(boardAction);
        removeBoardEntriesAfterDelay(boardAction);
    }

    private void updateBoardEntriesInformation(BoardAction boardAction) {
        getStationBoard(boardAction.getStationStart())
            .updateDeparture(boardAction.getScenario(), CANCELED_INFORMATION);
        getStationBoard(boardAction.getStationEnd())
            .updateArrival(boardAction.getScenario(), CANCELED_INFORMATION);
    }

    private void removeBoardEntriesAfterDelay(BoardAction boardAction) {
        scheduler.schedule(() -> {
            getStationBoard(boardAction.getStationStart())
                .removeDeparture(boardAction.getScenario());
            getStationBoard(boardAction.getStationEnd())
                .removeArrival(boardAction.getScenario());
        }, 1, TimeUnit.MINUTES);
    }

    private StationBoard getStationBoard(Station stationEnd) {
        return stationBoardFactory.getStationBoard(stationEnd.getId());
    }
}
