package upg;

/**
 * Definuje nějaké projektily.
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public final class Projectiles {

    private Projectiles() { }

    public static final Projectile NORMAL = new Projectile(3, new int[][] {
        {   0,   0,   0,  -1,   0,   0,   0},
        {   0,   0,  -1,  -4,  -1,   0,   0},
        {   0,  -1,  -4,  -8,  -4,  -1,   0},
        {  -1,  -4,  -8, -10,  -8,  -4,  -1},
        {   0,  -1,  -4,  -8,  -4,  -1,   0},
        {   0,   0,  -1,  -4,  -1,   0,   0},
        {   0,   0,   0,  -1,   0,   0,   0},
    });

    public static final Projectile SMALL = new Projectile(2, new int[][] {
        {   0,   0,  -1,   0,   0},
        {   0,  -1,  -3,  -1,   0},
        {  -1,  -3,  -8,  -3,  -1},
        {   0,  -1,  -3,  -1,   0},
        {   0,   0,  -1,   0,   0},
    });

}