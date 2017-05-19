package cz.harag.uir.sp.app;

import cz.harag.uir.sp.classification.*;
import cz.harag.uir.sp.classification.model.Model;
import cz.harag.uir.sp.classification.processing.PostprocessorCombined;
import cz.harag.uir.sp.classification.processing.PostprocessorRemoveInEveryClass;
import cz.harag.uir.sp.classification.processing.PostprocessorRemoveRare;
import cz.harag.uir.sp.classification.processing.Preprocessors;
import cz.hartrik.common.Exceptions;
import cz.hartrik.common.io.Resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Vstupní třída.
 *
 * @author Patrik Harag
 * @version 2017-05-09
 */
public class Main {

    private static final String HELP_FILE = "/help.txt";

    /**
     * <p>
     *     <strong>Pro natrénování modelu</strong> <br>
     *     Spušnění s parametry: <br>
     *     {@code trénovací_množina, testovací_množina, parametrizační_algoritmus,
     *     klasifikační_algoritmus, název_modelu}
     * </p>
     *
     * <p>
     *     <strong>Pro zobrazení GUI</strong> <br>
     *     Spušnění s parametry: <br>
     *     {@code název_modelu}
     * </p>
     *
     * @param args argumenty
     */
    public static void main(String... args) {
//        args = new String[]{
//                "data/learn",
//                "data/test",
//                "TF",
//                "naive-bayes",
//                "classifier.dat"
//        };

        try {
            if (args.length == 1)
                useClassifier(args[0]);
            else if (args.length == 5)
                trainClassifier(args);
            else
                printHelp();

        } catch (Exception e) {
            System.out.println("Došlo k neočekávané chybě:");
            System.err.println(e.toString());
        }
    }

    private static void trainClassifier(String... args) throws IOException {
        Path folderTrainingSet = Paths.get(args[0]);
        Path folderTestSet = Paths.get(args[1]);
        String parAlgorithm = args[2];
        String classAlgorithm = args[3];
        Path output = Paths.get(args[4]);

        Model<?> model = createModel(parAlgorithm);
        if (model == null) {
            System.err.printf("Neznámý parametrizační algoritmus: '%s'%n", parAlgorithm);
            return;
        }

        Classifier<?> classifier = createClassifier(classAlgorithm, model);
        if (classifier == null) {
            System.err.printf("Neznámý klasifikační algoritmus: '%s'%n", classAlgorithm);
            return;
        }

        // nastavení pre-processingu

        PostprocessorCombined postprocessor = new PostprocessorCombined(
                //new PostprocessorRemoveRare(),
                new PostprocessorRemoveInEveryClass()
        );

        model.setPreprocessor(Preprocessors.DEFAULT_PREPROCESSOR);
        model.setPostprocessor(postprocessor);

        // natrénování

        System.out.println("Trénování...");
        System.out.println("- načítání dat...");
        System.out.println();

        Files.list(folderTrainingSet).forEach(Exceptions.uncheckedConsumer(f -> {
            System.out.println("<< " + f.getFileName());

            Data d = DataLoader.loadFile(f);
            classifier.learn(d);
        }));

        System.out.println();
        System.out.println("- vytváření modelu...");

        classifier.finalizeLearning();

        // uložení

        System.out.println();
        System.out.println("Ukládání...");

        ClassifierIO.save(classifier, output);

        // testování

        System.out.println();
        System.out.println("Testování...");
        System.out.println();

        AtomicInteger passed = new AtomicInteger(0);
        AtomicInteger total = new AtomicInteger(0);

        Files.list(folderTestSet).forEach(Exceptions.uncheckedConsumer(f -> {
            System.out.println("<< " + f.getFileName());

            Data d = DataLoader.loadFile(f);

            String result = classifier.classify(d);
            if (result.equals(d.getCategory()))
                passed.incrementAndGet();

            total.incrementAndGet();
        }));

        System.out.println();
        if (total.get() != 0) {
            System.out.printf("Accuracy: %d/%d = %f%n",
                    passed.get(), total.get(), (double) passed.get()/total.get());
        } else {
            System.out.println("Žádná testovací data");
        }
    }

    private static Classifier<?> createClassifier(String name, Model<?> model) {
        name = name.toLowerCase().trim();

        for (ClassifierProvider provider : ClassifierProvider.values())
            if (provider.getName().equals(name))
                return provider.createClassifier(model);

        return null;
    }

    private static Model<?> createModel(String name) {
        name = name.toLowerCase().trim();

        for (ModelProvider provider : ModelProvider.values())
            if (provider.getName().equals(name))
                return provider.createModel();

        return null;
    }

    // ---

    private static void useClassifier(String arg) throws IOException {
        App.launchApp(ClassifierIO.load(Paths.get(arg)));
    }

    // ---

    private static void printHelp() {
        String text = Resources.text(HELP_FILE);
        System.out.println(text);
    }

}
