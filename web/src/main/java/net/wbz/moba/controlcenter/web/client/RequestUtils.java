package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.wbz.moba.controlcenter.web.client.Callbacks.VoidAsyncCallback;
import net.wbz.moba.controlcenter.web.shared.bus.BusService;
import net.wbz.moba.controlcenter.web.shared.bus.BusServiceAsync;
import net.wbz.moba.controlcenter.web.shared.config.ConfigService;
import net.wbz.moba.controlcenter.web.shared.config.ConfigServiceAsync;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionServiceAsync;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.train.TrainServiceAsync;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerServiceAsync;

/**
 * Utils to access the requests from the {@link com.google.web.bindery.requestfactory.shared.RequestFactory}.
 *
 * @author Daniel Tuerk
 */
public class RequestUtils {

    public static final AsyncCallback<Void> VOID_ASYNC_CALLBACK = new VoidAsyncCallback();
    private final static RequestUtils INSTANCE = new RequestUtils();
    private TrackEditorServiceAsync trackEditorRequest = GWT.create(TrackEditorService.class);
    private TrackViewerServiceAsync trackViewerRequest = GWT.create(TrackViewerService.class);
    private TrainServiceAsync trainRequest = GWT.create(TrainService.class);
    private TrainEditorServiceAsync trainEditorRequest = GWT.create(TrainEditorService.class);
    private BusServiceAsync busRequest = GWT.create(BusService.class);
    private ConfigServiceAsync configRequest = GWT.create(ConfigService.class);
    private ConstructionServiceAsync constructionRequest = GWT.create(ConstructionService.class);

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
}
