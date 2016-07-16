package upg;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.stream.IntStream;

/**
 * <b>Formát dat je následující:</b> <br>
 * soubor je binární, je v něm (6 + M × N) celých 32bitových čísel
 * {@code <W><H><XS><YS><XT><YT><Z11><Z21>...<ZW1><Z12><Z22>...<ZWH>}, kde
 * {@code <W>} udává šířku mapy (počet sloupců), {@code <H>} udává výšku mapy
 * (počet řádků), {@code <XS>} a {@code <YS>} představují polohu střelce
 * (sloupec, řádek), {@code <XT>} a {@code <YT>} představují polohu cíle
 * (sloupec, řádek) a {@code <ZXY>} je nadmořská výška na pozici X,Y (v metrech).
 * Nadmořské výšky jsou tedy v souboru uloženy „po řádcích“, a to od „západu“ k
 * „východu“, řádky jsou uloženy od „severu“ k „jihu“.
 *
 * @version 2016-02-20
 * @author Patrik Harag
 */
public class MapLoader {

    /**
     * Načte mapu ze souboru.
     *
     * @param file soubor s mapou
     * @return načtená mapa
     * @throws IOException chyba při čtení souboru
     */
    public Map load(Path file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file.toFile())) {
            FileChannel inChannel = stream.getChannel();

            ByteBuffer buffer = inChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, inChannel.size());

            IntBuffer intBuffer = buffer.asIntBuffer();

            return readBuffer(intBuffer);
        }
    }

    private Map readBuffer(IntBuffer buffer) {
        Map map = new Map() {{
            width = buffer.get();
            height = buffer.get();
            playerX = buffer.get();
            playerY = buffer.get();
            targetX = buffer.get();
            targetY = buffer.get();

            heightMap = IntStream.range(0, height)
                    .mapToObj(i -> new int[width])
                    .peek(array -> buffer.get(array))
                    .toArray(int[][]::new);
        }};

        return map;
    }

}