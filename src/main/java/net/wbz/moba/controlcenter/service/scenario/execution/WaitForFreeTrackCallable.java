package net.wbz.moba.controlcenter.service.scenario.execution;

import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.Scenario.RUN_STATE;

/**
 * Wait for a free track for the {@link RouteExecution} of the {@link Scenario}.
 *
 * @author Daniel Tuerk
 */
@Slf4j
class WaitForFreeTrackCallable implements Runnable {

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
        log.info("train request free track to start: {}", route.getName());

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
                    log.error("interrupted wait free track of route: {}", route.getName(), e);
                }
            }
        }
    }

}
