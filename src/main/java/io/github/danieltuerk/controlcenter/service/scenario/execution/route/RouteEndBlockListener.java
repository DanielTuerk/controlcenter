package io.github.danieltuerk.controlcenter.service.scenario.execution.route;

import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.selectrix4java.block.BlockListener;
import io.github.danieltuerk.selectrix4java.block.FeedbackBlockListener;

/**
 * Implementation of {@link FeedbackBlockListener} for the end block of the {@link Route}.
 * If the train enters the end block, the listener will call the {@link #trainEnterRouteEnd()}.
 * There could be any train to trigger the occupied state but the block is reserved for the train of the
 * {@link Scenario} and there should be no other one during the
 * execution.
 * The {@link FeedbackBlockListener} isn't used because the information is received too late by the delay to stop the
 * train in the end block.
 * 
 * @author Daniel Tuerk
 */
abstract class RouteEndBlockListener implements BlockListener {

    private final Route route;

    RouteEndBlockListener(Route route) {
        this.route = route;
    }

    @Override
    public void blockOccupied(int blockNr) {
        // asap the block is occupied stop the train because the feedback address is too slow
        if (blockNr == route.getEnd().getBlockFunction().getBit()) {
            trainEnterRouteEnd();
        }
    }

    @Override
    public void blockFreed(int blockNr) {
    }

    /**
     * The train reached the end block of the route.
     */
    protected abstract void trainEnterRouteEnd();

}
