package cz.harag.vss.sp.ui;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

/**
 * Obstarává práci s kamerou.
 *
 * @author Patrik Harag
 * @version 2019-12-15
 */
class CameraManager {

    private static final double CAMERA_INITIAL_DISTANCE = -500;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    private final XFormCamera cameraXForm = new XFormCamera();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private double mousePosX, mousePosY, mouseOldX, mouseOldY, mouseDeltaX, mouseDeltaY;

    CameraManager() {
        cameraXForm.getChildren().add(camera);
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    }

    XFormCamera getCameraXForm() {
        return cameraXForm;
    }

    void init(SubScene scene) {
        handleMouse(scene);
        scene.setCamera(camera);
    }

    private void handleMouse(SubScene scene) {
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
                cameraXForm.ry(mouseDeltaX * 180.0 / scene.getWidth());
                cameraXForm.rx(-mouseDeltaY * 180.0 / scene.getHeight());
            } else if (me.isSecondaryButtonDown()) {
                camera.setTranslateZ(camera.getTranslateZ() + mouseDeltaY);
            }
        });
    }

    private static class XFormCamera extends Group {
        private Point3D px = new Point3D(1.0, 0.0, 0.0);
        private Point3D py = new Point3D(0.0, 1.0, 0.0);
        private Rotate r;
        private Transform t = new Rotate();

        private XFormCamera() {
            super();
        }

        private void rx(double angle) {
            this.r = new Rotate(angle, px);
            this.t = t.createConcatenation(r);
            getTransforms().clear();
            getTransforms().addAll(t);
        }

        private void ry(double angle) {
            this.r = new Rotate(angle, py);
            this.t = t.createConcatenation(r);
            getTransforms().clear();
            getTransforms().addAll(t);
        }
    }

}
