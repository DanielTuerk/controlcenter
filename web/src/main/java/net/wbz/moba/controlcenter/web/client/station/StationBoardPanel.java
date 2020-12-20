package net.wbz.moba.controlcenter.web.client.station;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.station.StationBoardListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import net.wbz.moba.controlcenter.web.shared.station.StationBoardChangedEvent;
import net.wbz.moba.controlcenter.web.shared.station.StationBoardEntry;
import org.gwtbootstrap3.client.ui.PageHeader;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

/**
 * Panel for the station board of a {@link Station} which shows the arrivals and departures.
 *
 * @author Daniel Tuerk
 */
public class StationBoardPanel extends Composite {

    private static final Binder UI_BINDER = GWT.create(Binder.class);
    private final StationBoardListener stationBoardListener;
    private final ListDataProvider<StationBoardEntry> departureDataProvider = new ListDataProvider<>();
    private final ListDataProvider<StationBoardEntry> arrivalDataProvider = new ListDataProvider<>();
    private final Station station;
    @UiField
    PageHeader stationName;
    @UiField
    CellTable<StationBoardEntry> departureTable;
    @UiField
    CellTable<StationBoardEntry> arrivalTable;

    public StationBoardPanel(Station station) {
        this.station = station;
        initWidget(UI_BINDER.createAndBindUi(this));

        stationBoardListener = new StationBoardListener() {
            @Override
            public void arrivalBoardChanged(StationBoardChangedEvent event) {
                if (event.getStationId() == station.getId()) {
                    updateArrivals();
                }
            }

            @Override
            public void departureBoardChanged(StationBoardChangedEvent event) {
                if (event.getStationId() == station.getId()) {
                    updateDepartures();
                }
            }
        };

        stationName.setText(station.getName());

        initBoardTable("Destination", departureDataProvider, departureTable);
        initBoardTable("From", arrivalDataProvider, arrivalTable);
    }

    private void updateArrivals() {
        RequestUtils.getInstance().getStationsBoardService().loadArrivalBoard(station.getId(),
            new OnlySuccessAsyncCallback<Collection<StationBoardEntry>>() {
                @Override
                public void onSuccess(Collection<StationBoardEntry> result) {
                    updateDataProvider(arrivalDataProvider, result);
                }
            });
    }

    private void updateDepartures() {
        RequestUtils.getInstance().getStationsBoardService().loadDepartureBoard(station.getId(),
            new OnlySuccessAsyncCallback<Collection<StationBoardEntry>>() {
                @Override
                public void onSuccess(Collection<StationBoardEntry> result) {
                    updateDataProvider(departureDataProvider, result);
                }
            });
    }

    private void updateDataProvider(ListDataProvider<StationBoardEntry> dataProvider,
        Collection<StationBoardEntry> entries) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(entries);
        dataProvider.flush();
        dataProvider.refresh();
    }

    private void initBoardTable(String stationName, ListDataProvider<StationBoardEntry> dataProvider,
        CellTable<StationBoardEntry> table) {

        table.addColumn(new TextColumn<StationBoardEntry>() {
            @Override
            public String getValue(StationBoardEntry object) {
                return object.getTimeText();
            }
        }, "Time");
        table.addColumn(new TextColumn<StationBoardEntry>() {
            @Override
            public String getValue(StationBoardEntry object) {
                return object.getTrain();
            }
        }, "Train");

        table.addColumn(new TextColumn<StationBoardEntry>() {
            @Override
            public String getValue(StationBoardEntry object) {
                return object.getStation();
            }
        }, stationName);
        table.addColumn(new TextColumn<StationBoardEntry>() {
            @Override
            public String getValue(StationBoardEntry object) {
                return object.getPlatform();
            }
        }, "Platform");
        table.addColumn(new TextColumn<StationBoardEntry>() {
            @Override
            public String getValue(StationBoardEntry object) {
                return String.join(", ", object.getViaStations());
            }
        }, "via");
        table.addColumn(new TextColumn<StationBoardEntry>() {
            @Override
            public String getValue(StationBoardEntry object) {
                return object.getInformation();
            }
        }, "Information");

        dataProvider.addDataDisplay(table);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        updateArrivals();
        updateDepartures();

        EventReceiver.getInstance().addListener(stationBoardListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(stationBoardListener);
    }

    interface Binder extends UiBinder<Widget, StationBoardPanel> {

    }

}
