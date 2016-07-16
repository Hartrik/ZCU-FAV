package upg;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Multiple Launch Rocket System - nosič s více raketami.
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public class MLRS implements Weapon {

    private static final double ANGLE_RND = 4;
    private static final int VEL_RND = 10;

    private final int rockets;
    private final int rocketsPerSecond;
    private final Supplier<Projectile> supplier;

    /**
     * Vytvoří novou instanci.
     *
     * @param rockets počet raket v jedné salvě
     * @param rocketsPerSecond počet vystřelených raket za sekundu
     * @param supplier
     */
    public MLRS(int rockets, int rocketsPerSecond, Supplier<Projectile> supplier) {
        this.rockets = rockets;
        this.rocketsPerSecond = rocketsPerSecond;
        this.supplier = supplier;
    }

    @Override
    public void fire(FireSetup fireSetup, BiConsumer<FireSetup, Projectile> fp) {
        Duration sleep = Duration.millis((int) (1000. / rocketsPerSecond));

        Timeline salvo = new Timeline(new KeyFrame(sleep, (e) -> {
            double a = Math.random() * ANGLE_RND - ANGLE_RND/2 + fireSetup.angle;
            double v = Math.random() * VEL_RND - VEL_RND/2 + fireSetup.velocity;
            FireSetup fs = new FireSetup(a, fireSetup.elevation, v, fireSetup.wind);

            fp.accept(fs, supplier.get());
        }));
        salvo.setCycleCount(rockets);
        salvo.play();
    }

}