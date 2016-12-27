package net.wbz.moba.controlcenter.web.server.web.train;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainDataChangedEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrainEditorServiceImpl extends RemoteServiceServlet implements TrainEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainEditorServiceImpl.class);

    private final TrainManager trainManager;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public TrainEditorServiceImpl(TrainManager trainManager, EventBroadcaster eventBroadcaster) {
        this.trainManager = trainManager;
        this.eventBroadcaster = eventBroadcaster;
    }

    public Collection<Train> getTrains() {
        return trainManager.getTrains();
    }

    public Train getTrain(int address) {
        return trainManager.getTrain(address);
    }

    public void createTrain(Train train) {
        try {
            trainManager.createTrain(train);
        } catch (Exception e) {
            String msg = String.format("can't create train '%s'", train.getName());
            LOG.error(msg, e);
        }
    }

    public void deleteTrain(long trainId) {
        trainManager.deleteTrain(trainId);
        eventBroadcaster.fireEvent(new TrainDataChangedEvent(trainId));
    }

    public void updateTrain(Train train) {
        try {
            trainManager.updateTrain(train);

            eventBroadcaster.fireEvent(new TrainDataChangedEvent(train.getId()));
        } catch (Exception e) {
            String msg = String.format("can't update train '%s'", train.getName());
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
