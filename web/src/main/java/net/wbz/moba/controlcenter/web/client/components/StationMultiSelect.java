package net.wbz.moba.controlcenter.web.client.components;

import java.util.Collection;
import javax.validation.constraints.NotNull;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.station.Station;

/**
 * Multi Select component for {@link Station}s.
 *
 * @author Daniel Tuerk
 */
public class StationMultiSelect extends AbstractLoadableMultiSelect<Station> {

    @Override
    protected String getKey(@NotNull Station object) {
        return String.valueOf(object.getId());
    }

    @Override
    protected String getDisplayValue(Station choice) {
        return choice.getName();
    }

    @Override
    void loadChoices(OnlySuccessAsyncCallback<Collection<Station>> callback) {
        RequestUtils.getInstance().getStationEditorService().getStations(callback);
    }

}
