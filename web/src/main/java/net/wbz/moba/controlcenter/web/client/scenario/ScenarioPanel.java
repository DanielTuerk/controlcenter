package net.wbz.moba.controlcenter.web.client.scenario;

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
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.ArrayList;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.components.table.DeleteButtonColumn;
import net.wbz.moba.controlcenter.web.client.components.table.EditButtonColumn;
import net.wbz.moba.controlcenter.web.client.components.modal.DeleteModal;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenariosChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

/**
 * @author Daniel Tuerk
 */
public class ScenarioPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final RemoteEventListener scenarioEventListener;
    @UiField
    Container container;
    @UiField
    CellTable<Scenario> scenarioTable;
    private SimplePager simplePager = new SimplePager();
    private Pagination pagination = new Pagination(PaginationSize.SMALL);
    private ListDataProvider<Scenario> dataProvider = new ListDataProvider<>();
    private Collection<Station> stations = new ArrayList<>();

    public ScenarioPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        scenarioEventListener = new RemoteEventListener() {
            @Override
            public void apply(Event anEvent) {
                if (anEvent instanceof ScenariosChangedEvent) {
                    loadScenarios();
                }
            }
        };

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
                return object.getTrain() != null ? object.getTrain().getName() + "(" + object.getTrain().getAddress()
                    + ")" : " no train selected";
            }
        }, "Train");
        scenarioTable.addColumn(new TextColumn<Scenario>() {
            @Override
            public String getValue(Scenario object) {
                return String.valueOf(object.getTrainDrivingDirection());
            }
        }, "direction");
        scenarioTable.addColumn(new TextColumn<Scenario>() {
            @Override
            public String getValue(Scenario object) {
                return String.valueOf(object.getStartDrivingLevel());
            }
        }, "Start driving level");
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
                            sb.append(route.getName());
                            sb.append("\n");
                            sb.append(" to: ");
                            sb.append(route.getName());
                            sb.append("\n");
                        }
                    }

                }
                return sb.toString();
            }
        }, "Route");

        scenarioTable.addColumn(new EditButtonColumn<Scenario>() {
            @Override
            public void onAction(Scenario object) {
                showEdit(object);
            }
        }, "Edit");

        scenarioTable.addColumn(new DeleteButtonColumn<Scenario>() {
            @Override
            public void onAction(Scenario object) {
                showDelete(object);
            }
        }, "Delete");

        container.add(pagination);

        scenarioTable.addRangeChangeHandler(event -> pagination.rebuild(simplePager));

        simplePager.setDisplay(scenarioTable);
        pagination.clear();

        dataProvider.addDataDisplay(scenarioTable);

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(ScenariosChangedEvent.class, scenarioEventListener);

        loadStations();
        loadScenarios();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(ScenariosChangedEvent.class, scenarioEventListener);
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
    void onClick(ClickEvent ignored) {
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

    private void loadStations() {
        RequestUtils.getInstance().getScenarioEditorService().getStations(
            new OnlySuccessAsyncCallback<Collection<Station>>() {
                @Override
                public void onSuccess(Collection<Station> result) {
                    stations.clear();
                    stations.addAll(result);


                }
            });
    }

    private void showEdit(Scenario scenario) {
        new ScenarioEditModal(scenario).show();
    }

    interface Binder extends UiBinder<Widget, ScenarioPanel> {

    }

}
