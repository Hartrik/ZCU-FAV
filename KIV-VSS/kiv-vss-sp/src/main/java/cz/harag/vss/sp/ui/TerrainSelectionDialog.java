package cz.harag.vss.sp.ui;

import java.io.File;
import java.nio.file.Path;

import cz.harag.vss.sp.MapGenerator;
import cz.harag.vss.sp.Terrain;
import cz.harag.vss.sp.TerrainLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

/**
 *
 * @version 2019-12-23
 * @author Patrik Harag
 */
public class TerrainSelectionDialog extends Dialog<Terrain> {

    private ToggleGroup toggleGroup;
    private RadioButton rbRandom;
    private RadioButton rbSelect;

    public TerrainSelectionDialog() {
        init();
    }

    private void init() {
        setTitle("Water simulation");
        initModality(Modality.APPLICATION_MODAL);
        getDialogPane().setContent(createContent());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return getMap();
            }
            return null;
        });
    }

    private Node createContent() {
        Label labelHeader = new Label("Terrain selection");
        labelHeader.setStyle("-fx-font-weight: bold;");

        rbRandom = new RadioButton("Random");
        rbSelect = new RadioButton("Open from file");

        // nastavení RadioButton
        rbRandom.selectedProperty().set(true);
        toggleGroup = new ToggleGroup();
        rbRandom.setToggleGroup(toggleGroup);
        rbSelect.setToggleGroup(toggleGroup);

        // vložení do lyoutů
        VBox boxRB = new VBox(10, rbSelect, rbRandom);
        VBox boxMain = new VBox(10, labelHeader, boxRB);
        boxMain.setPadding(new Insets(20));
        boxMain.setPrefWidth(400);
        return boxMain;
    }

    private Terrain getMap() {
        if (rbRandom.isSelected()) {
            return MapGenerator.generateMap(100, 60, 255);
        } else {
            return loadMap();
        }
    }

    private Terrain loadMap() {
        Path file = showFileChooser();
        if (file != null) {
            String uri = file.toAbsolutePath().toUri().toString();
            return TerrainLoader.fromImage(new Image(uri), 255);
        }
        return null;
    }

    private Path showFileChooser() {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("."));
        fc.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Image with terrain", "*.png"));

        File file = fc.showOpenDialog(null);
        return (file != null) ? file.toPath() : null;
    }

}