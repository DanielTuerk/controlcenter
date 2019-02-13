package net.wbz.moba.controlcenter.web.client.scenario.route;

import com.google.gwt.dom.client.Style.HasCssName;

/**
 * Mode for the route block edit to select the track for the scenario.
 *
 * @author Daniel Tuerk
 */
public enum RouteEditMode implements HasCssName {

    /**
     * Start point of the route.
     */
    START("widget_track_selected_start"),
    /**
     * End point of the route.
     */
    END("widget_track_selected_end"),
    /**
     * Track parts to toggle between start and end point.
     */
    WAYPOINT("widget_track_selected_trackparts");

    private String cssName;

    RouteEditMode(String cssName) {
        this.cssName = cssName;
    }

    @Override
    public String getCssName() {
        return cssName;
    }

}
