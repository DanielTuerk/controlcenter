package net.wbz.moba.controlcenter.web.shared.train;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */

public interface TrainEditorService {

    public List<Train> getTrains();

    public Train getTrain(int address);

    public void createTrain(String name);

    public void deleteTrain(long trainId);

    public void updateTrain(Train train);

}
