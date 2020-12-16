package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.function.Consumer;

/**
 * @author Daniel Tuerk
 */
@Singleton
class QueuedScenarioBoardConsumer implements Consumer<BoardAction> {

    private final StationBoardFactory stationBoardFactory;
    private final StationManager stationManager;

    @Inject
    QueuedScenarioBoardConsumer(StationBoardFactory stationBoardFactory,
        StationManager stationManager) {
        this.stationBoardFactory = stationBoardFactory;
        this.stationManager = stationManager;
    }

    @Override
    public void accept(BoardAction boardAction) {
        stationBoardFactory.getStationBoard(boardAction.getStationStart().getId())
            .addDeparture(boardAction.getScenario(), boardAction.getStationEnd(),
                stationManager.findStationPlatform(boardAction.getStationPlatformStart()));
        stationBoardFactory.getStationBoard(boardAction.getStationEnd().getId())
            .addArrival(boardAction.getScenario(), boardAction.getStationStart(),
                stationManager.findStationPlatform(boardAction.getStationPlatformEnd()));
    }
}
