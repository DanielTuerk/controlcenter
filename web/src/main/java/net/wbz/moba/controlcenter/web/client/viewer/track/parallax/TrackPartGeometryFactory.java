package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget;
import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Curve3dWidget;
import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Signal3dWidget;
import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Straight3dWidget;
import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Turnout3dWidget;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;
import thothbot.parallax.core.shared.math.Vector3;

/**
 * Factory to create the
 * {@link net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget} implementation for
 * the type of {@link AbstractTrackPartEntity}.
 *
 * @author Daniel Tuerk
 */
public class TrackPartGeometryFactory {

    public TrackPartGeometryFactory() {
    }

    /**
     * Create and return the
     * {@link net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget} implementation
     * for the given {@link AbstractTrackPartEntity} model.
     *
     * @param trackPart {@link AbstractTrackPartEntity}
     * @return {@link net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget}
     */
    public Basic3dTrackWidget<?> getTrackWidget(AbstractTrackPart trackPart) {
        int x = (trackPart.getGridPosition().getX() + 1) * Basic3dTrackWidget.GEOM_SIZE - (Basic3dTrackWidget.GEOM_SIZE
                / 2);
        int y = (trackPart.getGridPosition().getY() + 1) * Basic3dTrackWidget.GEOM_SIZE - (Basic3dTrackWidget.GEOM_SIZE
                / 2);
        Vector3 vector3 = new Vector3(x, -y, 0d);

        Basic3dTrackWidget mesh;
        if (trackPart instanceof Signal) {
            mesh = new Signal3dWidget((Signal) trackPart);
        } else if (trackPart instanceof Straight) {
            mesh = new Straight3dWidget((Straight) trackPart);
        } else if (trackPart instanceof Curve) {
            mesh = new Curve3dWidget((Curve) trackPart);
        } else if (trackPart instanceof Turnout) {
            mesh = new Turnout3dWidget((Turnout) trackPart);
        } else {
            mesh = new Basic3dTrackWidget<>(trackPart);
        }

        mesh.setPosition(vector3);

        double angle = trackPart.getRotationAngle();
        if (angle > 0) {
            // rotate the mesh clockwise (negative z) for the rotation angle of the track part
            mesh.rotateZ((2 * Math.PI / 360 * (angle * -1)));
        }

        return mesh;
    }
}
