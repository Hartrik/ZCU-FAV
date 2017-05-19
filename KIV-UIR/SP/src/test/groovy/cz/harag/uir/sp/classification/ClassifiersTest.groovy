package cz.harag.uir.sp.classification

import cz.harag.uir.sp.classification.model.*
import cz.harag.uir.sp.classification.processing.PostprocessorCombined
import cz.harag.uir.sp.classification.processing.PostprocessorRemoveInEveryClass
import cz.harag.uir.sp.classification.processing.PostprocessorRemoveRare
import cz.harag.uir.sp.classification.processing.Preprocessors
import org.junit.Ignore
import org.junit.Test

import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 * @version 2017-05-03
 * @author Patrik Harag
 */
class ClassifiersTest {

    private static String LEARN_DATA = "data/learn/"
    private static String TEST_DATA = "data/test/"

    private static boolean enableSavingLoading = true
    private static String TEMP_FILE = "classifier.dat"

    // KNN

    @Ignore
    @Test
    void testKNN_Bin() {
        testKNN(new BinVectorModel())
    }

    @Ignore
    @Test
    void testKNN_Bin_Reduced() {
        testKNN(new BinVectorReducedModel())
    }

    @Ignore
    @Test
    void testKNN_Tf() {
        testKNN(new TfVectorModel())
    }

    @Ignore
    @Test
    void testKNN_Tf_Reduced() {
        testKNN(new TfVectorReducedModel())
    }

    @Ignore
    @Test
    void testKNN_TfIdf() {
        testKNN(new TfIdfVectorModel())
    }

    @Ignore
    @Test
    void testKNN_TfIdf_Reduced() {
        testKNN(new TfIdfVectorReducedModel())
    }

    // BAYES

    @Ignore
    @Test
    void testBayes_Bin() {
        testBayes(new BinVectorModel())
    }

    @Ignore
    @Test
    void testBayes_Bin_Reduced() {
        testBayes(new BinVectorReducedModel())
    }

    @Ignore
    @Test
    void testBayes_Tf() {
        testBayes(new TfVectorModel())
    }

    @Ignore
    @Test
    void testBayes_Tf_Reduced() {
        testBayes(new TfVectorReducedModel())
    }

    @Ignore
    @Test
    void testBayes_TfIdf() {
        testBayes(new TfIdfVectorModel())
    }

    @Ignore
    @Test
    void testBayes_TfIdf_Reduced() {
        testBayes(new TfIdfVectorReducedModel())
    }

    // --- ---

    void testKNN(Model model) {
        def postprocessor = new PostprocessorCombined(
                //new PostprocessorRemoveRare(),
                new PostprocessorRemoveInEveryClass()
        )

        model.setPreprocessor(Preprocessors.DEFAULT_PREPROCESSOR)
        model.setPostprocessor(postprocessor)

        def classifier = new NearestNeighbourClassifier(model)

        test(classifier)
    }

    void testBayes(Model model) {
        def postprocessor = new PostprocessorCombined(
                //new PostprocessorRemoveRare(),
                new PostprocessorRemoveInEveryClass()
        )

        model.setPreprocessor(Preprocessors.DEFAULT_PREPROCESSOR)
        model.setPostprocessor(postprocessor)

        def classifier = new NaiveBayesClassifier(model)

        test(classifier)
    }

    void test(Classifier classifier) {
        // learning

        Files.list(Paths.get(LEARN_DATA))
                .map { DataLoader.loadFile(it) }
                .forEach { classifier.learn(it) }

        classifier.finalizeLearning()

        // saving and loading

        if (enableSavingLoading) {
            // uloží a znovu načte klasifikátor
            def temp = Paths.get(TEMP_FILE)
            ClassifierIO.save(classifier, temp)
            classifier = ClassifierIO.load(temp)
        }

        // testing

        def tests = Files.list(Paths.get(TEST_DATA))
                // .limit(100)
                .collect { DataLoader.loadFile(it) }
                .groupBy { Data d -> d.category}

        int total = 0
        int totalPassed = 0

        tests.each { k, v ->
            int passed = 0
            String out = ""

            v.each {
                String r = classifier.classify(it)
                boolean s = (r == it.category)

                total++
                if (s) passed++

                out += "  " + (s ? "ok" : "miss ($r)") + "\n"
            }
            println "${k} | ${passed} / ${v.size()} = ${passed / v.size()}"
            println out

            totalPassed += passed
        }

        println ""
        println "passed / total = $totalPassed / $total = ${totalPassed / total}"
    }

}
