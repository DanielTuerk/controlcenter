package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import net.wbz.moba.controlcenter.web.shared.FoobarRequestFactory;
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

    private final static RequestUtils INSTANCE = new RequestUtils();
    private final FoobarRequestFactory requestFactory;

    private TrackEditorServiceAsync trackEditorRequest = GWT.create(TrackEditorService.class);
    private TrackViewerServiceAsync trackViewerRequest = GWT.create(TrackViewerService.class);
    private TrainServiceAsync trainRequest = GWT.create(TrainService.class);
    private TrainEditorServiceAsync trainEditorRequest = GWT.create(TrainEditorService.class);
    private BusServiceAsync busRequest = GWT.create(BusService.class);
    private ConfigServiceAsync configRequest = GWT.create(ConfigService.class);
    private ConstructionServiceAsync constructionRequest = GWT.create(ConstructionService.class);

    /**
     * Initialize event bus for request factory.
     */
    private RequestUtils() {
        final EventBus eventBus = new SimpleEventBus();
        requestFactory = GWT.create(FoobarRequestFactory.class);
        requestFactory.initialize(eventBus);
    }

    public static RequestUtils getInstance() {
        return INSTANCE;
    }

    public TrackViewerServiceAsync getTrackViewerRequest() {
//        if (trackViewerRequest == null) {
//            trackViewerRequest = requestFactory.trackViewerRequest();
//        }
        return trackViewerRequest;
//        return requestFactory.trackViewerRequest();
    }

    public TrackEditorServiceAsync getTrackEditorRequest() {
//        if (trackEditorRequest == null) {
//            trackEditorRequest = requestFactory.trackEditorRequest();
//        }
        return trackEditorRequest;
//        return requestFactory.trackEditorRequest();
    }

    public TrainServiceAsync getTrainRequest() {
//        if (trainRequest == null) {
//            trainRequest = requestFactory.trainRequest();
//        }
        return trainRequest;
//        return requestFactory.trainRequest();
    }

    public TrainEditorServiceAsync getTrainEditorRequest() {
//        if (trainEditorRequest == null) {
//            trainEditorRequest = requestFactory.trainEditorRequest();
//        }
        return trainEditorRequest;
//        return requestFactory.trainEditorRequest();
    }

    public BusServiceAsync getBusRequest() {
//        if (busRequest == null) {
//            busRequest = requestFactory.busRequest();
//        }
        return busRequest;

//        return requestFactory.busRequest();
    }

    public ConfigServiceAsync getConfigRequest() {
//        if (configRequest == null) {
//            configRequest = requestFactory.configRequest();
//        }
        return configRequest;
//        return requestFactory.configRequest();
    }

    public ConstructionServiceAsync getConstructionRequest() {
//        if (constructionRequest == null) {
//            constructionRequest = requestFactory.constructionRequest();
//        }
        return constructionRequest;
//        return requestFactory.constructionRequest();
    }
}
