package net.wbz.moba.controlcenter.web.server.web.scenario.execution;

import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.selectrix4java.block.FeedbackBlockListener;

/**
 * Implementation of {@link FeedbackBlockListener} for the end block of the {@link Route}.
 * If the train enter the end block, the listener will call the {@link #trainEnterRouteEnd()}.
 * 
 * @author Daniel Tuerk
 */
abstract class RouteEndFeedbackBlockListener implements FeedbackBlockListener {

    private final Route route;
    private final Train train;

    RouteEndFeedbackBlockListener(Route route, Train train) {
        this.route = route;
        this.train = train;
    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        if (trainAddress == train.getAddress() && blockNumber == route.getEnd()
                .getBlockFunction().getBit()) {
            trainEnterRouteEnd();
        }
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {

    }

    @Override
    public void blockOccupied(int blockNr) {

    }

    @Override
    public void blockFreed(int blockNr) {

    }

    /**
     * Train reach the end block of the route.
     */
    protected abstract void trainEnterRouteEnd();

}
