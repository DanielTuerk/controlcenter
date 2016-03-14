package net.wbz.moba.controlcenter.web.server.train;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainDataChangedEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrainEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainEditorService.class);

    private final TrainManager trainManager;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public TrainEditorService(TrainManager trainManager,
                              final EventBroadcaster eventBroadcaster) {
        this.trainManager = trainManager;
        this.eventBroadcaster = eventBroadcaster;
    }

    public List<Train> getTrains() {
        return trainManager.getTrains();
    }

    public Train getTrain(int address) {
        try {
            return trainManager.getTrainByAddress(address);
        } catch (TrainException e) {
            LOG.error("can't find train", e);
        }
        return null;
    }

    public void createTrain(String name) {
        Train train = new Train(name);

        Set<TrainFunction> trainFunctions = new HashSet<>();
        for (TrainFunction.FUNCTION function : TrainFunction.FUNCTION.values()) {
            trainFunctions.add(new TrainFunction(function, false));
        }
        train.setFunctions(trainFunctions);
        try {
            trainManager.storeTrain(train);
        } catch (Exception e) {
            String msg = String.format("can't create train '%s'", name);
            LOG.error(msg, e);
        }
    }

    public void deleteTrain(long trainId) {
        try {
            trainManager.deleteTrain(trainId);

            eventBroadcaster.fireEvent(new TrainDataChangedEvent(trainId));
        } catch (TrainException e) {
            String msg = String.format("can't delete train '%s'", trainId);
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public void updateTrain(Train train) {
        try {
            trainManager.storeTrain(train);

            eventBroadcaster.fireEvent(new TrainDataChangedEvent(train.getId()));
        } catch (Exception e) {
            String msg = String.format("can't update train '%s'", train.getName());
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
