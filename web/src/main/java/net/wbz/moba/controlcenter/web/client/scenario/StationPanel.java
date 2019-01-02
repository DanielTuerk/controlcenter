package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import javax.annotation.Nullable;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.scenario.StationRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.StringUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.StationRail;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;

/**
 * TODO edit
 *
 * @author Daniel Tuerk
 */
public class StationPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final StationRemoteListener stationRemoteListener;

    interface Binder extends UiBinder<Widget, StationPanel> {
    }

    @UiField
    Container stationContainer;
    @UiField
    ListGroup stationsList;

    StationPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        stationRemoteListener = event -> loadStations();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        loadStations();

        EventReceiver.getInstance().addListener(stationRemoteListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(stationRemoteListener);
    }

    private void loadStations() {
        stationsList.clear();
        RequestUtils.getInstance().getScenarioEditorService().getStations(
                new OnlySuccessAsyncCallback<Collection<Station>>() {
                    @Override
                    public void onSuccess(Collection<Station> result) {
                        for (Station station : result) {
                            ListGroupItem item = new ListGroupItem();
                            item.setText(station.getName() + ": " + StringUtils.join(Lists.transform(station.getRails(),
                                StationRail::getRailNumber), ","));
                            stationsList.add(item);
                        }
                    }
                });
    }
}
