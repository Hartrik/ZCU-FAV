
/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class BinarySearch {

    public static boolean contains(int n, int... array) {
        int min = 0;
        int max = array.length - 1;

        while (min <= max) {
            final int mid = (min + max) / 2;
            if (array[mid] == n)
                return true;

            if (array[mid] < n) {
                min = mid + 1;
            } else {
                max = mid - 1;
            }
        }
        return false;
    }

}
