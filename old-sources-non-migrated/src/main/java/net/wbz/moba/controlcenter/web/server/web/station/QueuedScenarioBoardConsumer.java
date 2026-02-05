package net.wbz.moba.controlcenter.web.server.web.station;



import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.shared.station.Station;

/**
 * @author Daniel Tuerk
 */
@Singleton
class QueuedScenarioBoardConsumer implements Consumer<BoardAction> {

    private final StationBoardFactory stationBoardFactory;

    @Inject
    QueuedScenarioBoardConsumer(StationBoardFactory stationBoardFactory) {
        this.stationBoardFactory = stationBoardFactory;
    }

    @Override
    public void accept(BoardAction boardAction) {
        List<String> viaStations = boardAction.getViaStations().stream().map(Station::getName)
            .collect(Collectors.toList());
        stationBoardFactory.getStationBoard(boardAction.getStationStart().getId())
            .addDeparture(boardAction.getScenario(), boardAction.getStationEnd(),
                boardAction.getStationPlatformStart(), viaStations);
        stationBoardFactory.getStationBoard(boardAction.getStationEnd().getId())
            .addArrival(boardAction.getScenario(), boardAction.getStationStart(),
                boardAction.getStationPlatformEnd(), viaStations);
    }
}
