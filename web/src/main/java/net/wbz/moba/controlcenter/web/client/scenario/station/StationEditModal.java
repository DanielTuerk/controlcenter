package net.wbz.moba.controlcenter.web.client.scenario.station;

import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.station.Station;

/**
 * @author Daniel Tuerk
 */
public class StationEditModal extends AbstractStationDataModal {

    StationEditModal(Station station) {
        super(station, "Edit station");
        setWidth("60%");
    }

    @Override
    protected void save(Station station) {
        RequestUtils.getInstance().getStationEditorService().updateStation(station,
            RequestUtils.VOID_ASYNC_CALLBACK);
    }
}
