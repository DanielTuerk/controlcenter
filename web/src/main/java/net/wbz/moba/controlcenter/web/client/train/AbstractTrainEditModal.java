package net.wbz.moba.controlcenter.web.client.train;

import com.google.gwt.user.client.ui.IsWidget;
import net.wbz.moba.controlcenter.web.client.components.AbstractEditModal;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
abstract class AbstractTrainEditModal extends AbstractEditModal<Train> {

    private TrainEditModalBody editModalBody;

    AbstractTrainEditModal(Train train, String title) {
        super(title, "Apply", "Cancel", train);
    }

    @Override
    protected IsWidget createContent(Train model) {
        editModalBody = new TrainEditModalBody(model);
        return editModalBody;
    }

    @Override
    protected void onCancel() {
        hide();
    }

    @Override
    protected void onConfirm(Train model) {
        persist(editModalBody.getUpdatedModel());
        hide();
    }

    protected abstract void persist(Train train);
}
