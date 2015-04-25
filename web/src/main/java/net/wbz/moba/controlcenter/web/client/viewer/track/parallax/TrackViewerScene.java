package net.wbz.moba.controlcenter.web.client.viewer.track.parallax;

import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import thothbot.parallax.core.client.AnimatedScene;
import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.geometries.PlaneBufferGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.LineBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Matrix4;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Line;
import thothbot.parallax.core.shared.objects.Mesh;

/**
 * Scene to display the world with an grid system.
 * The grid represents the available positions for the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrackViewerScene extends AnimatedScene {

    private TrackPartGeometryFactory trackPartGeometryFactory;

    private PerspectiveCamera camera;
    private TrackballControls controls;

    @Override
    protected void onStart() {
        trackPartGeometryFactory = new TrackPartGeometryFactory();

        // CAMERA
        camera = new PerspectiveCamera(
                45, // fov
                getRenderer().getAbsoluteAspectRation(), // aspect
                1, // near
                10000 // far
        );
        camera.getPosition().set(0, 0, 1000);

        controls = new TrackballControls(camera, getCanvas());
        controls.setRotateSpeed(1.0);
        controls.setZoomSpeed(1.2);
        controls.setPanSpeed(0.8);
        controls.setZoom(true);
        controls.setPan(true);
        controls.setStaticMoving(true);
        controls.setDynamicDampingFactor(0.15);

        // roll-over helpers
        BoxGeometry rollOverGeo = new BoxGeometry(50, 50, 50);
        MeshBasicMaterial rollOverMaterial = new MeshBasicMaterial();
        rollOverMaterial.setColor(new Color(0x00ff00));
        rollOverMaterial.setOpacity(0.5);
        rollOverMaterial.setTransparent(true);
        Mesh rollOverMesh = new Mesh(rollOverGeo, rollOverMaterial);
        getScene().add(rollOverMesh);

        // grid
        createGrid();

        PlaneBufferGeometry geometry2 = new PlaneBufferGeometry(1000, 1000);
        geometry2.applyMatrix(new Matrix4().makeRotationX(-Math.PI / 2));

        Mesh plane = new Mesh(geometry2);
        plane.setVisible(false);
        getScene().add(plane);

        // Lights
        getScene().add(new AmbientLight(0x606060));
        DirectionalLight directionalLight = new DirectionalLight(0xffffff);
        directionalLight.getPosition().set(1, 0.75, 0.5).normalize();
        getScene().add(directionalLight);
        getRenderer().setClearColor(0xf0f0f0);

        getRenderer().render(getScene(), camera);
    }

    public void centerCamera() {
        camera.lookAt(new Vector3(250, 250, 0));
    }

    private void createGrid() {
        int size = 1000, step = 25;

        Geometry geometry = new Geometry();
        for (int i = 0; i <= size; i += step) {
            geometry.getVertices().add(new Vector3(0, -i, 0));
            geometry.getVertices().add(new Vector3(size, -i, 0));

            geometry.getVertices().add(new Vector3(i, 0, 0));
            geometry.getVertices().add(new Vector3(i, -size, 0));
        }

        LineBasicMaterial material = new LineBasicMaterial();
        material.setColor(new Color(0x000000));
        material.setOpacity(0.2);
        material.setTransparent(true);

        Line line = new Line(geometry, material, Line.MODE.PIECES);
        getScene().add(line);
    }

    @Override
    protected void onUpdate(double duration) {
        this.controls.update();
        getRenderer().render(getScene(), camera);
    }


    public void addTrackWidget(TrackPart widget) {
        getScene().add(trackPartGeometryFactory.getTrackWidget(widget));
    }

}
