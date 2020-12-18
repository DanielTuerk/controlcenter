package net.wbz.moba.controlcenter.web.client.station;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import org.gwtbootstrap3.client.ui.Container;

/**
 * @author Daniel Tuerk
 */
public class StationBoardsPanel extends Composite {

    private static final Binder UI_BINDER = GWT.create(Binder.class);
    private final List<StationBoardPanel> stationBoardPanels = new ArrayList<>();
    @UiField
    Container stationsBoardContainer;
    @UiField
    SelectStationPanel selectStationPanel;

    public StationBoardsPanel() {
        initWidget(UI_BINDER.createAndBindUi(this));

        selectStationPanel.addOpenStationConsumer(stations -> {
            clearStations();
            stations.forEach(this::addStation);
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        clearStations();
    }

    private void addStation(Station station) {
        StationBoardPanel stationBoardPanel = new StationBoardPanel(station);
        stationBoardPanels.add(stationBoardPanel);
        stationsBoardContainer.add(stationBoardPanel);
    }

    private void clearStations() {
        stationBoardPanels.forEach(stationBoardPanel -> stationsBoardContainer.remove(stationBoardPanel));
        stationBoardPanels.clear();
    }

    interface Binder extends UiBinder<Widget, StationBoardsPanel> {

    }
}
