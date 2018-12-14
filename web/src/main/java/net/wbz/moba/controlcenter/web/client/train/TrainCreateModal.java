package net.wbz.moba.controlcenter.web.client.train;

import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Modal to create a {@link Train}.
 *
 * @author Daniel Tuerk
 */
class TrainCreateModal extends AbstractTrainEditModal {

    TrainCreateModal(Train train) {
        super(train, "Create train");
    }

    @Override
    protected void persist(Train train) {
        RequestUtils.getInstance().getTrainEditorService().createTrain(train,
            RequestUtils.VOID_ASYNC_CALLBACK);
    }
}
