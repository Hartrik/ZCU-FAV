package cz.zcu.kiv.ti.sp;

/**
 * Jednoduché stopky, které slouží hlavně k testování.
 *
 * @version 2016-11-27
 * @author Patrik Harag
 */
public class StopWatch {

    @FunctionalInterface
    public static interface ThrowableRunnable {
        void run() throws Exception;
    }

    // --- statické metody ---

    /**
     * Vytvoří stopky, spustí metodu {@link ThrowableRunnable#run()} a vrátí
     * stopky s výsledným časem.
     *
     * @param runnable instance s metodou <code>run</code>
     * @return stopky s časem
     */
    public static final StopWatch measure(ThrowableRunnable runnable) {
        StopWatch watch = new StopWatch();
        watch.start();

        try {
            runnable.run();
        } catch (Exception ex) {
            // propagace výjimky
            throw new RuntimeException(ex.getMessage(), ex);
        }

        watch.stop();
        return watch;
    }

    /**
     * Spustí metodu {@link ThrowableRunnable#run()} a výsledný čas v milisekundách
     * vypíše na standardní výstup v určitém formátu. <p>
     * Ukázka:
     * <pre>{@code
     *  StopWatch.measureAndPrint("Načtení: %d ms", () -> {
     *      ...
     *  });}</pre>
     *
     * @param format formát, ve kterém bude výsledný čas zapsán
     * @param runnable instance s metodou <code>run</code>
     * @see String#format(String, Object...)
     */
    public static final void measureAndPrint(String format, ThrowableRunnable runnable) {
        String text = String.format(format, measure(runnable).getMillis());
        System.out.println(text);
    }

    // --- třída ---

    private boolean running = false;
    private long start = 0;
    private long stop = 0;

    /** Spustí stopky */
    public void start() {
        if (!running) {
            running = true;
            start = System.currentTimeMillis();
        }
    }

    /** Zastaví stopky. */
    public void stop() {
        stop = System.currentTimeMillis();
        running = false;
    }

    public long getMillis() {
        return (running ? System.currentTimeMillis() : stop) - start;
    }

    public long getSec()  { return getMillis() / 1000; }
    public long getMin()  { return getSec()    / 60; }
    public long getHour() { return getMin()    / 60; }
    public long getDay()  { return getHour()   / 24; }

}