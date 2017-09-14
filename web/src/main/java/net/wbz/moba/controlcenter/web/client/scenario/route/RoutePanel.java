package net.wbz.moba.controlcenter.web.client.scenario.route;

import java.util.ArrayList;
import java.util.Collection;

import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlockPart;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenariosChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.StationRail;

/**
 * @author Daniel Tuerk
 */
public class RoutePanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final RemoteEventListener scenarioEventListener;
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

        scenarioEventListener = new RemoteEventListener() {
            @Override
            public void apply(Event anEvent) {
                if (anEvent instanceof ScenariosChangedEvent) {
                    loadRoutes();
                }
            }
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
                return route.getStart().getDisplayValue();
            }
        };
        routeTable.addColumn(colStart, "Start");
        TextColumn<Route> colEnd = new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return route.getEnd().getDisplayValue();
            }
        };
        routeTable.addColumn(colEnd, "End");
        routeTable.addColumn(new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return String.valueOf(route.getOneway());
            }
        }, "Oneway");

        final Column<Route, String> colEdit = new Column<Route, String>(new ButtonCell(ButtonType.DEFAULT,
                IconType.EDIT)) {
            @Override
            public String getValue(Route object) {
                return "";
            }
        };
        colEdit.setFieldUpdater(new FieldUpdater<Route, String>() {
            @Override
            public void update(int index, Route object, String value) {
                showEdit(object);
            }
        });
        routeTable.addColumn(colEdit, "Edit");

        container.add(pagination);

        routeTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {

            @Override
            public void onRangeChange(final RangeChangeEvent event) {
                pagination.rebuild(simplePager);
            }
        });

        simplePager.setDisplay(routeTable);
        pagination.clear();

        dataProvider.addDataDisplay(routeTable);
    }

    @UiHandler("btnCreateRoute")
    void onClick(ClickEvent ignored) {
        Route route = new Route();
        route.setRouteBlockParts(new ArrayList<RouteBlockPart>());
        showEdit(route);
    }

    private void showEdit(Route route) {
        new RouteEditModal(route).show();
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        EventReceiver.getInstance().addListener(ScenariosChangedEvent.class, scenarioEventListener);

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
        EventReceiver.getInstance().removeListener(ScenariosChangedEvent.class, scenarioEventListener);
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
