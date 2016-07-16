package upg;

import java.util.function.BiConsumer;

/**
 * Rozhraní pro zbraň (dělo).
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public interface Weapon {

    /**
     * Zprostředkuje střelbu.
     *
     * @param fireSetup nastavení střelby
     * @param fireProvider funkce zajišťující střelbu
     */
    public void fire(
            FireSetup fireSetup, BiConsumer<FireSetup, Projectile> fireProvider);

}