package cz.harag.vss.sp.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.harag.vss.sp.render.MapRenderer;
import cz.harag.vss.sp.Terrain;
import cz.harag.vss.sp.WaterProcessing;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

/**
 * Okno s 3D mapou.
 *
 * @version 2020-01-09
 * @author Patrik Harag
 */
public class AppMainUI {

    private final ProgressBar progressBar;
    private final Component3D component3D;
    private final WaterProcessing waterProcessing;
    private final List<Node> disabledWhenInProgress;

    public AppMainUI(Stage stage, Terrain terrain, MapRenderer renderer) {
        this.waterProcessing = new WaterProcessing(terrain, new Random());
        this.component3D = new Component3D(terrain, renderer, 600, 500);
        this.disabledWhenInProgress = new ArrayList<>();

        this.progressBar = new ProgressBar();
        progressBar.setDisable(false);
        progressBar.setProgress(0);

        VBox box = new VBox(
                10,
                createLabel("Simulation"),
                new HBox(
                        10,
                        createButtonAddWater("Water++", +1),
                        createButtonAddWater("Water--", -1),
                        createButtonSetWater("Drain")
                ),
                createButtonSimulate("Simulate 1", 1),
                createButtonSimulate("Simulate 10", 10),
                createButtonSimulate("Simulate 100", 100),
                createButtonSimulate("Simulate 1000", 1000),
                progressBar,
                createLabel("View"),
                createButtonSwitchWaterTransparency("Water visibility"),
                createButtonSwitchTerrainTransparency("Terrain visibility"),
                createButtonSwitchPedestalTransparency("Pedestal visibility"),
                createLabel("Settings"),
                createBalanceRatioInput("Water balance ratio")
        );
        box.setPadding(new Insets(10));

        SubScene subscene = component3D.getSubScene();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(subscene);
        subscene.heightProperty().bind(stackPane.heightProperty());
        subscene.widthProperty().bind(stackPane.widthProperty());

        SplitPane splitPane = new SplitPane(box, stackPane);
        splitPane.setDividerPositions(0.35);

        stage.setTitle("Water simulation");
        Scene scene = new Scene(splitPane, 700, 500, true, SceneAntialiasing.BALANCED);
        stage.setScene(scene);
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold");
        return label;
    }

    private ToggleButton createButtonSwitchWaterTransparency(String label) {
        ToggleButton button = new ToggleButton(label);
        button.setOnAction(e -> {
            if (button.isSelected()) {
                for (Box[] boxes : component3D.getTerrain3D().getWaterBlocks()) {
                    for (Box box : boxes) {
                        box.setVisible(false);
                    }
                }
            } else {
                component3D.getTerrain3D().updateWater();
            }
        });
        return button;
    }

    private ToggleButton createButtonSwitchTerrainTransparency(String label) {
        ToggleButton button = new ToggleButton(label);
        button.setOnAction(e -> {
            for (Box[] boxes : component3D.getTerrain3D().getTerrainBlocks()) {
                for (Box box : boxes) {
                    box.setVisible(!button.isSelected());
                }
            }
        });
        return button;
    }

    private ToggleButton createButtonSwitchPedestalTransparency(String label) {
        ToggleButton button = new ToggleButton(label);
        button.setOnAction(e -> {
            component3D.getTerrain3D().getPedestal().setVisible(!button.isSelected());
        });
        return button;
    }

    private VBox createBalanceRatioInput(String label) {
        TextField textField = new TextField("" + WaterProcessing.DEFAULT_BALANCE_RATIO);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                waterProcessing.setBalanceRatio(parseBalanceRatio(newValue));
                textField.setStyle("-fx-background-color: inherit;");
            } catch (Exception ignored) {
                waterProcessing.setBalanceRatio(WaterProcessing.WATER_LEVEL_DELTA);
                textField.setStyle("-fx-background-color: red;");
            }
        });
        return new VBox(5, new Label(label), textField);
    }

    private double parseBalanceRatio(String s) {
        double v = Double.parseDouble(s);
        if (v < 0 || v > 1) {
            throw new IllegalArgumentException();
        }
        return v;
    }

    private Button createButtonAddWater(String label, int level) {
        Button button = new Button(label);
        button.setOnAction(e -> {
            waterProcessing.addWater(level);
            component3D.getTerrain3D().updateWater();
        });
        disabledWhenInProgress.add(button);
        return button;
    }

    private Button createButtonSetWater(String label) {
        Button button = new Button(label);
        button.setOnAction(e -> {
            waterProcessing.setWaterTo(0);
            component3D.getTerrain3D().updateWater();
        });
        disabledWhenInProgress.add(button);
        return button;
    }

    private Button createButtonSimulate(String label, int count) {
        Button button = new Button(label);
        button.setOnAction(e -> {
            Thread thread = new Thread(() -> {
                Platform.runLater(() -> {
                    progressBar.setDisable(false);
                    progressBar.setProgress(0);
                    for (Node node : disabledWhenInProgress) {
                        node.setDisable(true);
                    }
                });
                for (int i = 0; i < count; i++) {
                    waterProcessing.simulate();
                    final int current = i + 1;
                    Platform.runLater(() -> {
                        progressBar.setProgress((double) current / count);
                    });
                }
                Platform.runLater(() -> {
                    progressBar.setDisable(true);
                    progressBar.setProgress(0);
                    component3D.getTerrain3D().updateWater();
                    for (Node node : disabledWhenInProgress) {
                        node.setDisable(false);
                    }
                });
            });
            thread.setDaemon(true);
            thread.start();
        });
        disabledWhenInProgress.add(button);
        return button;
    }

}