package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;

/**
 * @author Daniel Tuerk
 */
public class TrackPartGeometryFactory {

    public static final int GEOM_SIZE = 25;
    public static final int GEOM_DEPTH = 5;

    private static final String TEXTURE_STRAIGHT = "img/viewer3d/straight.png";
    private static final String TEXTURE_CURVE = "img/viewer3d/curve.png";

    private final MeshBasicMaterial unknownTrackPartMaterial;

    private final BoxGeometry straightTrackPartGeometry = new BoxGeometry(GEOM_SIZE, GEOM_SIZE, GEOM_DEPTH);
    private final MeshLambertMaterial straightTrackPartMaterial;
    private final MeshLambertMaterial curveTrackPartMaterial;

    public TrackPartGeometryFactory() {

        // roll-over helpers

        unknownTrackPartMaterial = new MeshBasicMaterial();
        unknownTrackPartMaterial.setColor(new Color(0xff0000));
        unknownTrackPartMaterial.setOpacity(0.5);
        unknownTrackPartMaterial.setTransparent(true);


        straightTrackPartMaterial = new MeshLambertMaterial();
//        cubeMaterial.setColor(new Color(0xfeb74c));
//        cubeMaterial.setAmbient(new Color(0x00ff80));
        straightTrackPartMaterial.setShading(Material.SHADING.FLAT);
        straightTrackPartMaterial.setMap(new Texture(TEXTURE_STRAIGHT));


        curveTrackPartMaterial = new MeshLambertMaterial();
//        cubeMaterial.setColor(new Color(0xfeb74c));
//        cubeMaterial.setAmbient(new Color(0x00ff80));
        curveTrackPartMaterial.setShading(Material.SHADING.FLAT);
        curveTrackPartMaterial.setMap(new Texture(TEXTURE_CURVE));
    }

    public Mesh getTrackWidget(TrackPart widget) {
        int x = (widget.getGridPosition().getX() + 1) * GEOM_SIZE - (GEOM_SIZE / 2);
        int y = (widget.getGridPosition().getY() + 1) * GEOM_SIZE - (GEOM_SIZE / 2);
        Vector3 vector3 = new Vector3(x, -y, 0d);

        Mesh mesh;
        if (widget instanceof Straight) {
            mesh = new Mesh(straightTrackPartGeometry, straightTrackPartMaterial);
        } else if(widget instanceof Curve) {
            mesh = new Mesh(straightTrackPartGeometry, curveTrackPartMaterial);
        } else {
            mesh = new Mesh(straightTrackPartGeometry, unknownTrackPartMaterial);
        }

        // create cube
        mesh.setPosition(vector3);
        return mesh;
    }
}
