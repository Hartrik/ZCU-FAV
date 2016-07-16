package upg;

import java.util.IntSummaryStatistics;
import java.util.function.IntFunction;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * Okno s 3D mapou. Držením levého tlačítka myši je možné mapu otáčet a pravým
 * tlačítkem přibližovat nebo oddalovat.
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public class Dialog3D extends Stage {

    final Group root = new Group();
    final XformWorld world = new XformWorld();
    final XformCamera cameraXform = new XformCamera();
    final PerspectiveCamera camera = new PerspectiveCamera(true);

    private static final double CAMERA_INITIAL_DISTANCE = -500;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    double mousePosX, mousePosY, mouseOldX, mouseOldY, mouseDeltaX, mouseDeltaY;
    double mouseFactorX, mouseFactorY;

    public Dialog3D(Map map, MapRenderer renderer) {
        setTitle("3D mapa");

        Scene scene = new Scene(root, 700, 500, true, SceneAntialiasing.BALANCED);
        createContent(scene, map, renderer);
        setScene(scene);
    }

    private void createContent(Scene scene, Map map, MapRenderer renderer) {
        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);
        buildCamera();
        buildMap(map, renderer);

        handleMouse(scene);

        scene.setCamera(camera);
        mouseFactorX = 180.0 / scene.getWidth();
        mouseFactorY = 180.0 / scene.getHeight();
    }

    private void buildCamera() {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(camera);
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    }

    private void buildMap(Map map, MapRenderer renderer) {
        double minH = 1;
        double length = 1;
        double xm = length/2. - map.width/2.;
        double ym = length/2. - map.height/2.;

        IntSummaryStatistics stats = MapRendererBase.coutStatistics(map);

        IntFunction<Color> colorFunc = (stats.getMin() == stats.getMax())
                ? (h) -> renderer.color(.5)
                : (h) -> renderer.color(MapRendererBase.relativize(h, stats));

        // podstavec
        Box padestal = new Box(map.width, map.height, 10);
        padestal.setMaterial(new PhongMaterial(Color.GRAY));
        padestal.setTranslateZ(5);
        world.getChildren().addAll(padestal);

        // hráč
        int pHeight = map.heightMap[map.playerY][map.playerX] - stats.getMin();
        Sphere player = new Sphere(length * 3/2);
        player.setMaterial(new PhongMaterial(Color.CYAN));
        player.setTranslateX(xm + map.playerX);
        player.setTranslateY(ym + map.playerY);
        player.setTranslateZ(-(minH + length / 2 + pHeight * Game.SCALE));
        world.getChildren().add(player);

        // cíl
        int tHeight = map.heightMap[map.targetY][map.targetX] - stats.getMin();
        Sphere target = new Sphere(length * 3/2);
        target.setMaterial(new PhongMaterial(Color.RED));
        target.setTranslateX(xm + map.targetX);
        target.setTranslateY(ym + map.targetY);
        target.setTranslateZ(-(minH + length / 2 + tHeight * Game.SCALE));
        world.getChildren().add(target);

        // terén
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                int h = map.heightMap[y][x];
                double rh = minH + (h - stats.getMin()) * Game.SCALE;

                PhongMaterial material = new PhongMaterial(colorFunc.apply(h));

                Box point = new Box(length, length, rh);
                point.setMaterial(material);
                point.setTranslateX(xm + x * length);
                point.setTranslateY(ym + y * length);
                point.setTranslateZ(- rh / 2);

                world.getChildren().addAll(point);
            }
        }
    }

    private void handleMouse(Scene scene) {
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            if (me.isPrimaryButtonDown()) {
                cameraXform.ry(mouseDeltaX * 180.0 / scene.getWidth());
                cameraXform.rx(-mouseDeltaY * 180.0 / scene.getHeight());
            } else if (me.isSecondaryButtonDown()) {
                camera.setTranslateZ(camera.getTranslateZ() + mouseDeltaY);
            }
        });
    }

    private class XformWorld extends Group {
        final Translate t = new Translate(0.0, 0.0, 0.0);
        final Rotate rx = new Rotate(-45, 0, 0, 0, Rotate.X_AXIS);
        final Rotate ry = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        final Rotate rz = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

        XformWorld() {
            super();
            this.getTransforms().addAll(t, rx, ry, rz);
        }
    }

    private class XformCamera extends Group {
        Point3D px = new Point3D(1.0, 0.0, 0.0);
        Point3D py = new Point3D(0.0, 1.0, 0.0);
        Rotate r;
        Transform t = new Rotate();

        XformCamera() {
            super();
        }

        public void rx(double angle) {
            r = new Rotate(angle, px);
            this.t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }

        public void ry(double angle) {
            r = new Rotate(angle, py);
            this.t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }
    }

}