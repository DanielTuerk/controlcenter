package net.wbz.moba.controlcenter.web.client.train;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Modal to edit a {@link Train}.
 *
 * @author Daniel Tuerk
 */
public class TrainEditModal extends AbstractTrainEditModal {

   public TrainEditModal(Train train) {
        super(train, "Edit train");
    }

    @Override
    protected void persist(Train train) {
        RequestUtils.getInstance().getTrainEditorService().updateTrain(train,
                RequestUtils.VOID_ASYNC_CALLBACK);
    }
}
