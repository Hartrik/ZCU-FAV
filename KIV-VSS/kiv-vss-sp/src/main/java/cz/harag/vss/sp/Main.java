package cz.harag.vss.sp;

import cz.harag.vss.sp.render.MapRenderers;
import cz.harag.vss.sp.ui.AppMainUI;
import cz.harag.vss.sp.ui.TerrainSelectionDialog;

import java.util.Locale;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Vstupní třída.
 *
 * @version 2019-11-21
 * @author Patrik Harag
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);

        Terrain terrain = chooseMap(getParameters().getRaw().toArray(new String[0]));
        if (terrain == null) {
            return;
        }

        new AppMainUI(stage, terrain, MapRenderers.FOREST);

        stage.show();
        stage.setOnCloseRequest(e -> Platform.exit());
    }

    private Terrain chooseMap(String... params) {
        if (params.length == 0) {
            return new TerrainSelectionDialog().showAndWait().orElse(null);
        } else {
            return TerrainLoader.fromImage(new Image(params[0]), 255);
        }
    }

    /**
     * Vstupní metoda.
     *
     * @param args argumenty, první argument může být cesta k mapě
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        launch(args);
    }

}