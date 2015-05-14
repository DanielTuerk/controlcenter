package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.*;
import net.wbz.moba.controlcenter.web.shared.track.model.*;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Vector3;

/**
 * Factory to create the
 * {@link net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget} implementation for
 * the type of {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 *
 * @author Daniel Tuerk
 */
public class TrackPartGeometryFactory {

    public TrackPartGeometryFactory() {
    }

    /**
     * Create and return the
     * {@link net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget} implementation
     * for the given {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart} model.
     *
     * @param trackPart {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}
     * @return {@link net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts.Basic3dTrackWidget}
     */
    public Basic3dTrackWidget<?> getTrackWidget(TrackPart trackPart) {
        int x = (trackPart.getGridPosition().getX() + 1) * Basic3dTrackWidget.GEOM_SIZE - (Basic3dTrackWidget.GEOM_SIZE / 2);
        int y = (trackPart.getGridPosition().getY() + 1) * Basic3dTrackWidget.GEOM_SIZE - (Basic3dTrackWidget.GEOM_SIZE / 2);
        Vector3 vector3 = new Vector3(x, -y, 0d);

        Basic3dTrackWidget mesh;
        if (trackPart instanceof Signal) {
            mesh = new Signal3dWidget((Signal) trackPart);
        } else if (trackPart instanceof Straight) {
            mesh = new Straight3dWidget((Straight) trackPart);
        } else if (trackPart instanceof Curve) {
            mesh = new Curve3dWidget((Curve) trackPart);
        } else if (trackPart instanceof Switch) {
            mesh = new Switch3dWidget((Switch) trackPart);
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
