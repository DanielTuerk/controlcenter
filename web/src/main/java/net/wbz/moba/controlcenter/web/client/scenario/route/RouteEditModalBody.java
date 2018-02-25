package net.wbz.moba.controlcenter.web.client.scenario.route;

import com.google.gwt.user.client.ui.Panel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.common.base.Optional;
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

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.SimpleTrackPanel;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSwitchWidget;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Track;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
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
    Panel trackPanelContainer;
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
        if (route != null) {
            final List<AbstractSvgTrackWidget> trackParts = trackPanel.getTrackParts();

            if (route.getStart() != null && route.getEnd() != null) {

                for (AbstractSvgTrackWidget svgTrackWidget : trackParts) {
                    svgTrackWidget.removeStyleName("widget_track_route_block");
                    svgTrackWidget.setColor(SvgTrackUtil.DEFAULT_TRACK_COLOR);
                    svgTrackWidget.repaint();
                }

                RequestUtils.getInstance().getScenarioEditorService().buildTrack(route,
                        new OnlySuccessAsyncCallback<Track>() {
                            @Override
                            public void onSuccess(Track track) {
                                for (AbstractSvgTrackWidget trackWidget : trackParts) {
                                    // draw the route for all grid positions of the track
                                    for (GridPosition gridPosition : track.getGridPositions()) {
                                        if (gridPosition.isSame(trackWidget.getTrackPart().getGridPosition())) {
                                            if (track.getTrackBlocks().contains(
                                                    trackWidget.getTrackPart().getTrackBlock())) {
                                                // set style for track blocks on the track
                                                trackWidget.addStyleName("widget_track_route_block");
                                            }
                                            trackWidget.setColor(SVGConstants.CSS_BLUE_VALUE);
                                            if (trackWidget instanceof AbstractSwitchWidget) {
                                                // paint by switch function
                                                AbstractSwitchWidget switchWidget = (AbstractSwitchWidget) trackWidget;
                                                BusDataConfiguration switchToggleFunction = switchWidget.getTrackPart()
                                                        .getToggleFunction();
                                                Optional<Boolean> functionState = track.getTrackFunctionState(
                                                        switchToggleFunction);
                                                if (functionState.isPresent()) {
                                                    switchWidget.updateFunctionState(switchToggleFunction,
                                                            functionState.get());
                                                }
                                            } else {
                                                // repaint default widgets to update the color
                                                trackWidget.repaint();
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        });
            }

            /*
             * Reinitialize the click handlers to assign the {@link RouteEditMode}.
             */
            for (HandlerRegistration trackPartClickHandler : trackPartClickHandlers) {
                trackPartClickHandler.removeHandler();
            }
            trackPartClickHandlers.clear();

            for (final AbstractSvgTrackWidget<?> trackWidget : trackParts) {
                for (RouteEditMode mode : RouteEditMode.values()) {
                    if (!Strings.isNullOrEmpty(mode.getCssName())) {
                        trackWidget.removeStyleName(mode.getCssName());
                    }
                }

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
                        for (GridPosition trackPartId : route.getWaypoints()) {
                            if (Objects.equals(trackPart.getGridPosition(), trackPartId)) {
                                trackWidget.addStyleName(RouteEditMode.WAYPOINT.getCssName());
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
            case WAYPOINT:
                updateTrackPart(trackWidget, route);
                break;
        }
        loadRoute();
    }

    private void updateTrackPart(AbstractSvgTrackWidget trackWidget, Route routeBlock) {
        AbstractTrackPart trackPart = trackWidget.getTrackPart();
        // check for existing to delete
        for (GridPosition routeBlockPart : routeBlock.getWaypoints()) {
            if (routeBlockPart.isSame(trackPart.getGridPosition())) {
                routeBlock.getWaypoints().remove(routeBlockPart);
                return;
            }
        }
        // add new waypoint
        routeBlock.addWaypoint(trackPart.getGridPosition());
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
