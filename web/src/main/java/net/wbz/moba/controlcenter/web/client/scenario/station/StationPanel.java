package net.wbz.moba.controlcenter.web.client.scenario.station;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Collection;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.client.components.modal.DeleteModal;
import net.wbz.moba.controlcenter.web.client.components.table.DeleteButtonColumn;
import net.wbz.moba.controlcenter.web.client.components.table.EditButtonColumn;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.station.StationRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import net.wbz.moba.controlcenter.web.shared.station.StationPlatform;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

/**
 * @author Daniel Tuerk
 */
public class StationPanel extends Composite {

    private static final Binder uiBinder = GWT.create(Binder.class);
    private final StationRemoteListener stationBoardListener;
    @UiField
    CellTable<Station> stationTable;
    @UiField
    Container stationContainer;
    private final SimplePager simplePager = new SimplePager();
    private final Pagination pagination = new Pagination(PaginationSize.SMALL);
    private final ListDataProvider<Station> dataProvider = new ListDataProvider<>();

    public StationPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        stationBoardListener = event -> loadStations();

        stationTable.addColumn(new TextColumn<Station>() {
            @Override
            public String getValue(Station object) {
                return object.getName();
            }
        }, "Name");
        stationTable.addColumn(new TextColumn<Station>() {
            @Override
            public String getValue(Station object) {
                StringBuilder sb = new StringBuilder();
                if (object.getPlatforms() != null) {
                    for (StationPlatform stationPlatform : object.getPlatforms()) {
                        sb.append(stationPlatform.getName());
                        sb.append(": ");
                        sb.append(stationPlatform.getTrackBlocks().stream()
                            .map(TrackBlock::getDisplayValue)
                            .collect(Collectors.joining(", ")));
                        sb.append("\n");
                    }
                }
                return sb.toString();
            }
        }, "Platforms");
        stationTable.addColumn(new EditButtonColumn<Station>() {
            @Override
            public void onAction(Station object) {
                showEdit(object);
            }
        }, "Edit");

        stationTable.addColumn(new DeleteButtonColumn<Station>() {
            @Override
            public void onAction(Station object) {
                showDelete(object);
            }
        }, "Delete");

        stationContainer.add(pagination);

        stationTable.addRangeChangeHandler(event -> pagination.rebuild(simplePager));

        simplePager.setDisplay(stationTable);
        pagination.clear();

        dataProvider.addDataDisplay(stationTable);

    }

    @UiHandler("btnCreateStation")
    void onClick(ClickEvent ignored) {
        new StationCreateModal(new Station()).show();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        loadStations();

        EventReceiver.getInstance().addListener(stationBoardListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(stationBoardListener);
    }

    private void loadStations() {
        RequestUtils.getInstance().getStationEditorService().getStations(
            new OnlySuccessAsyncCallback<Collection<Station>>() {
                @Override
                public void onSuccess(Collection<Station> result) {
                    dataProvider.setList(Lists.newArrayList(result));
                    dataProvider.flush();
                    dataProvider.refresh();
                    pagination.rebuild(simplePager);
                }
            });
    }

    private void showDelete(final Station station) {
        new DeleteModal("Delete station " + station.getName() + "?") {

            @Override
            public void onConfirm() {
                RequestUtils.getInstance().getStationEditorService().deleteStation(station.getId(),
                    RequestUtils.VOID_ASYNC_CALLBACK);
                hide();
            }
        }.show();
    }

    private void showEdit(Station station) {
        new StationEditModal(station).show();
    }

    interface Binder extends UiBinder<Widget, StationPanel> {

    }

}
