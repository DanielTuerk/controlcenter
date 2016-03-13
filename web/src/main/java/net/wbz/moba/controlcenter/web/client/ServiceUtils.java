package net.wbz.moba.controlcenter.web.client;

import net.wbz.moba.controlcenter.web.shared.FoobarRequestFactory;
import net.wbz.moba.controlcenter.web.shared.bus.BusRequest;
import net.wbz.moba.controlcenter.web.shared.config.ConfigRequest;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionRequest;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.train.TrainServiceAsync;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * @author Daniel Tuerk
 */
public class ServiceUtils {

    private static final ScenarioEditorServiceAsync scenarioEditorService = GWT.create(ScenarioEditorService.class);

    private static final TrackViewerServiceAsync trackViewerService = GWT.create(TrackViewerService.class);
    private static final TrackEditorServiceAsync trackEditorService = GWT.create(TrackEditorService.class);

    private static final ScenarioServiceAsync scenarioService = GWT.create(ScenarioService.class);

    private static final TrainEditorServiceAsync trainEditorService = GWT.create(TrainEditorService.class);
    private static final TrainServiceAsync trainService = GWT.create(TrainService.class);

    private static ServiceUtils instance;
    private final FoobarRequestFactory requestFactory;

    private ServiceUtils() {
        final EventBus eventBus = new SimpleEventBus();
        requestFactory = GWT.create(FoobarRequestFactory.class);
        requestFactory.initialize(eventBus);
    }

    public static ServiceUtils getInstance() {
        if (instance == null) {
            instance = new ServiceUtils();
        }
        return instance;
    }

    public static TrackEditorServiceAsync getTrackEditorService() {
        return trackEditorService;
    }

    public static TrackViewerServiceAsync getTrackViewerService() {
        return trackViewerService;
    }

    public static ScenarioEditorServiceAsync getScenarioEditorService() {
        return scenarioEditorService;
    }

    public static ScenarioServiceAsync getScenarioService() {
        return scenarioService;
    }

    public static TrainEditorServiceAsync getTrainEditorService() {
        return trainEditorService;
    }

    public static TrainServiceAsync getTrainService() {
        return trainService;
    }

    public BusRequest getBusService() {
        return requestFactory.busRequest();
    }

    public ConfigRequest getConfigService() {
        return requestFactory.configRequest();
    }

    public ConstructionRequest getConstructionService() {
        // TODO rename
        return requestFactory.constructionRequest();
    }
}
