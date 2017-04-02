package cz.zcu.kiv.pro.tree.app;

/**
 * Slouží k nastavení zobrazení.
 *
 * @version 2016-11-20
 * @author Patrik Harag
 */
public class ViewSettings {

    private double scale = 1;
    private double horizontalGap;
    private double verticalGap;

    public ViewSettings(double horizontalGapDef, double verticalGapDef) {
        this.horizontalGap = horizontalGapDef;
        this.verticalGap = verticalGapDef;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getHorizontalGap() {
        return horizontalGap;
    }

    public void setHorizontalGap(double horizontalGap) {
        this.horizontalGap = horizontalGap;
    }

    public double getVerticalGap() {
        return verticalGap;
    }

    public void setVerticalGap(double verticalGap) {
        this.verticalGap = verticalGap;
    }

}