package net.wbz.moba.controlcenter.web.server.scenario;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.wbz.moba.controlcenter.db.DatabaseFactory;
import net.wbz.moba.controlcenter.db.StorageException;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioCommand;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * Manipulation for the {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}
 * in an {@link net.wbz.moba.controlcenter.web.shared.constrution.model.Construction}.
 * <p/>
 * {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}s are stored in an own database named by the
 * corresponding construction. Access and manipulation of each {@link net.wbz.moba.controlcenter.web.shared.scenario.Scenario}
 * are from persistence.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Singleton
public class ScenarioManager {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioManager.class);
    private final List<Scenario> scenarios = Lists.newArrayList();

    private final Map<String, Class<? extends ScenarioCommand>> commandMapping = Maps.newHashMap();

    private final ConstructionServiceImpl constructionService;
    private final DatabaseFactory databaseFactory;

    @Inject
    public ScenarioManager(ConstructionServiceImpl constructionService, @Named("scenario") DatabaseFactory databaseFactory) {
        this.constructionService = constructionService;
        this.databaseFactory = databaseFactory;

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

    private ObjectContainer getStorageObjectContainer() throws StorageException, IOException {
        String constructionName = constructionService.getCurrentConstruction().getName();
        if(!databaseFactory.getExistingDatabaseNames().contains(constructionName)){
            databaseFactory.addDatabase(constructionName);
        }
        return databaseFactory.getStorage(constructionName).getObjectContainer();
    }

    public List<Scenario> getScenarios() throws Exception {
        scenarios.clear();

        ObjectContainer database = getStorageObjectContainer();
        ObjectSet<Scenario> result = database.query(Scenario.class);
        while (result.hasNext()) {
            scenarios.add(result.next());
        }

        return scenarios;
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
        ObjectContainer database = getStorageObjectContainer();
        database.store(scenario);
        database.commit();
    }

    public void deleteDatabase(long scenarioId) {
// TODO
//   databaseFactory.deleteDatabase(getDatabaseKey(getScenarioById(scenarioId)));
    }

    public void updateScenario(Scenario scenario) throws Exception {
        storeScenario(scenario);
    }

}
