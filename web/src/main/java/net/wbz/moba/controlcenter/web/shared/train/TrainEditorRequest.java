package net.wbz.moba.controlcenter.web.shared.train;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;
import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.train.TrainEditorService;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Service(value = TrainEditorService.class, locator = InjectingServiceLocator.class)
public interface TrainEditorRequest extends RequestContext {

    Request<List<TrainProxy>> getTrains();

    Request<TrainProxy> getTrain(int address);

    Request<Void> createTrain(String name);

    Request<Void> deleteTrain(long trainId);

    Request<Void> updateTrain(TrainProxy train);

}
