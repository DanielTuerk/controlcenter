package net.wbz.moba.controlcenter.web.server.web.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlock;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlockPart;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario.RUN_STATE;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioService;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioServiceImpl extends RemoteServiceServlet implements ScenarioService {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioServiceImpl.class);

    /**
     * Service to toggle track parts of the track in the scenarios.
     */
    private final TrackViewerServiceImpl trackViewerRequest;
    /**
     * Manager for the scenario data.
     */
    private final ScenarioManager scenarioManager;
    /**
     * Broadcaster for client side event handling of state changes.
     */
    private final EventBroadcaster eventBroadcaster;
    /**
     * Server side listeners for state changes.
     */
    private final List<ScenarioStateListener> listeners = new ArrayList<>();

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public ScenarioServiceImpl(TrackViewerServiceImpl trackViewerService, ScenarioManager scenarioManager,
            EventBroadcaster eventBroadcaster) {
        this.trackViewerRequest = trackViewerService;
        this.scenarioManager = scenarioManager;

        this.eventBroadcaster = eventBroadcaster;
    }

    public void addScenarioStateListener(ScenarioStateListener listener) {
        listeners.add(listener);
    }

    public void removeScenarioStateListener(ScenarioStateListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void start(long scenarioId) {
        // TODO
        throw new NotImplementedException();

        // eventBroadcaster.fireEvent(new ScenarioStateEvent());
    }

    @Override
    public void stop(long scenarioId) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public void pause(long scenarioId) {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Update the track for the given {@link Scenario}.
     * Fetch the next block of the route after the given {@link Signal}. The {@link Signal} is the start point on the
     * next {@link RouteBlock}. Toggle all {@link net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart}
     * for that {@link RouteBlock}.
     * 
     * @param scenario running {@link Scenario}
     * @param signal current {@link Signal}
     */
    public void updateTrack(Scenario scenario, Signal signal) {
        LOG.debug("update the track for scenario {} in signal: ", scenario, signal);
        Optional<RouteBlock> routeBlockOptional = scenario.getRouteBlockForStartSignal(signal);
        if (routeBlockOptional.isPresent()) {
            RouteBlock routeBlock = routeBlockOptional.get();
            for (RouteBlockPart routeBlockPart : routeBlock.getRouteBlockParts()) {
                trackViewerRequest.toggleTrackPart(routeBlockPart.getSwitchTrackPart().getToggleFunction(),
                        routeBlockPart.isState());
            }
        }
    }

    /**
     * Get scenarios for the given train.
     *
     * @param train {@link Train}
     * @return {@link Scenario}s
     */
    public Iterable<Scenario> getScenariosOfTrain(final Train train) {
        return Iterables.filter(scenarioManager.getScenarios(), new Predicate<Scenario>() {
            @Override
            public boolean apply(Scenario input) {
                return input.getTrain().equals(train);
            }
        });
    }

    /**
     * Get the running scenario for the given train.
     *
     * @param train {@link Train}
     * @return {@link Optional} for {@link Scenario}
     */
    public Optional<Scenario> getRunningScenarioOfTrain(Train train) {
        for (Scenario scenario : getScenariosOfTrain(train)) {
            if (scenario.getRunState() == RUN_STATE.RUNNING) {
                return Optional.of(scenario);
            }
        }
        return Optional.absent();
    }
}
