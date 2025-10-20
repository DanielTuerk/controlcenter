package net.wbz.moba.controlcenter.service.scenario.execution;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.service.scenario.RouteListener;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.RouteStateEvent;
import net.wbz.moba.controlcenter.shared.scenario.RouteStateEvent.STATE;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;

/**
 * Implementation of the {@link RouteListener} to fire {@link RouteStateEvent}s by {@link EventBroadcaster}.
 * 
 * @author Daniel Tuerk
 */
@ApplicationScoped
class ScenarioRouteEventBroadcaster implements RouteListener {

    /**
     * Broadcaster for client side event handling of state changes.
     */
    private final EventBroadcaster eventBroadcaster;

    @Inject
    ScenarioRouteEventBroadcaster(EventBroadcaster eventBroadcaster) {
        this.eventBroadcaster = eventBroadcaster;
    }

    @Override
    public void routeStarted(Scenario scenario, RouteSequence routeSequence) {
        fireEvent(scenario, routeSequence, STATE.RUNNING);
    }

    @Override
    public void routeFinished(Scenario scenario, RouteSequence routeSequence) {
        fireEvent(scenario, routeSequence, STATE.FINISHED);
    }

    @Override
    public void routeWaitingToStart(Scenario scenario, RouteSequence routeSequence) {
        fireEvent(scenario, routeSequence, STATE.WAITING);
    }

    @Override
    public void routeFailed(Scenario scenario, RouteSequence routeSequence, String msg) {
        fireEvent(scenario, routeSequence, STATE.FAILED, msg);
    }

    private void fireEvent(Scenario scenario, RouteSequence routeSequence, STATE state) {
        fireEvent(scenario, routeSequence, state, null);
    }

    private void fireEvent(Scenario scenario, RouteSequence routeSequence, STATE state, String message) {
        eventBroadcaster.fireEvent(new RouteStateEvent(scenario.getId(), routeSequence.getId(), state, message));
    }
}
