package net.wbz.moba.controlcenter.web.client.scenario.station;

import com.google.gwt.user.client.ui.IsWidget;
import net.wbz.moba.controlcenter.web.client.components.AbstractEditModal;
import net.wbz.moba.controlcenter.web.shared.station.Station;

/**
 * @author Daniel Tuerk
 */
abstract class AbstractStationDataModal extends AbstractEditModal<Station> {

    private StationEditModalBody editModalBody;

    AbstractStationDataModal(Station station, String title) {
        super(title, "Apply", "Cancel", station);
    }

    @Override
    protected IsWidget createContent(Station model) {
        editModalBody = new StationEditModalBody(model);
        return editModalBody;
    }

    @Override
    protected void onCancel() {
        hide();
    }

    @Override
    protected void onConfirm(Station model) {
        save(editModalBody.getUpdatedModel());
        hide();
    }

    protected abstract void save(Station scenario);
}
