package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.scenario.route.RouteEditPanel;
import org.gwtbootstrap3.client.ui.PanelBody;

/**
 * @author Daniel Tuerk
 */
public class ScenarioEditor extends Composite {
    private static Binder uiBinder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, ScenarioEditor> {
    }

    @UiField
    PanelBody scenarioEditBody;
    @UiField
    PanelBody stationEditBody;
    @UiField
    PanelBody routeEditBody;

    public ScenarioEditor() {
        initWidget(uiBinder.createAndBindUi(this));

        getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);

        scenarioEditBody.add(new ScenarioEditPanel());
        stationEditBody.add(new StationEditPanel());
        routeEditBody.add(new RouteEditPanel());
    }

    @Override
    protected void onLoad() {
        super.onLoad();

    }
}
