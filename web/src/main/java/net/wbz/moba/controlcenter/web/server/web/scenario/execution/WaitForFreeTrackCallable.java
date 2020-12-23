package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import java.util.concurrent.Callable;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wait for a free track for the {@link RouteExecution} of the {@link Scenario}.
 *
 * @author Daniel Tuerk
 */
class WaitForFreeTrackCallable implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(WaitForFreeTrackCallable.class);
    private static final long TIME_TO_WAIT_IN_MILLIS = 500L;

    private final RouteExecution routeExecution;
    private final RouteExecutionObserver routeExecutionObserver;
    private final Scenario scenario;

    WaitForFreeTrackCallable(RouteExecutionObserver routeExecutionObserver,
        Scenario scenario, RouteExecution routeExecution) {
        this.routeExecutionObserver = routeExecutionObserver;
        this.scenario = scenario;
        this.routeExecution = routeExecution;
    }

    @Override
    public void run() {
        final Route route = routeExecution.getRouteSequence().getRoute();
        LOG.info("train ({}) request free track: {}", routeExecution.getTrain(), route);

        routeExecutionObserver.addRunningRouteSequence(routeExecution.getRouteSequence());

        while (scenario.getRunState() != RUN_STATE.STOPPED) {
            if (routeExecutionObserver.checkAndReserveNextRunningRoute(routeExecution.getRouteSequence(),
                routeExecution.getPreviousRouteSequence())) {
                // no dependent route running, stop check
                return ;
            } else {
                // dependency running, wait and recheck
                try {
                    Thread.sleep(TIME_TO_WAIT_IN_MILLIS);
                } catch (InterruptedException e) {
                    //TODO
                    e.printStackTrace();
                }
            }
        }
        //TODO
        return ;
    }

}
