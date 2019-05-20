package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioHistoryEntity;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatistic;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatisticService;

/**
 * Implementation of {@link ScenarioStatisticService}.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioStatisticServiceImpl extends RemoteServiceServlet implements ScenarioStatisticService {

    private final ScenarioManager scenarioManager;
    private final ScenarioHistoryDao scenarioHistoryDao;

    @Inject
    public ScenarioStatisticServiceImpl(ScenarioManager scenarioManager,
        ScenarioHistoryDao scenarioHistoryDao) {
        this.scenarioManager = scenarioManager;
        this.scenarioHistoryDao = scenarioHistoryDao;
    }

    @Override
    public ScenarioStatistic load(long scenarioId) {
        ScenarioStatistic scenarioStatistic = new ScenarioStatistic();
        scenarioStatistic.setScenario(scenarioManager.getScenarioById(scenarioId));

        Optional<ScenarioStatistic> scenarioHistoryEntities = scenarioHistoryDao.listByScenario(scenarioId);
        return scenarioHistoryEntities.orElse(null);
    }

    @Override
    public Collection<ScenarioStatistic> loadAll() {
        return scenarioHistoryDao.loadAll();
    }

}
