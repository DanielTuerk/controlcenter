package net.wbz.moba.controlcenter.web.server.scenario;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioCommand;
import org.apache.commons.lang.NotImplementedException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manipulation for the {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}
 * in an {@link net.wbz.moba.controlcenter.web.shared.constrution.Construction}.
 * <p/>
 * {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}s are stored in an own database named by the
 * corresponding construction. Access and manipulation of each {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}
 * are from persistence.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioManager {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioManager.class);
    private final List<Scenario> scenarios = Lists.newArrayList();

    private final Map<String, Class<? extends ScenarioCommand>> commandMapping = Maps.newHashMap();

    private final ConstructionService constructionService;

    @Inject
    public ScenarioManager(ConstructionService constructionService) {
        this.constructionService = constructionService;

        LOG.debug("Load ScenarioCommand mappings");
        Reflections reflections = new Reflections("net.wbz.moba.controlcenter.web.shared.scenario");
        for (Class<? extends ScenarioCommand> aClass : reflections.getSubTypesOf(ScenarioCommand.class)) {
            // if class is abstract we do nothing
            if (!Modifier.isAbstract(aClass.getModifiers())) {
                String errorMsg = "Can't create instance of " + aClass.getName();
                try {
                    Constructor<? extends ScenarioCommand> constructor = aClass.getConstructor();
                    ScenarioCommand plugin = constructor.newInstance();
                    commandMapping.put(plugin.getCommand(), plugin.getClass());
                } catch (Throwable e) {
                    // catch all possible errors, because a broken plugin should not take effect to start the core components
                    LOG.error(errorMsg, e);
                }
            }
        }
    }

    public List<Scenario> getScenarios() throws Exception {
//        scenarios.clear();
//        scenarios.addAll(databaseFactory.query());
//        return scenarios;
        return new ArrayList<>();
    }

    public Scenario getScenarioById(long scenarioId) {
        for (Scenario scenario : scenarios) {
            if (scenarioId == scenario.getId()) {
                return scenario;
            }
        }
        throw new RuntimeException(String.format("no scenario found for id: %d", scenarioId));
    }

    public List<ScenarioCommand> loadCommandsFromText(String[] commandTexts) {
        List<ScenarioCommand> scenarioCommands = Lists.newArrayList();
        for (String commandText : commandTexts) {
            String[] commandParts = commandText.split("\\(");
            try {
                ScenarioCommand scenarioCommand = commandMapping.get(commandParts[0]).getConstructor().newInstance();
                scenarioCommand.parseParameters(commandParts[1].split("\\)")[0].split(","));
                scenarioCommands.add(scenarioCommand);
            } catch (Exception e) {
                LOG.error("can't create sceanrio command", e);
            }
        }
        return scenarioCommands;
    }

    public void createScenario(Scenario scenario) throws Exception {
        scenario.setId(System.nanoTime());
        storeScenario(scenario);
    }

    private void storeScenario(Scenario scenario) throws Exception {
//        databaseFactory.store(scenario);
    }

    public void deleteDatabase(long scenarioId) {
        throw new NotImplementedException();
// TODO
//   databaseFactory.deleteDatabase(getDatabaseKey(getScenarioById(scenarioId)));
    }

    public void updateScenario(Scenario scenario) throws Exception {
        storeScenario(scenario);
    }

}
