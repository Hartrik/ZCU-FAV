package cz.zcu.kiv.pt.sp;

/**
 * Poskytuje metody pro výpočet Levenshteinovy vzdálenosti.
 *
 * @version 2016-09-28
 * @author Patrik Harag
 */
public class LevenshteinDistance {

    private LevenshteinDistance() {
    }

    /**
     * Vypočítá Levenshteinovu vzdálenost mezi dvěma řetězci.
     *
     * @param s1 první řetězec
     * @param s2 druhý řetězec
     * @return vzdálenost
     */
    public static int compute(String s1, String s2) {
        int[][] distance = new int[s1.length() + 1][s2.length() + 1];

        for (int x = 0; x <= s1.length(); x++) {
            distance[x][0] = x;
        }

        for (int y = 1; y <= s2.length(); y++) {
            distance[0][y] = y;
        }

        for (int x = 1; x <= s1.length(); x++) {
            for (int y = 1; y <= s2.length(); y++) {
                int i1 = distance[x - 1][y] + 1;
                int i2 = distance[x][y - 1] + 1;
                int i3 = distance[x - 1][y - 1]
                        + ((s1.charAt(x - 1) == s2.charAt(y - 1)) ? 0 : 1);

                distance[x][y] = min(i1, i2, i3);
            }
        }

        return distance[s1.length()][s2.length()];
    }

    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

}