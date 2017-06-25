package net.wbz.moba.controlcenter.web.client.scenario.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.UnorderedList;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.SimpleTrackPanel;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSwitchWidget;
import net.wbz.moba.controlcenter.web.client.model.track.signal.AbstractSignalWidget;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteBlock;
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
    @UiField
    SimpleTrackPanel trackPanel;
    @UiField
    NavPills navRouteBlocks;
    @UiField
    RouteEditTrackToolbar trackToolbar;
    @UiField
    UnorderedList legend;
    @UiField
    TextBox txtName;
    @UiField
    InlineCheckBox cbxOneway;
    private RouteBlock activeRouteBlock;

    public RouteEditModalBody(Route route) {
        this.route = route;
        initWidget(uiBinder.createAndBindUi(this));

        addModeLegend();
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
        activeRouteBlock = null;

        txtName.setText(route.getName());
        cbxOneway.setValue(route.getOneway());

        initRouteBlocks();

        // TODO add event listener
    }

    @UiHandler("btnCreateRouteBlock")
    void onClickCreateRouteBlock(ClickEvent ignored) {
        RouteBlock routeBlock = new RouteBlock();
        routeBlock.setPosition(route.getRouteBlocks().size() + 1);
        route.getRouteBlocks().add(routeBlock);
    }

    @UiHandler("btnDeleteRouteBlock")
    void onClickDeleteRouteBlock(ClickEvent ignored) {
        route.getRouteBlocks().remove(activeRouteBlock);
        int pos = 1;
        for (RouteBlock routeBlock : route.getRouteBlocks()) {
            routeBlock.setPosition(pos);
            pos++;
        }
    }

    private void initRouteBlocks() {
        List<RouteBlock> routeBlocks = route.getRouteBlocks();
        Collections.sort(routeBlocks, new Comparator<RouteBlock>() {
            @Override
            public int compare(RouteBlock o1, RouteBlock o2) {
                return Integer.compare(o1.getPosition(), o2.getPosition());
            }
        });
        for (final RouteBlock routeBlock : routeBlocks) {
            AnchorListItem item = new AnchorListItem(String.valueOf(routeBlock.getPosition()));
            item.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    showBlock(routeBlock);
                }
            });
            navRouteBlocks.add(item);
        }
    }

    private void showBlock(final RouteBlock routeBlock) {
        activeRouteBlock = routeBlock;
        for (HandlerRegistration trackPartClickHandler : trackPartClickHandlers) {
            trackPartClickHandler.removeHandler();
        }
        trackPartClickHandlers.clear();

        for (final AbstractSvgTrackWidget trackWidget : trackPanel.getTrackParts()) {
            for (RouteEditMode mode : RouteEditMode.values()) {
                if (!Strings.isNullOrEmpty(mode.getCssName())) {
                    trackWidget.removeStyleName(mode.getCssName());
                }
            }

            trackPartClickHandlers.add(trackWidget.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {

                    handleTrackPartClick(trackWidget, routeBlock);
                }
            }, ClickEvent.getType()));

            AbstractTrackPart trackPart = trackWidget.getTrackPart();
            if (trackPart != null) {
                if (trackPart.equals(routeBlock.getStartPoint())) {
                    trackWidget.addStyleName(RouteEditMode.START.getCssName());
                } else if (Objects.equals(trackWidget.getTrackPart().getTrackBlock(),
                        routeBlock.getEndPoint())) {
                    trackWidget.addStyleName(RouteEditMode.END.getCssName());
                } else {
                    for (RouteBlockPart routeBlockPart : routeBlock.getRouteBlockParts()) {
                        if (Objects.equals(trackWidget.getTrackPart(), routeBlockPart.getSwitchTrackPart())) {
                            AbstractSwitchWidget switchTrackPart = (AbstractSwitchWidget) trackWidget;
                            switchTrackPart.addStyleName(RouteEditMode.TRACK_PART.getCssName());
                            switchTrackPart.updateFunctionState(routeBlockPart.getSwitchTrackPart().getToggleFunction(),
                                    routeBlockPart.isState());
                        }
                    }
                }
            }
        }
    }

    private void handleTrackPartClick(AbstractSvgTrackWidget trackWidget, final RouteBlock routeBlock) {
        switch (trackToolbar.getCurrentMode()) {
            case START:
                if (trackWidget instanceof AbstractSignalWidget) {
                    routeBlock.setStartPoint(((AbstractSignalWidget) trackWidget).getTrackPart());
                }
                break;
            case END:
                TrackBlock trackBlock = trackWidget.getTrackPart().getTrackBlock();
                if (trackBlock.getBlockFunction() != null
                        && trackBlock.getBlockFunction().isValid()) {
                    routeBlock.setEndPoint(trackBlock);
                }
                break;
            case TRACK_PART:
                updateTrackPart(trackWidget, routeBlock);
                break;
            case SWITCH_STATE:
                toggleSwitchState(trackWidget, routeBlock);
                break;
        }
        showBlock(routeBlock);
    }

    private void toggleSwitchState(AbstractSvgTrackWidget trackWidget, RouteBlock routeBlock) {
        if (trackWidget instanceof AbstractSwitchWidget) {
            for (RouteBlockPart routeBlockPart : routeBlock.getRouteBlockParts()) {
                if (routeBlockPart.getSwitchTrackPart().equals(((AbstractSwitchWidget) trackWidget)
                        .getTrackPart())) {
                    routeBlockPart.setState(!routeBlockPart.isState());
                }
            }
        }
    }

    private void updateTrackPart(AbstractSvgTrackWidget trackWidget, RouteBlock routeBlock) {
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
            routeBlockPart.setState(true);
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
