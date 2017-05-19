package cz.harag.uir.sp.classification;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Řeší ukládání a načítání klasifikátorů (pomocí serializace).
 *
 * @author Patrik Harag
 * @version 2017-04-30
 */
public class ClassifierIO {

    /**
     * Uložení klasifikátoru do souboru.
     *
     * @param classifier klasifikátor
     * @param file výstupní soubor
     */
    public static void save(Classifier<?> classifier, Path file) throws IOException {
        try (OutputStream out = Files.newOutputStream(file);
                 OutputStream bos = new BufferedOutputStream(out);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(classifier);
        }
    }

    /**
     * Načtení klasifikátoru ze souboru.
     *
     * @param file vstupní soubor
     * @return klasifikátor
     */
    public static Classifier<?> load(Path file) throws IOException {
        try (InputStream is = Files.newInputStream(file);
                InputStream bis = new BufferedInputStream(is);
                ObjectInputStream ois = new ObjectInputStream(bis)) {

            @SuppressWarnings("unchecked")
            Classifier<?> classifier = (Classifier<?>) ois.readObject();
            return classifier;

        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }

}
