package p1;

/**
 * Třída obsahuje hashovací funkce.
 *
 * @version 2016-04-22
 * @author Patrik Harag
 */
public final class HashFunctions {

    public static int hash1(String key, int max) {
        return (int) (Long.parseLong(key) % max);
    }

    // (a b c..) % m
    public static int hash2(String key, int max) {
        int n = 0;
        for (int i = 0; i < key.length(); ++i) {
            n = (n * 128 + key.charAt(i)) % max;
        }
        return n;
    }

    public static int hash2_B(String key, int max) {
        return key.chars().reduce(0, (acc, c) -> ((acc << 7) + c) % max);
    }

    public static int hash3(String key, int max) {
        return key.charAt(0) % max;
    }

}