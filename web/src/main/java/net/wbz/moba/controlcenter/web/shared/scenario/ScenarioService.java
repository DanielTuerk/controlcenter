package net.wbz.moba.controlcenter.web.shared.scenario;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public interface ScenarioService {

    public void start(long scenarioId);

    public void stop(long scenarioId);

    public void pause(long scenarioId);

}
