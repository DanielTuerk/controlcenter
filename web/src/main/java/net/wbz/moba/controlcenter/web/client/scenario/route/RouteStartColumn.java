package net.wbz.moba.controlcenter.web.client.scenario.route;

import com.google.gwt.user.cellview.client.TextColumn;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * Column for the route start.
 *
 * @author Daniel Tuerk
 */
class RouteStartColumn extends TextColumn<Route> {

    @Override
    public String getValue(Route route) {
        StringBuilder sb = new StringBuilder();
        if (route.getStart() != null) {
            appendBlock(sb, route.getStart().getLeftTrackBlock(), "left");
            appendBlock(sb, route.getStart().getMiddleTrackBlock(), "middle");
            appendBlock(sb, route.getStart().getRightTrackBlock(), "right");
        }
        return sb.toString();
    }

    private void appendBlock(StringBuilder sb, TrackBlock leftTrackBlock, String title) {
        if (leftTrackBlock != null) {
            sb.append(title).append(": ").append(leftTrackBlock.getDisplayValue()).append("\\n");
        }
    }
}
