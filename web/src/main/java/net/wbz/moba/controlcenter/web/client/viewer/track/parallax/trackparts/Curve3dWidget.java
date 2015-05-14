package net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts;

import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.math.Vector3;

/**
 * Widget for representation of the {@link net.wbz.moba.controlcenter.web.shared.track.model.Curve} trackpart.
 *
 * @author Daniel Tuerk
 */
public class Curve3dWidget extends Basic3dTrackWidget<Curve> {

    private static final String TEXTURE_CURVE = "img/viewer3d/curve.png";

    public Curve3dWidget(Curve trackPart) {
        super(trackPart);

        setMaterial(createTrackPartTexture(TEXTURE_CURVE));

        getRailwayMeshLeft().setPosition(new Vector3(4, -4, 0));
        getRailwayMeshLeft().rotateZ((2 * Math.PI / 360 * (45)));
        getRailwayMeshLeft().setGeometry(new BoxGeometry(GEOM_SIZE - 4, 1, GEOM_DEPTH + 1));

        getRailwayMeshRight().setPosition(new Vector3(9, -7, 0));
        getRailwayMeshRight().rotateZ((2 * Math.PI / 360 * (45)));
        getRailwayMeshRight().setGeometry(new BoxGeometry(GEOM_SIZE - 9.5, 1, GEOM_DEPTH + 1));
    }
}
