package upg;

/**
 * Třída popisující projektil.
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public class Projectile {

    private final double damageRadius;
    private final int[][] impactMask;

    public Projectile(double damageRadius, int[][] impactMask) {
        this.damageRadius = damageRadius;
        this.impactMask = impactMask;
    }

    /**
     * Vrátí masku, která se aplikuje na terén. Záporná čísla pro snížení
     * nadmořské výšky, kladné pro zvýšení.
     *
     * @return maska
     */
    public int[][] getImpactMask() {
        return impactMask;
    }

    /**
     * Při dopadu projektilu udává poloměr kruhu, uvnitř kterého dojde k
     * zasažení cíle.
     *
     * @return vzdálenost
     */
    public double getDamageRadius() {
        return damageRadius;
    }

}