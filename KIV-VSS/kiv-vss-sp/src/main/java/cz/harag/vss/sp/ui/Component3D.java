package cz.harag.vss.sp.ui;

import cz.harag.vss.sp.render.MapRenderer;
import cz.harag.vss.sp.Terrain;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Abstrakce celé 3D komponenty s terénem.
 * Držením levého tlačítka myši je možné mapu otáčet a pravým
 * tlačítkem přibližovat nebo oddalovat.
 *
 * @author Patrik Harag
 * @version 2019-12-15
 */
class Component3D {

    private final SubScene scene;
    private final Terrain3D terrain3D;

    Component3D(Terrain terrain, MapRenderer renderer, int w, int h) {
        Group root = new Group();
        scene = new SubScene(root, w, h, true, SceneAntialiasing.BALANCED);
        CameraManager cameraManager = new CameraManager();

        terrain3D = new Terrain3D(terrain, renderer);
        XFormWorld world = new XFormWorld();
        terrain3D.addToGroup(world);

        root.getChildren().addAll(
                world,
                cameraManager.getCameraXForm()
        );
        root.setDepthTest(DepthTest.ENABLE);
        cameraManager.init(scene);
    }

    SubScene getSubScene() {
        return scene;
    }

    Terrain3D getTerrain3D() {
        return terrain3D;
    }

    private static class XFormWorld extends Group {
        final Translate t = new Translate(0.0, 0.0, 0.0);
        final Rotate rx = new Rotate(-45, 0, 0, 0, Rotate.X_AXIS);
        final Rotate ry = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        final Rotate rz = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

        XFormWorld() {
            super();
            this.getTransforms().addAll(t, rx, ry, rz);
        }
    }

}
