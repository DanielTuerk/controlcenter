package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.scenario.ScenarioRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStatistic;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

/**
 * TODO
 *
 * @author Daniel Tuerk
 */
public class ScenarioStatisticPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final ScenarioRemoteListener scenarioRemoteListener;
    @UiField
    Container container;
    @UiField
    CellTable<ScenarioStatistic> scenarioTable;
    private SimplePager simplePager = new SimplePager();
    private Pagination pagination = new Pagination(PaginationSize.SMALL);
    private ListDataProvider<ScenarioStatistic> dataProvider = new ListDataProvider<>();

    ScenarioStatisticPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        scenarioRemoteListener = event -> loadStatistic();

        scenarioTable.addColumn(new TextColumn<ScenarioStatistic>() {
            @Override
            public String getValue(ScenarioStatistic object) {
                return object.getScenario().getName();
            }
        }, "Name");
        scenarioTable.addColumn(new TextColumn<ScenarioStatistic>() {
            @Override
            public String getValue(ScenarioStatistic object) {
                return object.getScenario().getRunState() != null ? object.getScenario().getRunState().name() : "";
            }
        }, "State");

        container.add(pagination);

        scenarioTable.addRangeChangeHandler(event -> pagination.rebuild(simplePager));

        simplePager.setDisplay(scenarioTable);
        pagination.clear();

        dataProvider.addDataDisplay(scenarioTable);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        loadStatistic();

        EventReceiver.getInstance().addListener(scenarioRemoteListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(scenarioRemoteListener);
    }

    private void loadStatistic() {
        RequestUtils.getInstance().getScenarioStatisticService().loadAll(
            new OnlySuccessAsyncCallback<Collection<ScenarioStatistic>>() {
                @Override
                public void onSuccess(Collection<ScenarioStatistic> result) {
                    dataProvider.setList(Lists.newArrayList(result));
                    dataProvider.flush();
                    dataProvider.refresh();
                    pagination.rebuild(simplePager);
                }
            });
    }

    interface Binder extends UiBinder<Widget, ScenarioStatisticPanel> {

    }
}
