package net.wbz.moba.controlcenter.web.client.request;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.VoidAsyncCallback;
import net.wbz.moba.controlcenter.web.shared.bus.BusService;
import net.wbz.moba.controlcenter.web.shared.bus.BusServiceAsync;
import net.wbz.moba.controlcenter.web.shared.config.ConfigService;
import net.wbz.moba.controlcenter.web.shared.config.ConfigServiceAsync;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionServiceAsync;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioServiceAsync;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatisticService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatisticServiceAsync;
import net.wbz.moba.controlcenter.web.shared.station.StationEditorService;
import net.wbz.moba.controlcenter.web.shared.station.StationEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.station.StationsBoardService;
import net.wbz.moba.controlcenter.web.shared.station.StationsBoardServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.train.TrainServiceAsync;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerServiceAsync;

/**
 * Utils to access the services.
 *
 * @author Daniel Tuerk
 */
public class RequestUtils {

    public static final AsyncCallback<Void> VOID_ASYNC_CALLBACK = new VoidAsyncCallback();
    private final static RequestUtils INSTANCE = new RequestUtils();
    private final TrackEditorServiceAsync trackEditorRequest = GWT.create(TrackEditorService.class);
    private final TrackViewerServiceAsync trackViewerRequest = GWT.create(TrackViewerService.class);
    private final TrainServiceAsync trainRequest = GWT.create(TrainService.class);
    private final TrainEditorServiceAsync trainEditorRequest = GWT.create(TrainEditorService.class);
    private final BusServiceAsync busRequest = GWT.create(BusService.class);
    private final ConfigServiceAsync configRequest = GWT.create(ConfigService.class);
    private final ConstructionServiceAsync constructionRequest = GWT.create(ConstructionService.class);
    private final ScenarioServiceAsync scenarioService = GWT.create(ScenarioService.class);
    private final ScenarioEditorServiceAsync scenarioEditorService = GWT.create(ScenarioEditorService.class);
    private final ScenarioStatisticServiceAsync scenarioStatisticService = GWT.create(ScenarioStatisticService.class);
    private final StationEditorServiceAsync stationEditorServiceAsync = GWT.create(StationEditorService.class);
    private final StationsBoardServiceAsync stationsBoardServiceAsync = GWT.create(StationsBoardService.class);

    private RequestUtils() {
    }

    public static RequestUtils getInstance() {
        return INSTANCE;
    }

    public TrackViewerServiceAsync getTrackViewerService() {
        return trackViewerRequest;
    }

    public TrackEditorServiceAsync getTrackEditorService() {
        return trackEditorRequest;
    }

    public TrainServiceAsync getTrainService() {
        return trainRequest;
    }

    public TrainEditorServiceAsync getTrainEditorService() {
        return trainEditorRequest;
    }

    public BusServiceAsync getBusService() {
        return busRequest;
    }

    public ConfigServiceAsync getConfigService() {
        return configRequest;
    }

    public ConstructionServiceAsync getConstructionService() {
        return constructionRequest;
    }

    public ScenarioServiceAsync getScenarioService() {
        return scenarioService;
    }

    public ScenarioEditorServiceAsync getScenarioEditorService() {
        return scenarioEditorService;
    }

    public ScenarioStatisticServiceAsync getScenarioStatisticService() {
        return scenarioStatisticService;
    }

    public StationsBoardServiceAsync getStationsBoardService() {
        return stationsBoardServiceAsync;
    }

    public StationEditorServiceAsync getStationEditorService() {
        return stationEditorServiceAsync;
    }
}
