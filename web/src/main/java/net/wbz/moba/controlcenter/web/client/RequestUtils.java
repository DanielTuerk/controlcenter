package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import net.wbz.moba.controlcenter.web.shared.FoobarRequestFactory;
import net.wbz.moba.controlcenter.web.shared.bus.BusRequest;
import net.wbz.moba.controlcenter.web.shared.config.ConfigRequest;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionRequest;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorRequest;
import net.wbz.moba.controlcenter.web.shared.train.TrainRequest;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerRequest;

/**
 * Utils to access the requests from the {@link com.google.web.bindery.requestfactory.shared.RequestFactory}.
 *
 * @author Daniel Tuerk
 */
public class RequestUtils {

    private final static RequestUtils INSTANCE = new RequestUtils();
    private final FoobarRequestFactory requestFactory;

    private TrackViewerRequest trackViewerRequest;
    private TrackEditorRequest trackEditorRequest;
    private TrainRequest trainRequest;
    private TrainEditorRequest trainEditorRequest;
    private BusRequest busRequest;
    private ConfigRequest configRequest;
    private ConstructionRequest constructionRequest;

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

    public TrackViewerRequest getTrackViewerRequest() {
//        if (trackViewerRequest == null) {
//            trackViewerRequest = requestFactory.trackViewerRequest();
//        }
//        return trackViewerRequest;
        return requestFactory.trackViewerRequest();
    }

    public TrackEditorRequest getTrackEditorRequest() {
//        if (trackEditorRequest == null) {
//            trackEditorRequest = requestFactory.trackEditorRequest();
//        }
//        return trackEditorRequest;
        return requestFactory.trackEditorRequest();
    }

    public TrainRequest getTrainRequest() {
//        if (trainRequest == null) {
//            trainRequest = requestFactory.trainRequest();
//        }
//        return trainRequest;
        return requestFactory.trainRequest();
    }

    public TrainEditorRequest getTrainEditorRequest() {
//        if (trainEditorRequest == null) {
//            trainEditorRequest = requestFactory.trainEditorRequest();
//        }
//        return trainEditorRequest;
        return requestFactory.trainEditorRequest();
    }

    public BusRequest getBusRequest() {
//        if (busRequest == null) {
//            busRequest = requestFactory.busRequest();
//        }
//        return busRequest;

        return requestFactory.busRequest();
    }

    public ConfigRequest getConfigRequest() {
//        if (configRequest == null) {
//            configRequest = requestFactory.configRequest();
//        }
//        return configRequest;
        return requestFactory.configRequest();
    }

    public ConstructionRequest getConstructionRequest() {
//        if (constructionRequest == null) {
//            constructionRequest = requestFactory.constructionRequest();
//        }
//        return constructionRequest;
        return requestFactory.constructionRequest();
    }
}
