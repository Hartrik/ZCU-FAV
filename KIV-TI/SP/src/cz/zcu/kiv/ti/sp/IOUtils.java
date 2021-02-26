package cz.zcu.kiv.ti.sp;

import java.io.*;
import java.nio.file.Path;
import java.util.function.IntConsumer;

/**
 * Zprostředkovává načítání ze souboru.
 *
 * @version 2016-11-05
 * @author Patrik Harag
 * @author Jakub Vašta
 */
public class IOUtils {

    private IOUtils() {
    }

    /**
     * Iteruje přes všechny bajty v daném souboru.
     *
     * @param path
     * @param consumer
     * @throws IOException
     */
    public static final void forEachByte(Path path, final IntConsumer consumer)
            throws IOException {

        try (FileInputStream is = new FileInputStream(path.toFile());
                BufferedInputStream bis = new BufferedInputStream(is)) {

            // nejvíc low-level načítání je pomocí InputStream::read()
            // všechny ostatní metody, jako třeba DataInputStream::readByte()
            // používají vnitřně tuto metodu

            if (Main.info)
                System.out.println("Vstupní data [hexadecimálně]:");
            int nextByte;
            while ((nextByte = bis.read()) != -1) {
                consumer.accept(nextByte);
                
                if (Main.info)
                     System.out.printf("%h\n", nextByte);
            }
        }
    }

    /**
     * Metoda na čtení ze souboru, který budeme dekódovat
     *
     * @param source cesta k souboru, který budeme dekódovat
     * @param destination dekódovaný soubor
     * @throws IOException
     */
    public static final void decode(Path source, Path destination)
            throws IOException {

        try (FileInputStream is = new FileInputStream(source.toFile());
                BufferedInputStream bis = new BufferedInputStream(is);
                DataInputStream dis = new DataInputStream(bis);) {

            try (FileOutputStream fos = new FileOutputStream(destination.toFile());
                    BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                int nextByte;

                // Načteme si velikost tabulky
                nextByte = dis.readInt();

                // Pokud nejsme na konci souboru vytvoříme tabulku
                if(nextByte != -1) {
                    int[] frequencies = new int[nextByte];

                    // Načítáme do tabulky hodnoty
                    for(int i = 0; i < nextByte; i++)
                        frequencies[i] = dis.readInt();

                    // Vytvoříme dekodér
                    Decoder decoder = new Decoder(frequencies, (encodedByte) -> {
                        try {
                            bos.write(encodedByte);
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    });

                    // Procházíme soubor až do konce a dekódujeme
                    while ((nextByte = dis.read()) != -1) {
                        decoder.decode(nextByte);
                    }
                }
            }
        }
    }

    public static final void encode(Path source, Path destination,
            String[] table, int[] frequencies) throws IOException {

        try (FileInputStream is = new FileInputStream(source.toFile());
                BufferedInputStream bis = new BufferedInputStream(is)) {

            try (FileOutputStream fos = new FileOutputStream(destination.toFile());
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    DataOutputStream dos = new DataOutputStream(bos)) {
            
                // Zapíšeme do výstupu velikost tabulky v int
                dos.writeInt(frequencies.length);
                
                // Zapíšeme vždy znak a četnost
                for (int i = 0; i < frequencies.length; i++) {
                    dos.writeInt(frequencies[i]);
                }

                String temp = "";

                if (Main.info) 
                    System.out.println("Zapisovaná data po bytech (bez inf. pro dekódování): ");
                int nextByte;
                while ((nextByte = bis.read()) != -1) {
                    temp += table[nextByte];

                    if (temp.length() >= 8) {
                       dos.writeByte(Integer.parseInt(temp.substring(0, 8), 2));
                       if (Main.info)
                            System.out.println(temp.substring(0, 8));    
                       temp = temp.substring(8);
                    }
                }

                /*
                Pokud na konci není řetěcez prázdný (to by znamenalo, že nám znaky vyšly přesně),
                tak musíme doplnit na konec tolik 0, abychom měli zarovnání na byte
                */
                if (!temp.isEmpty()) {

                    int it = 8 - temp.length();
                    for (int i = 0; i < it; i++)
                        temp += "0";

                   dos.writeByte(Integer.parseInt(temp.substring(0, 8), 2));
                   if (Main.info)
                        System.out.println(temp.substring(0, 8)); 
                }
            }
        }
    }
}