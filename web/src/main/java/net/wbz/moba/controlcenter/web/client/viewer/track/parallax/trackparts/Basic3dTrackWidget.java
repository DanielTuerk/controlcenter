package net.wbz.moba.controlcenter.web.client.viewer.track.parallax.trackparts;

import net.wbz.moba.controlcenter.web.client.model.track.BlockPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.ConfigurationProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartProxy;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;

/**
 * Basic widget for the 3D representation of a {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 * <p/>
 * Mesh to show a box in the grid system with an material texture. The railway are two mesh for the left and right side.
 * As implementation of {@link net.wbz.moba.controlcenter.web.client.model.track.BlockPart} the railway meshes are
 * colored by the states of the block.
 *
 * @author Daniel Tuerk
 */
public class Basic3dTrackWidget<T extends TrackPartProxy> extends Mesh implements BlockPart {

    /**
     * Size in as box in the grid.
     */
    public static final int GEOM_SIZE = 25;

    /**
     * Default depth for the mesh in the grid.
     */
    public static final int GEOM_DEPTH = 5;

    /**
     * Default color of the initial state from the {@link net.wbz.moba.controlcenter.web.client.model.track.BlockPart}.
     */
    public static final int DEFAULT_RAILWAY_COLOR = 0xededed;

    /**
     * Mesh of the left railway.
     */
    private final Mesh railwayMeshLeft;

    /**
     * Mesh of the right railway.
     */
    private final Mesh railwayMeshRight;

    /**
     * Type of {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart} which will be presented of the
     * extending 3D widget.
     */
    private final T trackPart;
    private final MeshLambertMaterial railwayMaterial;

    /**
     * Create the 3D model for the grid with the default states.
     *
     * @param trackPart {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}
     */
    public Basic3dTrackWidget(T trackPart) {
        this.trackPart = trackPart;

        /**
         * Initialize as an unknown trackpart which is override by the material of the implementing track widget.
         */
        MeshBasicMaterial unknownTrackPartMaterial = new MeshBasicMaterial();
        unknownTrackPartMaterial.setColor(new Color(0xff0000));
        unknownTrackPartMaterial.setOpacity(0.5);
        unknownTrackPartMaterial.setTransparent(true);
        setMaterial(unknownTrackPartMaterial);

        /**
         * Common track part geometry for the grid.
         */
        railwayMaterial = new MeshLambertMaterial();
        railwayMaterial.setColor(new Color(DEFAULT_RAILWAY_COLOR));
        railwayMaterial.setShading(Material.SHADING.SMOOTH);

        BoxGeometry straightTrackPartGeometry = new BoxGeometry(GEOM_SIZE, GEOM_SIZE, GEOM_DEPTH);
        setGeometry(straightTrackPartGeometry);

        /*
         * Railway left and right.
         */
        BoxGeometry railwayGeometry = new BoxGeometry(GEOM_SIZE, 1, GEOM_DEPTH + 1);
        railwayMeshLeft = new Mesh(railwayGeometry, railwayMaterial);
        railwayMeshLeft.setPosition(new Vector3(0, 3.5, 0));
        add(railwayMeshLeft);

        railwayMeshRight = new Mesh(railwayGeometry, railwayMaterial);
        railwayMeshRight.setPosition(new Vector3(0, -3.5, 0));
        add(railwayMeshRight);
    }

    public T getTrackPart() {
        return trackPart;
    }

    /**
     * Railway of the left side.
     *
     * @return {@link thothbot.parallax.core.shared.objects.Mesh}
     */
    protected Mesh getRailwayMeshLeft() {
        return railwayMeshLeft;
    }

    /**
     * Railway of the right side.
     *
     * @return {@link thothbot.parallax.core.shared.objects.Mesh}
     */
    protected Mesh getRailwayMeshRight() {
        return railwayMeshRight;
    }

    protected MeshLambertMaterial createTrackPartTexture(String texture) {
        MeshLambertMaterial material = new MeshLambertMaterial();
        material.setShading(Material.SHADING.FLAT);
        material.setMap(new Texture(texture));
        return material;
    }

    @Override
    public void freeBlock() {
        updateColorOfRailway(0x00FF00);
    }

    @Override
    public void unknownBlock() {
        updateColorOfRailway(DEFAULT_RAILWAY_COLOR);
    }

    @Override
    public void usedBlock() {
        updateColorOfRailway(0xFF0000);
    }

    /**
     * Update the color of the left and right railway.
     *
     * @param color hexcode of the color to be set
     */
    private void updateColorOfRailway(int color) {
        railwayMaterial.setColor(new Color(color));
    }

    public void updateFunctionState(Configuration configuration, boolean state) {
        ConfigurationProxy blockFunctionConfig = trackPart.getDefaultBlockFunctionConfig();
        if (blockFunctionConfig != null && blockFunctionConfig.equals(configuration)) {
            if (state == blockFunctionConfig.isBitState()) {
                usedBlock();
            } else {
                freeBlock();
            }
        }
    }

}
