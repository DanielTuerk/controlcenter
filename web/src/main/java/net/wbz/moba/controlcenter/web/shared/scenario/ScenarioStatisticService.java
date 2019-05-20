package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * Service for the statistics of the {@link Scenario}s.
 *
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_SCENARIO_STATISTIC)
public interface ScenarioStatisticService extends RemoteService {

   ScenarioStatistic load(long scenarioId);
   Collection<ScenarioStatistic> loadAll();

}
