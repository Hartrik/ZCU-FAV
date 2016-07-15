package cz.hartrik.ppa1.sort.app;

import cz.hartrik.ppa1.sort.algorithm.Algorithm;
import cz.hartrik.ppa1.sort.algorithm.Algorithms;
import cz.hartrik.ppa1.sort.algorithm.Data;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * @version 2015-10-30
 * @author Patrik Harag
 */
public class MainScene {

    private final Scene scene;
    private HBox chartBox;

    private final Data<Integer> original;
    private Data<Integer> data;

    private volatile boolean paused;
    private Algorithm algorithm;

    private final SyncTools syncTools = new SyncTools();

    public MainScene(Data<Integer> data) {
        this.original = data;
        this.data = new Data<>(original.list());
        this.scene = new Scene(createContent());

        scene.getStylesheets().addAll(Resources.getCSS().toExternalForm());
    }

    private Parent createContent() {
        Chart staticChart = new Chart(600, 200, 5, data, 100);
        HBox staticChartBox = new HBox(staticChart.getPane());
        staticChartBox.getStyleClass().add("chart-panel-wrapper");

        chartBox = new HBox();
        chartBox.getStyleClass().add("chart-panel-wrapper");

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(Algorithms.getSupportedAlgorithms());
        comboBox.getSelectionModel().selectFirst();
        comboBox.valueProperty().addListener((ov, o, n) -> revert(n));

        Button bNext = new Button("", Resources.getIconNext());
        bNext.setDisable(true);
        bNext.setOnAction(e -> {
            syncTools.start(() -> {
                syncTools.waitForLastAnimation();
                algorithm.nextStep();
            });
        });

        Button bStartPause = new Button("", Resources.getIconPause());
        bStartPause.setOnAction(e -> {
            paused = !paused;
            bNext.setDisable(!paused);
            bStartPause.setGraphic(paused ? Resources.getIconPlay()
                                          : Resources.getIconPause());
            if (paused)
                syncTools.stop();
            else
                startSorting();
        });

        Button bRestart = new Button("", Resources.getIconReplay());
        bRestart.setOnAction(
                (e) -> revert(comboBox.getSelectionModel().getSelectedItem()));

        HBox toolBar = new HBox(10, comboBox, bRestart, bStartPause, bNext);
        toolBar.getStyleClass().add("toolbar");

        VBox box = new VBox(staticChartBox, chartBox, toolBar);
        box.getStyleClass().add("main-box");

        revert(comboBox.getSelectionModel().getSelectedItem());

        return box;
    }

    /**
     * Změní algoritmus a použije původní data.
     * Spustí řazení, pokud není pozastaveno.
     *
     * @param algName název řadícího algoritmu
     */
    public void revert(String algName) {
        data = new Data<>(original.list());
        updateAnimatedChart(algName);
    }

    private void updateAnimatedChart(String algName) {
        AnimatedChart animatedChart = new AnimatedChart(600, 200, 5, data, 100);
        animatedChart.setAnimationDuration(Duration.millis(333));
        syncTools.setChart(animatedChart);

        chartBox.getChildren().clear();
        chartBox.getChildren().add(animatedChart.getPane());

        algorithm = Algorithms.createByName(algName, data);
        if (!paused)
            startSorting();
    }

    /**
     * Zahájí řazení, pokračuje tam, kde přestal.
     */
    public void startSorting() {
        syncTools.start(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled() && !algorithm.isSorted()) {
                    // čekání na konec předchozí animace
                    syncTools.waitForLastAnimation();
                    algorithm.nextStep();
                }
                return null;
            }
        });
    }

    public Scene getScene() {
        return scene;
    }

}