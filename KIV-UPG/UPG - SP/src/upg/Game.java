package upg;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * Obsahuje herní logiku.
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public class Game {

    public static final double SCALE = 0.1;
    private static final int PADDING = 10;
    private static final int TRAJECTORY_GAP = 100; // ms
    private static final Duration TRAJECTORY_POINT_DURATION = Duration.seconds(30);

    private final ImageView imageView;
    private final Pane region;

    private BiConsumer<Double, Double> onCollision = (x, y) -> {};
    private Runnable onTargetDestroyed = () -> {};
    private Runnable onPlayerDestroyed = () -> {};
    private Runnable onOutOfMap = () -> {};

    private final GraphicsProvider gp = new GraphicsProvider();

    private Map map;
    private MapRenderer renderer;

    public Game(ImageView imageView, Pane region) {
        this.imageView = imageView;
        this.region = region;
    }

    /**
     * Vyrenderuje mapu a zobrazí všechny prvky.
     *
     * @param map mapa
     * @param renderer renderer, který vyrenderuje mapu
     */
    public void show(Map map, MapRenderer renderer) {
        this.map = map;
        this.renderer = renderer;

        // zobrazení mapy
        repaint();
        imageView.setFitWidth(map.getWidth());
        imageView.setFitHeight(map.getHeight());

        // nastavení velikosti plátna, nechává se okraj
        region.setPrefSize(map.width + 20, map.height + 20);
        region.setMaxSize(map.width + 20, map.height + 20);

        // nastavení ukazatele úhlu
        region.getChildren().add(gp.angleIndicator);
        gp.angleIndicator.setStartX(map.playerX);
        gp.angleIndicator.setStartY(map.playerY);

        // zobrazení střelce
        gp.player.setLayoutX(map.playerX - gp.player.getFitWidth() / 2);
        gp.player.setLayoutY(map.playerY - gp.player.getFitHeight() / 2);
        region.getChildren().add(gp.player);

        // zobrazení cíle
        gp.target.setLayoutX(map.targetX - gp.target.getFitWidth() / 2);
        gp.target.setLayoutY(map.targetY - gp.target.getFitHeight() / 2);
        region.getChildren().add(gp.target);

        // zobrazení měřítka
        showScale(map, 100);
    }

    public void repaint() {
        imageView.setImage(renderer.render(map));
    }

    private void showScale(Map map, int step) {

        // horizontální měřítko
        double horY = map.height + PADDING/2;

        Line lHor = new Line(0, horY, map.width, horY);
        lHor.setStroke(Color.RED);
        region.getChildren().add(lHor);

        for (int x = 0; x <= map.width; x += step) {
            Line tick = new Line(x, map.height + 1, x, map.height + PADDING - 1);
            tick.setStroke(Color.RED);
            region.getChildren().add(tick);

            Label label = new Label((int) (x/SCALE) + " m");
            label.setTranslateX(x - (x == 0 ? 0 : 15));
            label.setTranslateY(map.height + PADDING);
            region.getChildren().add(label);
        }

        // vertikální měřítko
        double verX = -PADDING/2;

        Line lVer = new Line(verX, 0, verX, map.height);
        lVer.setStroke(Color.RED);
        region.getChildren().add(lVer);

        for (int y = 0; y <= map.height; y += step) {
            Line tick = new Line(-9, map.height - y, -1, map.height - y);
            tick.setStroke(Color.RED);
            region.getChildren().add(tick);

            if (y == 0)
                continue;

            Label label = new Label((int) (y/SCALE) + " m");
            label.setRotate(270);
            label.setTranslateX(-32);
            label.setTranslateY(map.height - y + 15);

            region.getChildren().add(label);
        }
    }

    public TrajectoryCounter createBallisticComputer(FireSetup s) {
        return createBallisticComputer(s.angle, s.elevation, s.velocity, s.wind);
    }

    public TrajectoryCounter createBallisticComputer(
            double angle, double elevation, double velocity, Vec w) {

        Vec p = new Vec(map.playerX / SCALE, map.playerY / SCALE,
                map.heightMap[map.playerY][map.playerX]);

        Vec v = Vec.countVector(angle, elevation, velocity);
        Vec g = new Vec(0, 0, -10);
        double c = 0.05;
        double dt = 0.01;

        return new TrajectoryCounter(p, v, g, w, c, dt);
    }

    public void fire(FireSetup fireSetup, Projectile projectile) {
        TrajectoryCounter bc = createBallisticComputer(fireSetup);

        Circle cProjectile = gp.createProjectile(map.playerX, map.playerY);
        region.getChildren().add(cProjectile);

        AtomicLong lastTrajectoryPontTime = new AtomicLong();
        AtomicReference<Timeline> atom = new AtomicReference<>();

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(10), (e) -> {
            Vec pos = bc.nextPosition();

            double x = (pos.x * SCALE);
            double y = (pos.y * SCALE);

            int ax = (int) (x + .5);  // souřadnice v poli; zaokrouhlené
            int ay = (int) (y + .5);

            cProjectile.setCenterX(x);
            cProjectile.setCenterY(y);

            if (ax < 0 || ay < 0 || ax >= map.width || ay >= map.height) {
                // mimo mapu
                atom.get().stop();
                region.getChildren().remove(cProjectile);
                onOutOfMap.run();

            } else if (map.heightMap[ay][ax] > pos.z) {
                // zásah - zastavení animace
                atom.get().stop();

                destroyTerrain(projectile, ax, ay);
                repaint();

                gp.updateProjectileOnImpact(cProjectile, projectile.getDamageRadius());
                updateOrder();  // kráter nesmí zastínit navigaci

                onCollision.accept(x, y);

                if (hit(x, y, map.targetX, map.targetY, projectile)) {
                    region.getChildren().remove(gp.target);
                    onTargetDestroyed.run();
                } else if (hit(x, y, map.playerX, map.playerY, projectile)) {
                    onPlayerDestroyed.run();
                }

            } else {
                // letí dál...
                long time = System.currentTimeMillis();
                if (time - lastTrajectoryPontTime.get() >= TRAJECTORY_GAP) {
                    lastTrajectoryPontTime.set(time);

                    // vykreslení bodu trajektorie jen jednou za čas...
                    int relativeHeight = (int) (pos.z - map.heightMap[ay][ax]);
                    showTrajectoryPoint(x, y, relativeHeight);

                    cProjectile.toFront();
                }
            }
        }));
        atom.set(animation);

        animation.setCycleCount(Integer.MAX_VALUE);
        animation.play();
    }

    private void showTrajectoryPoint(double x, double y, int height) {
        Circle point = gp.getTrajectoryPoint(x, y, height);
        region.getChildren().add(point);

        FadeTransition ft = new FadeTransition(TRAJECTORY_POINT_DURATION, point);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setOnFinished(e -> region.getChildren().remove(point));
        ft.play();
    }

    private void destroyTerrain(Projectile projectile, int x, int y) {
        int[][] impactMask = projectile.getImpactMask();

        int my = impactMask.length / 2;
        int mx = impactMask[0].length / 2;

        for (int iy = 0; iy < impactMask.length; iy++) {
            for (int ix = 0; ix < impactMask[0].length; ix++) {
                int ax = x + ix - mx;
                int ay = y + iy - my;

                if (ax >= 0 && ay >= 0 && ax < map.width && ay < map.height)
                    map.heightMap[ay][ax] += impactMask[iy][ix];
            }
        }
    }

    /**
     * Testuje, jestli došlo k zásahu.
     *
     * @param x horizontální pozice dopadu
     * @param y vertikální pozice dopadu
     * @param tx horizontální pozice cíle
     * @param ty vertikální pozice cíle
     * @return zásah
     */
    private boolean hit(double x, double y, double tx, double ty, Projectile p) {
        Circle destruction = new Circle(x, y, p.getDamageRadius());
        return destruction.contains(tx, ty);
    }

    /**
     * Zobrazí na mapě úhel, pod kterým se střílí.
     *
     * @param angle úhel
     */
    public void showAngle(double angle) {
        int size = 1920;
        double nx = map.getPlayerX() + size * Math.cos(-angle * Math.PI / 180);
        double ny = map.getPlayerY() + size * Math.sin(-angle * Math.PI / 180);

        gp.angleIndicator.setEndX(nx);
        gp.angleIndicator.setEndY(ny);
    }

    /**
     * Aktualizuje pořadí vykreslovaných prvků.
     */
    private void updateOrder() {
        if (gp.angleIndicator != null) gp.angleIndicator.toFront();
    }

    public Map getMap() {
        return map;
    }

    public void setOnPlayerDestroyed(Runnable onPlayerDestroyed) {
        this.onPlayerDestroyed = onPlayerDestroyed;
    }

    public void setOnTargetDestroyed(Runnable onTargetDestroyed) {
        this.onTargetDestroyed = onTargetDestroyed;
    }

    public void setOnOutOfMap(Runnable onOutOfMap) {
        this.onOutOfMap = onOutOfMap;
    }

    public void setOnCollision(BiConsumer<Double, Double> onCollision) {
        this.onCollision = onCollision;
    }

    public void setRenderer(MapRenderer renderer) {
        this.renderer = renderer;
    }

}