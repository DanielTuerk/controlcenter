package net.wbz.moba.controlcenter.web.client.scenario.route;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.wbz.moba.controlcenter.web.client.components.Popover;
import net.wbz.moba.controlcenter.web.client.editor.track.SimpleTrackPanel;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractCurveWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractStraightWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractTurnoutWidget;
import net.wbz.moba.controlcenter.web.client.model.track.block.AbstractBlockStraightWidget;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.Track;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * Content for the {@link RouteEditModal} to configure the {@link Route}.
 *
 * @author Daniel Tuerk
 */
public class RouteEditModalBody extends Composite {

    private static final String TRACK_WIDGET_ID = "routeTrackPanel";
    private static final String ROUTE_TRACK_COLOR = SVGConstants.CSS_BLUE_VALUE;
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

    RouteEditModalBody(Route route) {
        this.route = route;
        initWidget(uiBinder.createAndBindUi(this));

        addModeLegend();

        trackPanel = new SimpleTrackPanel() {
            @Override
            protected void trackLoaded() {
                loadRoute();
            }

            @Override
            protected Widget initTrackWidget(AbstractSvgTrackWidget trackWidget) {
                String trackWidgetStyleName = trackWidget.getTrackWidgetStyleName();
                if (trackWidgetStyleName != null) {
                    trackWidget.addStyleName(trackWidgetStyleName);
                }
                return super.initTrackWidget(trackWidget);
            }
        };
        trackPanel.getElement().setId(TRACK_WIDGET_ID);
        trackPanelContainer.add(trackPanel);

        trackToolbar.addListener(newMode -> {
            final List<AbstractSvgTrackWidget> trackParts = trackPanel.getTrackParts();
            trackParts.forEach(trackPart -> {
                trackPart.setEnabled(true);
                switch (newMode) {
                    case START:
                        trackPart.setEnabled(trackPart instanceof AbstractBlockStraightWidget);
                        break;
                    case END:
                        trackPart.setEnabled(trackPart instanceof AbstractBlockStraightWidget);
                        break;
                    case WAYPOINT:
                        trackPart.setEnabled(trackPart instanceof AbstractStraightWidget
                            || trackPart instanceof AbstractCurveWidget);
                        break;
                }
            });

        });
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

                if (route.hasTrack()) {
                    final Track track = route.getTrack();
                    for (AbstractSvgTrackWidget trackWidget : trackParts) {
                        // draw the route for all grid positions of the track
                        for (GridPosition gridPosition : track.getGridPositions()) {
                            if (gridPosition.isSame(trackWidget.getTrackPart().getGridPosition())) {
                                if (trackWidget instanceof AbstractBlockStraightWidget) {
                                    BlockStraight blockStraight = ((AbstractBlockStraightWidget) trackWidget)
                                        .getTrackPart();
                                    // set style for track blocks on the track
                                    track.getTrackBlocks().stream().filter(
                                        trackBlock -> trackBlock.equals(blockStraight.getLeftTrackBlock())
                                            || trackBlock.equals(blockStraight.getMiddleTrackBlock())
                                            || trackBlock.equals(blockStraight.getRightTrackBlock())).findAny()
                                        .ifPresent(y -> trackWidget.addStyleName("widget_track_route_block"));
                                }

                                trackWidget.setColor(ROUTE_TRACK_COLOR);
                                if (trackWidget instanceof AbstractTurnoutWidget) {
                                    // paint by switch function
                                    AbstractTurnoutWidget switchWidget = (AbstractTurnoutWidget) trackWidget;
                                    BusDataConfiguration switchToggleFunction = switchWidget.getTrackPart()
                                        .getToggleFunction();
                                    Optional<Boolean> functionState = track
                                        .getTrackFunctionState(switchToggleFunction);
                                    functionState.ifPresent(
                                        on -> switchWidget.updateFunctionState(switchToggleFunction, on));
                                } else {
                                    // repaint default widgets to update the color
                                    trackWidget.repaint();
                                }
                                break;
                            }
                        }
                    }
                }
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

                trackPartClickHandlers
                    .add(trackWidget.addDomHandler(event -> handleTrackPartClick(trackWidget), ClickEvent.getType()));

                AbstractTrackPart trackPart = trackWidget.getTrackPart();
                if (trackPart != null) {

                    if (trackPart instanceof BlockStraight) {
                        BlockStraight blockStraight = (BlockStraight) trackPart;

                        if (blockStraight.equals(route.getStart())) {
                            trackWidget.addStyleName(RouteEditMode.START.getCssName());
                        }

                        Set<TrackBlock> collect = Stream.of(blockStraight.getLeftTrackBlock(),
                            blockStraight.getMiddleTrackBlock(),
                            blockStraight.getRightTrackBlock())
                            .filter(Objects::nonNull).collect(Collectors.toSet());
                        if (collect.contains(route.getEnd())) {
                            trackWidget.addStyleName(RouteEditMode.END.getCssName());
                        }
                    }

                    //waypoints
                    for (GridPosition waypointGridPos : route.getWaypoints()) {
                        if (Objects.equals(trackPart.getGridPosition(), waypointGridPos)) {
                            trackWidget.addStyleName(RouteEditMode.WAYPOINT.getCssName());
                        }
                    }
                }
            }
        }
    }

    private void handleTrackPartClick(AbstractSvgTrackWidget trackWidget) {
        if (trackWidget instanceof AbstractBlockStraightWidget) {
            // for start or end a track block is needed
            AbstractBlockStraightWidget blockStraightWidget = (AbstractBlockStraightWidget) trackWidget;
            if (trackToolbar.getCurrentMode() == RouteEditMode.START) {
                route.setStart(blockStraightWidget.getTrackPart());
            } else if (trackToolbar.getCurrentMode() == RouteEditMode.END) {
                selectTrackBlock(route, blockStraightWidget);
            }
        } else if (trackToolbar.getCurrentMode() == RouteEditMode.WAYPOINT) {
            updateTrackPart(trackWidget, route);
        }

        buildTrackAndReloadRoute();
    }

    private void buildTrackAndReloadRoute() {
        RequestUtils.getInstance().getScenarioEditorService().buildTrack(route, new AsyncCallback<Track>() {
            @Override
            public void onFailure(Throwable caught) {
                route.setTrack(null);
                loadRoute();
            }

            @Override
            public void onSuccess(Track track) {
                route.setTrack(track);
                loadRoute();
            }
        });
    }

    private void selectTrackBlock(Route route, AbstractBlockStraightWidget blockStraightWidget) {
        if (!blockStraightWidget.getTrackPart().getAllTrackBlocks().isEmpty()) {
            Popover popover = new Popover(TRACK_WIDGET_ID, blockStraightWidget, "Select Block");
            addBlockButton(route, popover, blockStraightWidget.getTrackPart().getLeftTrackBlock());
            addBlockButton(route, popover, blockStraightWidget.getTrackPart().getMiddleTrackBlock());
            addBlockButton(route, popover, blockStraightWidget.getTrackPart().getRightTrackBlock());
            popover.toggle();
        } else {
            Notify.notify("no track block available");
        }
    }

    private void addBlockButton(Route route, Popover popover, TrackBlock trackBlock) {
        if (trackBlock != null) {
            popover.addContent(new Button(trackBlock.getName(), event -> {
                route.setEnd(trackBlock);
                popover.hide();
                buildTrackAndReloadRoute();
            }));
        }
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
