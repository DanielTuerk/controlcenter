package net.wbz.moba.controlcenter.web.client.scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import org.gwtbootstrap3.client.ui.Label;

/**
 * @author Daniel Tuerk
 */
abstract class ScenarioRoutePanel extends Composite {
    private static Binder uiBinder = GWT.create(Binder.class);
    private final RouteSequence routeSequence;
    private final Scenario scenario;

    @UiField
    Label name;

    ScenarioRoutePanel(Scenario scenario, RouteSequence routeSequence) {
        this.routeSequence = routeSequence;
        this.scenario = scenario;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        name.setText(routeSequence.getRoute().getName() + " (pos: " + routeSequence.getPosition() + ")");
    }

    @UiHandler("btnMoveUp")
    void onClickMoveUp(ClickEvent ignored) {
        movePosition(true);
    }

    @UiHandler("btnMoveDown")
    void onClickMoveDown(ClickEvent ignored) {
        movePosition(false);
    }

    private void movePosition(boolean moveToTop) {
        int positionChange = moveToTop ? -1 : 1;
        int currentPos = routeSequence.getPosition();
        int newPos = currentPos + positionChange;

        if (newPos >= 0 && newPos < scenario.getRouteSequences().size()) {
            for (RouteSequence sequence : scenario.getRouteSequences()) {
                if (sequence.getPosition() == newPos) {
                    // swap position
                    sequence.setPosition(routeSequence.getPosition());
                    routeSequence.setPosition(newPos);
                }
            }
            positionChanged();
        }
    }

    @UiHandler("btnDelete")
    void onClickDelete(ClickEvent ignored) {
        deleted();
    }

    protected abstract void positionChanged();

    protected abstract void deleted();

    interface Binder extends UiBinder<Widget, ScenarioRoutePanel> {
    }
}
