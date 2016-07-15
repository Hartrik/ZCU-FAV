package cz.hartrik.ppa1.sort.app;

import javafx.concurrent.Task;

/**
 * Nástroje usnadňující synchronizaci animací.
 *
 * @version 2015-10-25
 * @author Patrik Harag
 */
public class SyncTools {

    private Task<?> task;
    private AnimatedChart chart;

    /**
     * Zahájí novou úlohu.
     *
     * @param runnable runnable
     */
    public void start(Runnable runnable) {
        start(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                runnable.run();
                return null;
            }
        });
    }

    /**
     * Zahájí novou úlohu.
     *
     * @param newTask úloha
     */
    public void start(Task<?> newTask) {
        if (task != null)
            task.cancel();  // zastavení probíhajícího úlohu

        task = newTask;

        Thread t = new Thread(task);
        t.setDaemon(true);  // vlákno nebude bránit vypnutí
        t.start();
    }

    /**
     * Zastaví probíhající úlohu.
     */
    public void stop() {
        if (task != null)
            task.cancel();
    }

    /**
     * Blokuje dokud neskončí předchozí úloha - animace.
     */
    public void waitForLastAnimation() {
        if (chart == null)
            throw new NullPointerException();

        synchronized (chart) {
            while (chart.isRunning()) {
                try {
                    chart.wait(5000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void setChart(AnimatedChart chart) {
        this.chart = chart;
    }

    public AnimatedChart getChart() {
        return chart;
    }

}