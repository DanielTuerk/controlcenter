package net.wbz.moba.controlcenter.web.server.web.station;



import java.util.function.Consumer;
import net.wbz.moba.controlcenter.shared.station.Station;

/**
 * Consumer to update the entries of a {@link StationBoard} for a specific {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}.
 *
 * @author Daniel Tuerk
 */
@Singleton
class UpdateScenarioBoardConsumer implements Consumer<BoardAction> {

    private static final String CANCELED_INFORMATION = "CANCELLED";
    private final StationBoardFactory stationBoardFactory;

    @Inject
    UpdateScenarioBoardConsumer(StationBoardFactory stationBoardFactory) {
        this.stationBoardFactory = stationBoardFactory;
    }

    @Override
    public void accept(BoardAction boardAction) {
        getStationBoard(boardAction.getStationStart())
            .updateDeparture(boardAction.getScenario(), CANCELED_INFORMATION);
        getStationBoard(boardAction.getStationEnd())
            .updateArrival(boardAction.getScenario(), CANCELED_INFORMATION);
    }

    private StationBoard getStationBoard(Station stationEnd) {
        return stationBoardFactory.getStationBoard(stationEnd.getId());
    }
}
