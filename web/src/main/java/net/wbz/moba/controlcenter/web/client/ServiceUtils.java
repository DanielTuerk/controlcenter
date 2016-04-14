package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import net.wbz.moba.controlcenter.web.shared.FoobarRequestFactory;
import net.wbz.moba.controlcenter.web.shared.bus.BusRequest;
import net.wbz.moba.controlcenter.web.shared.config.ConfigRequest;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionRequest;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorRequest;
import net.wbz.moba.controlcenter.web.shared.train.TrainRequest;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerRequest;

/**
 * @author Daniel Tuerk
 */
public class ServiceUtils {

    // private static final ScenarioEditorServiceAsync scenarioEditorService = GWT.create(ScenarioEditorService.class);

    // private static final TrackViewerServiceAsync trackViewerService = GWT.create(TrackViewerRequest.class);
    // private static final TrackEditorRequestAsync trackEditorService = GWT.create(TrackEditorRequest.class);

    // private static final ScenarioServiceAsync scenarioService = GWT.create(ScenarioService.class);

    private static ServiceUtils instance;
    private final FoobarRequestFactory requestFactory;
    private TrackEditorRequest trackEditorRequest;

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

    public static ScenarioEditorServiceAsync getScenarioEditorService() {
        return null;
    }

    public static ScenarioServiceAsync getScenarioService() {
        return null;
    }

    public TrackEditorRequest getTrackEditorService() {
        if (trackEditorRequest == null) {
            trackEditorRequest = requestFactory.trackEditorRequest();
        }
        return trackEditorRequest;
    }

    public TrackViewerRequest getTrackViewerService() {
        return requestFactory.trackViewerRequest();
    }

    public TrainEditorRequest getTrainEditorService() {
        return requestFactory.trainEditorRequest();
    }

    public TrainRequest getTrainService() {
        return requestFactory.trainRequest();
    }

    public BusRequest getBusService() {
        return requestFactory.busRequest();
    }

    public ConfigRequest getConfigService() {
        return requestFactory.configRequest();
    }

    public ConstructionRequest getConstructionService() {
        return requestFactory.constructionRequest();
    }
}
