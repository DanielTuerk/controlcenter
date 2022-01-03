package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Remove the scenario after scheduled delay.
 *
 * @author Daniel Tuerk
 */
@Singleton
class DelayedRemoveScenarioBoardConsumer extends RemoveScenarioBoardConsumer {

    private static final int DELAY_IN_SECONDS = 60;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Inject
    DelayedRemoveScenarioBoardConsumer(StationBoardFactory stationBoardFactory) {
        super(stationBoardFactory);
    }

    @Override
    public void accept(BoardAction boardAction) {
        scheduler.schedule(() -> super.accept(boardAction), DELAY_IN_SECONDS, TimeUnit.SECONDS);
    }

}
