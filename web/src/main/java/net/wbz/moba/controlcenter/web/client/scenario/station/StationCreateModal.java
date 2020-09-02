package net.wbz.moba.controlcenter.web.client.scenario.station;

import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;

/**
 * @author Daniel Tuerk
 */
class StationCreateModal extends AbstractStationDataModal {

    StationCreateModal(Station station) {
        super(station, "Create station");
    }

    @Override
    protected void save(Station station) {
        RequestUtils.getInstance().getScenarioEditorService().createStation(station,
            RequestUtils.VOID_ASYNC_CALLBACK);
    }
}
