package net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts;

import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import thothbot.parallax.core.shared.math.Vector3;

/**
 * Widget for representation of the {@link net.wbz.moba.controlcenter.web.shared.track.model.Straight} trackpart.
 *
 * @author Daniel Tuerk
 */
public class Straight3dWidget extends Basic3dTrackWidget<Straight> {

    private static final String TEXTURE_STRAIGHT = "img/viewer3d/straight.png";

    public Straight3dWidget(Straight trackPart) {
        super(trackPart);

        setMaterial(createTrackPartTexture(TEXTURE_STRAIGHT));

        getRailwayMeshLeft().setPosition(new Vector3(0, 3.5, 0));
        getRailwayMeshRight().setPosition(new Vector3(0, -3.5, 0));
    }
}
