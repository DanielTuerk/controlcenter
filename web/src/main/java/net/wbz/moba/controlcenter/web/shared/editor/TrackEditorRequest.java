package net.wbz.moba.controlcenter.web.shared.editor;

import com.google.web.bindery.requestfactory.shared.ExtraTypes;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;
import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.track.model.*;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunctionProxy;
import net.wbz.moba.controlcenter.web.shared.train.TrainProxy;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Service(value = TrackEditorService.class, locator = InjectingServiceLocator.class)
@ExtraTypes({SignalProxy.class, StraightProxy.class, UncouplerProxy.class, SwitchProxy.class, CurveProxy.class})
public interface TrackEditorRequest extends RequestContext {

    Request<List<TrackPartProxy>> loadTrack();

    Request<Void> saveTrack(List<TrackPartProxy> trackParts);

    Request<Void> registerConsumersByConnectedDeviceForTrackParts(List<TrackPartProxy> trackParts);
}
