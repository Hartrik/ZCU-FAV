package cz.hartrik.ppa1.sort.app;

import cz.hartrik.ppa1.sort.algorithm.Data;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Graf, který kromě statického zobrazení dat dokáže sledovat jejich změny
 * pomocí animací.
 * K synchronizaci animace používá vlastní instanci.
 *
 * @version 2015-10-25
 * @author Patrik Harag
 */
public class AnimatedChart extends Chart {

    private Duration animationDuration = Duration.millis(1000);
    private volatile boolean isRunning = false;

    public AnimatedChart(int w, int h, double spacing,
            Data<Integer> data, double max) {

        super(w, h, spacing, data, max);
        data.setOnSwap(this::onSwap);
        data.setOnInsert(this::onInsert);
    }

    private void onSwap(int i, int j) {
        if (i == j) return;

        beforeAnimation();

        // příprava sloupců
        Rectangle c1 = columns.get(i);
        Rectangle c2 = columns.get(j);

        // aktualizace pořadí sloupců
        columns.set(i, c2);
        columns.set(j, c1);

        // provedení animace
        animate(new KeyValue(c1.xProperty(), c2.getX()),
                new KeyValue(c2.xProperty(), c1.getX()));
    }

    private void onInsert(int i, int j) {
        if (i == j)
            return;
        else if (j > i)
            throw new UnsupportedOperationException("Not implemented yet.");

        beforeAnimation();

        // příprava sloupců
        final Rectangle cIns = columns.get(i);
        final List<Rectangle> cMove = columns.subList(j, i + 1);

        final List<KeyValue> kvs = new LinkedList<>();

        // posun vkládaného sloupce
        kvs.add(new KeyValue(cIns.xProperty(), cMove.get(0).getX()));

        // posun ostatních sloupců směrem doprava
        for (int k = 0; k < cMove.size() - 1; k++)
            kvs.add(new KeyValue(cMove.get(k).xProperty(), cMove.get(-~k).getX()));

        // aktualizace pořadí sloupců
        columns.add(j, columns.get(i));
        columns.remove(i + 1);

        // provedení animace
        animate(kvs.toArray(new KeyValue[0]));
    }

    /**
     * Metoda, která se volá před zahájením animace.
     * Zároveň blokuje dokud neskončí předchozí animace.
     */
    private void beforeAnimation() {
        synchronized (this) {
            while (isRunning) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    return;
                }
            }
            isRunning = true;
        }
    }

    /**
     * Provede animaci, neblokuje.
     */
    private void animate(KeyValue... keyValues) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(animationDuration, keyValues));
        timeline.setOnFinished(e -> {
            synchronized (this) {
                isRunning = false;
                this.notify();
            }
        });

        timeline.play();
    }

    /**
     * Nastaví délku animace.
     *
     * @param animationDuration délka animace
     */
    public void setAnimationDuration(Duration animationDuration) {
        this.animationDuration = animationDuration;
    }

    /**
     * Vrátí {@code true}, pokud právě probíhá animace.
     *
     * @return boolean
     */
    public boolean isRunning() {
        return isRunning;
    }

}