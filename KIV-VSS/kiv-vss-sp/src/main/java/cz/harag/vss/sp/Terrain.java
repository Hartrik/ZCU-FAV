package cz.harag.vss.sp;

import java.util.IntSummaryStatistics;

/**
 * Představuje herní mapu.
 *
 * @author Patrik Harag
 * @version 2019-12-15
 */
public interface Terrain {

    /**
     * Vrátí šířku.
     *
     * @return šířka
     */
    int getWidth();

    /**
     * Vrátí výšku.
     *
     * @return výška
     */
    int getHeight();

    int getElevationAt(int x, int y);
    IntSummaryStatistics getElevationStatistics();

    double getWaterAt(int x, int y);
    void setWaterAt(int x, int y, double level);
    void addWaterAt(int x, int y, double inc);

}