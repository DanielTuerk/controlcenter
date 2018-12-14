package net.wbz.moba.controlcenter.web.client.scenario;

import java.util.Collection;

import javax.annotation.Nullable;

import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.StringUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.StationRail;

/**
 * TODO edit
 * TODO change event
 *
 * @author Daniel Tuerk
 */
public class StationPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, StationPanel> {
    }

    @UiField
    Container stationContainer;
    @UiField
    ListGroup stationsList;

    public StationPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        loadStations();
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
                                    new Function<StationRail, Integer>() {
                                        @Nullable
                                        @Override
                                        public Integer apply(StationRail input) {
                                            return input.getRailNumber();
                                        }
                                    }), ","));
                            stationsList.add(item);

                        }
                    }
                });
    }
}
