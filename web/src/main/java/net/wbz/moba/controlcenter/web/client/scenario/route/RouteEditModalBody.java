package net.wbz.moba.controlcenter.web.client.scenario.route;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.UnorderedList;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.editor.track.SimpleTrackPanel;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSwitchWidget;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlockPart;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class RouteEditModalBody extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final Route route;

    private final List<HandlerRegistration> trackPartClickHandlers = new ArrayList<>();
    private final SimpleTrackPanel trackPanel;
    @UiField
    ScrollPanel trackPanelContainer;
    @UiField
    RouteEditTrackToolbar trackToolbar;
    @UiField
    UnorderedList legend;
    @UiField
    TextBox txtName;
    @UiField
    InlineCheckBox cbxOneway;

    public RouteEditModalBody(Route route) {
        this.route = route;
        initWidget(uiBinder.createAndBindUi(this));

        addModeLegend();

        trackPanel = new SimpleTrackPanel() {
            @Override
            protected void trackLoaded() {
                loadRoute();
            }
        };
        trackPanelContainer.add(trackPanel);
    }

    private void addModeLegend() {
        for (RouteEditMode mode : RouteEditMode.values()) {
            if (!Strings.isNullOrEmpty(mode.getCssName())) {
                Widget child = new ListItem(mode.name());
                child.addStyleName(mode.getCssName());
                child.addStyleName("route-track-legend");
                legend.add(child);
            }
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        txtName.setText(route.getName());
        cbxOneway.setValue(route.getOneway());
    }

    private void loadRoute() {
        for (HandlerRegistration trackPartClickHandler : trackPartClickHandlers) {
            trackPartClickHandler.removeHandler();
        }
        trackPartClickHandlers.clear();

        for (final AbstractSvgTrackWidget<?> trackWidget : trackPanel.getTrackParts()) {
            for (RouteEditMode mode : RouteEditMode.values()) {
                if (!Strings.isNullOrEmpty(mode.getCssName())) {
                    trackWidget.removeStyleName(mode.getCssName());
                }
            }
            if (route != null) {
                trackPartClickHandlers.add(trackWidget.addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        handleTrackPartClick(trackWidget);
                    }
                }, ClickEvent.getType()));

                AbstractTrackPart trackPart = trackWidget.getTrackPart();
                if (trackPart != null) {
                    if (trackPart.getTrackBlock() != null
                            && Objects.equals(trackPart.getTrackBlock(), route.getStart())) {
                        trackWidget.addStyleName(RouteEditMode.START.getCssName());
                    } else if (trackPart.getTrackBlock() != null
                            && Objects.equals(trackPart.getTrackBlock(), route.getEnd())) {
                        trackWidget.addStyleName(RouteEditMode.END.getCssName());
                    } else {
                        for (RouteBlockPart routeBlockPart : route.getRouteBlockParts()) {
                            if (Objects.equals(trackPart, routeBlockPart.getSwitchTrackPart())) {
                                AbstractSwitchWidget switchTrackPart = (AbstractSwitchWidget) trackWidget;
                                switchTrackPart.addStyleName(RouteEditMode.TRACK_PART.getCssName());
                                switchTrackPart.updateFunctionState(
                                        routeBlockPart.getSwitchTrackPart().getToggleFunction(),
                                        routeBlockPart.isState());
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleTrackPartClick(AbstractSvgTrackWidget trackWidget) {
        TrackBlock trackBlock = trackWidget.getTrackPart().getTrackBlock();
        // TODO start and end, same code
        switch (trackToolbar.getCurrentMode()) {
            case START:
                if (trackBlock.getBlockFunction() != null
                        && trackBlock.getBlockFunction().isValid()) {
                    route.setStart(trackBlock);
                }
                break;
            case END:
                if (trackBlock.getBlockFunction() != null
                        && trackBlock.getBlockFunction().isValid()) {
                    route.setEnd(trackBlock);
                }
                break;
            case TRACK_PART:
                updateTrackPart(trackWidget, route);
                break;
            case SWITCH_STATE:
                toggleSwitchState(trackWidget, route);
                break;
        }
        loadRoute();
    }

    private void toggleSwitchState(AbstractSvgTrackWidget trackWidget, Route routeBlock) {
        if (trackWidget instanceof AbstractSwitchWidget) {
            for (RouteBlockPart routeBlockPart : routeBlock.getRouteBlockParts()) {
                if (routeBlockPart.getSwitchTrackPart().equals(((AbstractSwitchWidget) trackWidget)
                        .getTrackPart())) {
                    routeBlockPart.setState(!routeBlockPart.isState());
                }
            }
        }
    }

    private void updateTrackPart(AbstractSvgTrackWidget trackWidget, Route routeBlock) {
        if (trackWidget instanceof AbstractSwitchWidget) {
            // check for existing to delete
            for (RouteBlockPart routeBlockPart : routeBlock.getRouteBlockParts()) {
                if (routeBlockPart.getSwitchTrackPart().equals(((AbstractSwitchWidget) trackWidget)
                        .getTrackPart())) {
                    routeBlock.getRouteBlockParts().remove(routeBlockPart);
                    return;
                }
            }
            // create new route block
            RouteBlockPart routeBlockPart = new RouteBlockPart();
            routeBlockPart.setSwitchTrackPart(((AbstractSwitchWidget) trackWidget).getTrackPart());
            routeBlockPart.setState(false);
            routeBlock.getRouteBlockParts().add(routeBlockPart);
        }
    }

    String getSelectedName() {
        return txtName.getText();
    }

    Boolean getSelectedOnewayState() {
        return cbxOneway.getValue();
    }

    interface Binder extends UiBinder<Widget, RouteEditModalBody> {
    }
}
