package cz.hartrik.puzzle.net;

import java.util.function.Consumer;

/**
 * Consumer that waits for a specific response.
 *
 * @author Patrik Harag
 * @version 2017-10-07
 */
public interface MessageConsumer extends Consumer<String> {

    boolean isTemporary();


    /**
     * Creates temporary consumer.
     *
     * @param consumer
     * @return
     */
    static MessageConsumer temporary(Consumer<String> consumer) {
        return create(consumer, true);
    }

    /**
     * Creates persistent consumer.
     *
     * @param consumer
     * @return
     */
    static MessageConsumer persistent(Consumer<String> consumer) {
        return create(consumer, false);
    }

    /**
     * Creates consumer.
     *
     * @param consumer
     * @param temporary
     * @return
     */
    static MessageConsumer create(Consumer<String> consumer, boolean temporary) {
        return new MessageConsumer() {
            @Override
            public boolean isTemporary() {
                return temporary;
            }

            @Override
            public void accept(String s) {
                consumer.accept(s);
            }
        };
    }

}
