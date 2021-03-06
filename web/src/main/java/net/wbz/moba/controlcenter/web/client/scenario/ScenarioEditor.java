package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.scenario.route.RoutePanel;
import net.wbz.moba.controlcenter.web.client.scenario.station.StationPanel;
import org.gwtbootstrap3.client.ui.Container;

/**
 * @author Daniel Tuerk
 */
public class ScenarioEditor extends Composite {
    private static Binder uiBinder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, ScenarioEditor> {
    }

    @UiField
    Container scenarioBody;
    @UiField
    Container stationBody;
    @UiField
    Container routeBody;
    @UiField
    Container scenarioStatisticBody;

    public ScenarioEditor() {
        initWidget(uiBinder.createAndBindUi(this));

        getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);

        scenarioBody.add(new ScenarioPanel());
        stationBody.add(new StationPanel());
        routeBody.add(new RoutePanel());
        scenarioStatisticBody.add(new ScenarioStatisticPanel());
    }

}
