package cz.harag.uir.sp.classification

import cz.harag.uir.sp.app.ClassifierProvider
import cz.harag.uir.sp.app.ModelProvider
import cz.harag.uir.sp.classification.processing.PostprocessorCombined
import cz.harag.uir.sp.classification.processing.PostprocessorRemoveInEveryClass
import cz.harag.uir.sp.classification.processing.PostprocessorRemoveRare
import cz.harag.uir.sp.classification.processing.Preprocessors
import org.junit.Ignore
import org.junit.Test

import java.nio.file.Files
import java.nio.file.Paths

/**
 * Vytvoří tabulku do dokumentace.
 *
 * @version 2017-05-06
 * @author Patrik Harag
 */
class AccuracyTest {

    private static String LEARN_DATA = "data/learn/"
    private static String TEST_DATA = "data/test/"

    @Ignore
    @Test
    void createTable() {
        def indent = "\t"

        println "\\begin{tabular}{l|${ "l" * ClassifierProvider.values().size() }}"
        println indent + "Parametrizační alg. & " + ClassifierProvider.values()*.name.join(" & ") + " \\\\"
        println indent + "\\hline"

        ModelProvider.values().each { modelProvider ->
            String line = ClassifierProvider.values().collect { classifierProvider ->
                def classifier = createClassifier(modelProvider, classifierProvider)
                learn(classifier)
                def (total, passed) = test(classifier)

                String.format Locale.ENGLISH, "%.4f", passed / total
            }.join(" & ")

            println indent + "$modelProvider.name & $line \\\\"
        }

        println "\\end{tabular}"
    }

    Classifier createClassifier(ModelProvider modelProvider, ClassifierProvider classifierProvider) {
        def model = modelProvider.createModel()
        def postprocessor = new PostprocessorCombined(
                //new PostprocessorRemoveRare(),
                new PostprocessorRemoveInEveryClass()
        )
        model.setPostprocessor(postprocessor)
        model.setPreprocessor(Preprocessors.DEFAULT_PREPROCESSOR)

        classifierProvider.createClassifier(model)
    }

    void learn(Classifier classifier) {
        Files.list(Paths.get(LEARN_DATA))
                .map { DataLoader.loadFile(it) }
                .forEach { classifier.learn(it) }

        classifier.finalizeLearning()
    }

    def test(Classifier classifier) {

        def tests = Files.list(Paths.get(TEST_DATA))
                .collect { DataLoader.loadFile(it) }

        int total = 0
        int passed = 0

        tests.each { data ->
            String r = classifier.classify(data)
            boolean s = (r == data.category)

            total++
            if (s) passed++
        }

        return [total, passed]
    }

}
