package net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts;

import net.wbz.moba.controlcenter.web.server.persist.construction.track.Curve;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartConfigurationEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.SwitchEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.SwitchProxy;
import thothbot.parallax.core.shared.math.Vector3;

/**
 * Widget for representation of the {@link Curve} trackpart.
 *
 * @author Daniel Tuerk
 */
public class Switch3dWidget extends Basic3dTrackWidget<SwitchProxy> {

    private static final String TEXTURE_SWITCH_LEFT = "img/viewer3d/switch_left.png";
    private static final String TEXTURE_SWITCH_RIGHT = "img/viewer3d/switch_right.png";

    public Switch3dWidget(SwitchProxy trackPart) {
        super(trackPart);

        String texture;
        if (trackPart.getCurrentDirection() == SwitchEntity.DIRECTION.LEFT) {
            texture = TEXTURE_SWITCH_LEFT;
        } else {
            texture = TEXTURE_SWITCH_RIGHT;
        }
        setMaterial(createTrackPartTexture(texture));
    }

    public void updateFunctionState(TrackPartConfigurationEntity configuration, boolean state) {
        super.updateFunctionState(configuration, state);

        if (state) {
            // switch is in curve mode
            if (getTrackPart().getCurrentDirection() == SwitchEntity.DIRECTION.RIGHT) {
                getRailwayMeshLeft().setPosition(new Vector3(0, 3.5, 0));
                getRailwayMeshLeft().rotateZ((2 * Math.PI / 360 * (45)));

                getRailwayMeshRight().setPosition(new Vector3(7.5, -7.5, 0));
                getRailwayMeshRight().rotateZ((2 * Math.PI / 360 * (45)));
            } else {
                getRailwayMeshLeft().setPosition(new Vector3(3.5, -3.5, 0));
                getRailwayMeshLeft().rotateZ((2 * Math.PI / 360 * (-45)));

                getRailwayMeshRight().setPosition(new Vector3(7.5, -7.5, 0));
                getRailwayMeshRight().rotateZ((2 * Math.PI / 360 * (-45)));
            }
        } else {
            // switch is straight
            getRailwayMeshLeft().setPosition(new Vector3(0, 3.5, 0));
            getRailwayMeshRight().setPosition(new Vector3(0, -3.5, 0));
        }
    }
}
