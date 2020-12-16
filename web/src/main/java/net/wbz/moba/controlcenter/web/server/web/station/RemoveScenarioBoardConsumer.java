package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Daniel Tuerk
 */
@Singleton
class RemoveScenarioBoardConsumer implements Consumer<BoardAction> {

    private static final int DELAY_IN_MINUTES = 2;
    private final StationBoardFactory stationBoardFactory;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Inject
    RemoveScenarioBoardConsumer(StationBoardFactory stationBoardFactory) {
        this.stationBoardFactory = stationBoardFactory;
    }

    @Override
    public void accept(BoardAction boardAction) {
        scheduler.schedule(() -> {
            stationBoardFactory.getStationBoard(boardAction.getStationStart().getId())
                .removeDeparture(boardAction.getScenario());
            stationBoardFactory.getStationBoard(boardAction.getStationEnd().getId())
                .removeArrival(boardAction.getScenario());
        }, DELAY_IN_MINUTES, TimeUnit.MINUTES);
    }

}
