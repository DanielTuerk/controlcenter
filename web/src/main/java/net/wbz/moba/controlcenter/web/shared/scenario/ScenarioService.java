package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * Service to control the {@link Scenario}s.
 * A {@link Scenario} can be started and stopped manually or scheduled by cron job.
 *
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_SCENARIO)
public interface ScenarioService extends RemoteService {

    /**
     * Immediately start the {@link Scenario} for the given id.
     *
     * @param scenarioId id of {@link Scenario} to start
     */
    void start(long scenarioId);

    /**
     * Schedule {@link Scenario} by defined cron for the given id.
     *
     * @param scenarioId id of {@link Scenario} to schedule
     */
    void schedule(long scenarioId);

    /**
     * Immediately stop the {@link Scenario} for the given id.
     *
     * @param scenarioId id of {@link Scenario} to stop
     */
    void stop(long scenarioId);

}
