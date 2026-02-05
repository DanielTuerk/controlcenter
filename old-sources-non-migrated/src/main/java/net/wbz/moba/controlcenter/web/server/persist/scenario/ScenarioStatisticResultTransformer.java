package net.wbz.moba.controlcenter.web.server.persist.scenario;



import java.time.LocalDateTime;
import java.util.List;
import net.wbz.moba.controlcenter.web.server.DateUtil;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStatistic;
import org.hibernate.transform.ResultTransformer;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioStatisticResultTransformer implements ResultTransformer {
    private final DataMapper<Scenario, ScenarioEntity> scenarioEntityDataMapper = new DataMapper<>(Scenario.class,
        ScenarioEntity.class);
    private final ScenarioDao scenarioDao;

    @Inject
    public ScenarioStatisticResultTransformer(ScenarioDao scenarioDao) {
        this.scenarioDao = scenarioDao;
    }

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        ScenarioStatistic scenarioStatistic = new ScenarioStatistic();
        scenarioStatistic.setScenario(scenarioEntityDataMapper.transformSource(
            scenarioDao.findById((Long) objects[0])
        ));
        scenarioStatistic.setRuns((Long) objects[1]);
        scenarioStatistic.setLastRun(DateUtil.convertToDate((LocalDateTime) objects[2]));
        scenarioStatistic.setAverageRunTimeInMillis((Double) objects[3]);
        return scenarioStatistic;
    }

    @Override
    public List transformList(List list) {
        return list;
    }
}
