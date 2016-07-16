package upg;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

/**
 *
 * @version 2016-03-17
 * @author Patrik Harag
 */
public class MapDialog extends Dialog<Map> {

    private ToggleGroup toggleGroup;
    private RadioButton rbRandom;
    private RadioButton rbSelect;

    public MapDialog() {
        init();
    }

    private void init() {
        setTitle("Výběr mapy");
        initModality(Modality.APPLICATION_MODAL);
        getDialogPane().setContent(createContent());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK)
                return getMap();

            return null;
        });
    }

    private Node createContent() {
        Label labelHeader = new Label("Herní mapa");
        labelHeader.setStyle("-fx-font-weight: bold;");

        rbRandom = new RadioButton("Náhodně generovat");
        rbSelect = new RadioButton("Otevřít ze souboru");

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

    private Map getMap() {
        if (rbRandom.isSelected())
            return MapGenerator.generateMap(300, 300, 500);
        else
            return loadMap();
    }

    private Map loadMap() {
        Path file = showFileChooser();
        if (file != null) {
            try {
                return new MapLoader().load(file);

            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        return null;
    }

    private Path showFileChooser() {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("."));
        fc.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Binární soubor s terénem", "*.ter"));

        File file = fc.showOpenDialog(null);
        return (file != null) ? file.toPath() : null;
    }

}