package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.function.Consumer;

/**
 * @author Daniel Tuerk
 */
@Singleton
class RemoveScenarioBoardConsumer implements Consumer<BoardAction> {

    private final StationBoardFactory stationBoardFactory;

    @Inject
    RemoveScenarioBoardConsumer(StationBoardFactory stationBoardFactory) {
        this.stationBoardFactory = stationBoardFactory;
    }

    @Override
    public void accept(BoardAction boardAction) {
        stationBoardFactory.getStationBoard(boardAction.getStationStart().getId())
            .removeDeparture(boardAction.getScenario());
        stationBoardFactory.getStationBoard(boardAction.getStationEnd().getId())
            .removeArrival(boardAction.getScenario());
    }

}
