package net.wbz.moba.controlcenter.web.server.scenario;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioServiceImpl extends RemoteServiceServlet implements ScenarioService {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioServiceImpl.class);

    private final TrackViewerServiceImpl trackViewerRequest;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final ScenarioManager scenarioManager;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public ScenarioServiceImpl(TrackViewerServiceImpl trackViewerService, ScenarioManager scenarioManager,
            EventBroadcaster eventBroadcaster) {
        this.trackViewerRequest = trackViewerService;
        this.scenarioManager = scenarioManager;

        this.eventBroadcaster = eventBroadcaster;
    }

    @Override
    public void start(long scenarioId) {
        // final Scenario scenario = scenarioManager.getScenarioById(scenarioId);
        // if (scenario.getRunState() != Scenario.RUN_STATE.RUNNING) { // TODO multiple ok -> check by modification for
        // // conflicts
        // FutureTask<Boolean> scenarioRunTask = new FutureTask<Boolean>(new ScenarioRunCallable(scenario,
        // trackViewerRequest, eventBroadcaster));
        // executor.execute(scenarioRunTask);
        // } else if (scenario.getRunState() == Scenario.RUN_STATE.PAUSED) {
        //
        // } else {
        // LOG.error(String.format("can't start %d (%s)", scenarioId, scenario.getRunState()));
        // }
        eventBroadcaster.fireEvent(new ScenarioStateEvent());
    }

    @Override
    public void stop(long scenarioId) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pause(long scenarioId) {
        // To change body of implemented methods use File | Settings | File Templates.
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
