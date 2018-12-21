package net.wbz.moba.controlcenter.web.client.scenario.route;

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
import java.util.ArrayList;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.components.modal.DeleteModal;
import net.wbz.moba.controlcenter.web.client.components.table.DeleteButtonColumn;
import net.wbz.moba.controlcenter.web.client.components.table.EditButtonColumn;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.event.scenario.ScenarioRemoteListener;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.StationRail;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

/**
 * @author Daniel Tuerk
 */
public class RoutePanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final ScenarioRemoteListener scenarioRemoteListener;
    @UiField
    Container container;
    @UiField
    CellTable<Route> routeTable;
    private SimplePager simplePager = new SimplePager();
    private Pagination pagination = new Pagination(PaginationSize.SMALL);
    private ListDataProvider<Route> dataProvider = new ListDataProvider<>();
    private Collection<Station> stations = new ArrayList<>();

    public RoutePanel() {
        initWidget(uiBinder.createAndBindUi(this));

        scenarioRemoteListener = anEvent -> {
                loadRoutes();
        };

        TextColumn<Route> colName = new TextColumn<Route>() {
            @Override
            public String getValue(Route object) {
                return object.getName();
            }
        };
        routeTable.addColumn(colName, "Name");
        TextColumn<Route> colStart = new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return route.getStart() != null ? route.getStart().getDisplayValue() : "";
            }
        };
        routeTable.addColumn(colStart, "Start");
        TextColumn<Route> colEnd = new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return route.getEnd() != null ? route.getEnd().getDisplayValue() : "";
            }
        };
        routeTable.addColumn(colEnd, "End");
        routeTable.addColumn(new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return String.valueOf(route.getOneway());
            }
        }, "Oneway");
        routeTable.addColumn(new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return route.getTrack() != null && route.getTrack().getLength() > 0 ? "Ok" : "Error";
            }
        }, "Track");

        routeTable.addColumn(new EditButtonColumn<Route>() {
            @Override
            public void onAction(Route object) {
                showEdit(object);
            }
        }, "Edit");
        routeTable.addColumn(new DeleteButtonColumn<Route>() {
            @Override
            public void onAction(Route object) {
                showDelete(object);
            }
        }, "Delete");

        container.add(pagination);

        routeTable.addRangeChangeHandler(event -> pagination.rebuild(simplePager));

        simplePager.setDisplay(routeTable);
        pagination.clear();

        dataProvider.addDataDisplay(routeTable);
    }

    @UiHandler("btnCreateRoute")
    void onClick(ClickEvent ignored) {
        Route route = new Route();
        route.setWaypoints(new ArrayList<>());
        showEdit(route);
    }

    private void showEdit(Route route) {
        new RouteEditModal(route).show();
    }

    private void showDelete(final Route route) {
        new DeleteModal("Delete route " + route.getName() + "?") {

            @Override
            public void onConfirm() {
                RequestUtils.getInstance().getScenarioEditorService().deleteRoute(route.getId(),
                    RequestUtils.VOID_ASYNC_CALLBACK);
                hide();
            }
        }.show();
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        EventReceiver.getInstance().addListener(scenarioRemoteListener);

        RequestUtils.getInstance().getScenarioEditorService().getStations(
            new OnlySuccessAsyncCallback<Collection<Station>>() {
                @Override
                public void onSuccess(Collection<Station> result) {
                    stations.clear();
                    stations.addAll(result);

                    loadRoutes();
                }
            });
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(scenarioRemoteListener);
    }

    private String getStationRailDisplayName(StationRail stationRail) {
        for (Station station : stations) {
            for (StationRail rail : station.getRails()) {
                if (rail.equals(stationRail)) {
                    return String.valueOf(station.getName() + " - " + rail.getRailNumber());
                }
            }
        }
        return "";
    }

    private void loadRoutes() {
        RequestUtils.getInstance().getScenarioEditorService().getRoutes(
            new OnlySuccessAsyncCallback<Collection<Route>>() {
                @Override
                public void onSuccess(Collection<Route> result) {
                    dataProvider.setList(Lists.newArrayList(result));
                    dataProvider.flush();
                    dataProvider.refresh();
                    pagination.rebuild(simplePager);
                }
            });
    }

    interface Binder extends UiBinder<Widget, RoutePanel> {

    }
}
