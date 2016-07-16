package upg;

import java.util.function.BiConsumer;

/**
 * Klasická houfnice.
 *
 * @version 2016-05-03
 * @author Patrik Harag
 */
public class Howitzer implements Weapon {

    private final Projectile projectile;

    public Howitzer(Projectile projectile) {
        this.projectile = projectile;
    }

    @Override
    public void fire(FireSetup fireSetup, BiConsumer<FireSetup, Projectile> fp) {
        fp.accept(fireSetup, projectile);
    }

}