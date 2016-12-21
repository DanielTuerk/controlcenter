package net.wbz.moba.controlcenter.web.shared;

import com.google.web.bindery.requestfactory.shared.RequestFactory;
import net.wbz.moba.controlcenter.web.shared.bus.BusService;
import net.wbz.moba.controlcenter.web.shared.config.ConfigService;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;

/**
 * TODO
 *
 * @author Daniel Tuerk
 */
public interface FoobarRequestFactory extends RequestFactory {

    ConstructionService constructionRequest();
    BusService busRequest();
    ConfigService configRequest();
    TrainService trainRequest();
    TrainEditorService trainEditorRequest();
    TrackViewerService trackViewerRequest();
    TrackEditorService trackEditorRequest();
}
