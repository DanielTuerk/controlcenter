package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
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

    private final static RequestUtils instance = new RequestUtils();
    private final FoobarRequestFactory requestFactory;

    /**
     * Initialize event bus for request factory.
     */
    private RequestUtils() {
        final EventBus eventBus = new SimpleEventBus();
        requestFactory = GWT.create(FoobarRequestFactory.class);
        requestFactory.initialize(eventBus);
    }

    public static RequestUtils getInstance() {
        return instance;
    }

    public TrackEditorRequest getTrackEditorRequest() {
        return requestFactory.trackEditorRequest();
    }

    public TrackViewerRequest getTrackViewerRequest() {
        return requestFactory.trackViewerRequest();
    }

    public TrainEditorRequest getTrainEditorRequest() {
        return requestFactory.trainEditorRequest();
    }

    public TrainRequest getTrainRequest() {
        return requestFactory.trainRequest();
    }

    public BusRequest getBusRequest() {
        return requestFactory.busRequest();
    }

    public ConfigRequest getConfigRequest() {
        return requestFactory.configRequest();
    }

    public ConstructionRequest getConstructionRequest() {
        return requestFactory.constructionRequest();
    }
}
