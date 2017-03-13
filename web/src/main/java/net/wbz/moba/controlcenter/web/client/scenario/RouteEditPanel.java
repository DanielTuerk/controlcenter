package net.wbz.moba.controlcenter.web.client.scenario;

import java.util.ArrayList;
import java.util.Collection;

import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.StationRail;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlock;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlockPart;

/**
 * @author Daniel Tuerk
 */
public class RouteEditPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Container container;
    @UiField
    CellTable<Route> routeTable;

    private SimplePager simplePager = new SimplePager();
    private Pagination pagination = new Pagination(PaginationSize.SMALL);
    private ListDataProvider<Route> dataProvider = new ListDataProvider<>();

    public RouteEditPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        routeTable.addColumn(new TextColumn<Route>() {
            @Override
            public String getValue(Route object) {
                return object.getName();
            }
        }, "Name");
        routeTable.addColumn(new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return getStationRailDisplayName(route.getStartStationRail());
            }
        }, "Start");
        routeTable.addColumn(new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return getStationRailDisplayName(route.getEndStationRail());
            }
        }, "End");
        routeTable.addColumn(new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                return String.valueOf(route.getOneway());
            }
        }, "Oneway");

        routeTable.addColumn(new TextColumn<Route>() {
            @Override
            public String getValue(Route route) {
                StringBuilder sb = new StringBuilder();
                if (route.getRouteBlocks() != null) {
                    for (RouteBlock routeBlock : route.getRouteBlocks()) {
                        if (routeBlock != null) {
                            sb.append("pos: ")
                                    .append(routeBlock.getPosition())
                                    .append("start: ")
                                    .append(routeBlock.getStartPoint() != null ? routeBlock.getStartPoint()
                                            .getStopBlock().getDisplayValue() : "")
                                    .append(" end: ")
                                    .append(routeBlock.getEndPoint() != null ? routeBlock.getEndPoint().getStopBlock()
                                            .getDisplayValue() : "")
                                    .append("\n");
                            if (routeBlock.getRouteBlockParts() != null) {
                                sb.append("blocks: ").append("\n");
                                for (RouteBlockPart blockPart : routeBlock.getRouteBlockParts()) {
                                    sb.append("pos: ")
                                            .append(blockPart.getSwitchTrackPart().getToggleFunction())
                                            .append(" state: ").append(blockPart.isState());
                                }

                            }
                        }
                    }
                }
                return sb.toString();
            }
        }, "Blocks");

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

    private Collection<Station> stations = new ArrayList<>();

    @Override
    protected void onLoad() {
        super.onLoad();

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

    interface Binder extends UiBinder<Widget, RouteEditPanel> {
    }
}
