package net.wbz.moba.controlcenter.web.server.web.scenario;

import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * Listener for the state of execution of single {@link RouteSequence} in {@link Scenario}.
 *
 * @author Daniel Tuerk
 */
public interface RouteListener {

    /**
     * Given route of scenario has started.
     *
     * @param scenario {@link Scenario}
     * @param routeSequence {@link RouteSequence}
     */
    void routeStarted(Scenario scenario, RouteSequence routeSequence);

    /**
     * Given route of scenario has finished.
     *
     * @param scenario {@link Scenario}
     * @param routeSequence {@link RouteSequence}
     */
    void routeFinished(Scenario scenario, RouteSequence routeSequence);

    /**
     * Given route of scenario is waiting for clear track to start.
     *
     * @param scenario {@link Scenario}
     * @param routeSequence {@link RouteSequence}
     */
    void routeWaitingToStart(Scenario scenario, RouteSequence routeSequence);

    /**
     * Given route of scenario failed.
     *
     * @param scenario {@link Scenario}
     * @param routeSequence {@link RouteSequence}
     * @param msg {@link String}
     */
    void routeFailed(Scenario scenario, RouteSequence routeSequence, String msg);
}
