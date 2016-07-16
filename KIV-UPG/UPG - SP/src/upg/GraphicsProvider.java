package upg;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * Poskytuje grafiku třídě {@link Game}.
 *
 * @version 2016-03-31
 * @author Patrik Harag
 */
public class GraphicsProvider {

    public final ImageView target = new ImageView("/upg/icon - target.png");
    {
        target.setFitWidth(16);
        target.setFitHeight(16);
    }

    public final ImageView player = new ImageView("/upg/icon - player.png");
    {
        player.setFitWidth(16);
        player.setFitHeight(16);
    }

    public final Line angleIndicator = new Line();
    {
        angleIndicator.setStroke(Color.CYAN);
        angleIndicator.setStrokeWidth(.5);
        angleIndicator.getStrokeDashArray().setAll(1., 4.);
    }

    // střelba

    private static final int projectileRadius = 2;

    private static final Paint projectilePaint = Color.ORANGE.darker();
    private static final Paint inpactPaint = Color.ORANGE;

    public Circle createProjectile(int x, int y) {
        return new Circle(x, y, projectileRadius, projectilePaint);
    }

    public void updateProjectileOnImpact(Circle projectile, double damageRadius) {
        projectile.setFill(inpactPaint);
        projectile.setRadius(damageRadius);
    }

    private final Image gradient = new Image("/upg/gradient.png");

    public Circle getTrajectoryPoint(double x, double y, int height) {
        if (height < 0) height = 0;

        final int iSize = 255;

        double h = (height == 0) ? 0 : Math.min(height / 2, iSize);
        int i = Math.min((int) h, iSize);

        Color color = gradient.getPixelReader().getColor(i, 0);

        Circle circle = new Circle(.75, color);
        circle.setCenterX(x);
        circle.setCenterY(y);

        return circle;
    }

}