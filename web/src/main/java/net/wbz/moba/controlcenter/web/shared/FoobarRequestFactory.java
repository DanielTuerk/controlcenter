package net.wbz.moba.controlcenter.web.shared;

import com.google.web.bindery.requestfactory.shared.RequestFactory;
import net.wbz.moba.controlcenter.web.shared.bus.BusRequest;
import net.wbz.moba.controlcenter.web.shared.config.ConfigRequest;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionRequest;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorRequest;
import net.wbz.moba.controlcenter.web.shared.train.TrainRequest;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerRequest;

/**
 * TODO
 *
 * @author Daniel Tuerk
 */
public interface FoobarRequestFactory extends RequestFactory {

    ConstructionRequest constructionRequest();
    BusRequest busRequest();
    ConfigRequest configRequest();
    TrainRequest trainRequest();
    TrainEditorRequest trainEditorRequest();
    TrackViewerRequest trackViewerRequest();
    TrackEditorRequest trackEditorRequest();
}
