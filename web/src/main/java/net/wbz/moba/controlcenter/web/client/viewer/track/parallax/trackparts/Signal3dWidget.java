package net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts;

import net.wbz.moba.controlcenter.web.server.persist.construction.track.SignalEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.SignalProxy;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.objects.Mesh;

/**
 * Widget for representation of the {@link SignalEntity} trackpart.
 *
 * @author Daniel Tuerk
 */
public class Signal3dWidget extends Straight3dWidget {

    public Signal3dWidget(SignalProxy trackPart) {
        super(trackPart);

        MeshLambertMaterial signalMaterial = new MeshLambertMaterial();
        signalMaterial.setColor(new Color(0xb2b2b2));
        signalMaterial.setShading(Material.SHADING.FLAT);

        BoxGeometry signalGeometry = new BoxGeometry(5, 5, 25);
        Mesh signalMesh = new Mesh(signalGeometry, signalMaterial);
        add(signalMesh);
    }
}
