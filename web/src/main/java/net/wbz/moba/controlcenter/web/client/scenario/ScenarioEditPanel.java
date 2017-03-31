package net.wbz.moba.controlcenter.web.client.scenario;

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

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.modal.DeleteModal;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.StationRail;

/**
 * @author Daniel Tuerk
 */
public class ScenarioEditPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    @UiField
    Container container;
    @UiField
    CellTable<Scenario> scenarioTable;
    private SimplePager simplePager = new SimplePager();
    private Pagination pagination = new Pagination(PaginationSize.SMALL);
    private ListDataProvider<Scenario> dataProvider = new ListDataProvider<>();
    private Collection<Station> stations = new ArrayList<>();

    public ScenarioEditPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        scenarioTable.addColumn(new TextColumn<Scenario>() {
            @Override
            public String getValue(Scenario object) {
                return object.getName();
            }
        }, "Name");
        scenarioTable.addColumn(new TextColumn<Scenario>() {
            @Override
            public String getValue(Scenario object) {
                return object.getCron();
            }
        }, "Cron");
        scenarioTable.addColumn(new TextColumn<Scenario>() {
            @Override
            public String getValue(Scenario object) {
                String trainDisplayValue =
                        object.getTrain() != null ? object.getTrain().getName() + "(" + object.getTrain().getAddress()
                                + ")" : "unknown";
                return trainDisplayValue + " direction: "
                        + (object.getTrainDrivingDirection() != null ? object.getTrainDrivingDirection().name() : "");
            }
        }, "Train");
        scenarioTable.addColumn(new TextColumn<Scenario>() {
            @Override
            public String getValue(Scenario object) {
                return object.getRunState() != null ? object.getRunState().name() : "";
            }
        }, "State");
        scenarioTable.addColumn(new TextColumn<Scenario>() {
            @Override
            public String getValue(Scenario object) {
                return object.getMode() != null ? object.getMode().name() : "";
            }
        }, "Mode");
        scenarioTable.addColumn(new TextColumn<Scenario>() {
            @Override
            public String getValue(Scenario object) {
                // TODO refactor to component like list
                StringBuilder sb = new StringBuilder();
                if (object.getRouteSequences() != null) {
                    for (RouteSequence routeSequence : object.getRouteSequences()) {
                        Route route = routeSequence.getRoute();
                        if (route != null) {
                            sb.append("from: ");
                            sb.append(getStationRailDisplayName(route.getStartStationRail()));
                            sb.append(" to: ");
                            sb.append(getStationRailDisplayName(route.getEndStationRail()));
                            sb.append("\n");
                        }
                    }

                }
                return sb.toString();
            }
        }, "Route");

        final Column<Scenario, String> colEdit = new Column<Scenario, String>(new ButtonCell(ButtonType.DEFAULT,
                IconType.EDIT)) {
            @Override
            public String getValue(Scenario object) {
                return "";
            }
        };
        colEdit.setFieldUpdater(new FieldUpdater<Scenario, String>() {
            @Override
            public void update(int index, Scenario object, String value) {
                showEdit(object);
            }
        });
        scenarioTable.addColumn(colEdit, "Delete");
        final Column<Scenario, String> colDelete = new Column<Scenario, String>(new ButtonCell(ButtonType.DANGER,
                IconType.TRASH)) {
            @Override
            public String getValue(Scenario object) {
                return "";
            }
        };
        colDelete.setFieldUpdater(new FieldUpdater<Scenario, String>() {
            @Override
            public void update(int index, Scenario object, String value) {
                showDelete(object);
            }
        });
        scenarioTable.addColumn(colDelete, "Edit");

        container.add(pagination);

        scenarioTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {

            @Override
            public void onRangeChange(final RangeChangeEvent event) {
                pagination.rebuild(simplePager);
            }
        });

        simplePager.setDisplay(scenarioTable);
        pagination.clear();

        dataProvider.addDataDisplay(scenarioTable);

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

    @Override
    protected void onLoad() {
        super.onLoad();

        RequestUtils.getInstance().getScenarioEditorService().getStations(
                new OnlySuccessAsyncCallback<Collection<Station>>() {
                    @Override
                    public void onSuccess(Collection<Station> result) {
                        stations.clear();
                        stations.addAll(result);

                        loadScenarios();
                    }
                });
    }

    private void showDelete(final Scenario scenario) {
        new DeleteModal("Delete scenario " + scenario.getName() + "?") {

            @Override
            public void onConfirm() {
                RequestUtils.getInstance().getScenarioEditorService().deleteScenario(scenario.getId(),
                        RequestUtils.VOID_ASYNC_CALLBACK);
                hide();
            }
        }.show();
    }

    @UiHandler("btnCreateScenario")
    void onClick(ClickEvent e) {
        new ScenarioCreateModal(new Scenario()).show();
    }

    private void loadScenarios() {
        RequestUtils.getInstance().getScenarioEditorService().getScenarios(
                new OnlySuccessAsyncCallback<Collection<Scenario>>() {
                    @Override
                    public void onSuccess(Collection<Scenario> result) {
                        dataProvider.setList(Lists.newArrayList(result));
                        dataProvider.flush();
                        dataProvider.refresh();
                        pagination.rebuild(simplePager);
                    }
                });
    }

    private void showEdit(Scenario scenario) {
        new ScenarioEditModal(scenario).show();
    }

    interface Binder extends UiBinder<Widget, ScenarioEditPanel> {
    }

}
