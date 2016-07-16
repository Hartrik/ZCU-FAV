package upg;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Vstupní třída.
 *
 * @version 2016-03-31
 * @author Patrik Harag
 */
public class Main extends Application {

    public static final String TITLE = "Dělostřelec (Patrik Harag)";

    private static Map map;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(new Locale("cs", "CZ"));
        setUserAgentStylesheet(STYLESHEET_MODENA);

        map = chooseMap(getParameters().getRaw().toArray(new String[0]));
        if (map == null)
            return;

        printMapInfo(map);

        Parent root = FXMLLoader.load(getClass().getResource("Frame.fxml"));
        Scene scene = new Scene(root);

        Main.stage = stage;
        stage.setTitle(TITLE);
        stage.setMinWidth(500);
        stage.setMinHeight(350);

        stage.setWidth(750);
        stage.setHeight(600);

//        stage.getIcons().add(...));

        stage.setScene(scene);
        stage.show();
    }

    private Map chooseMap(String... params) throws IOException {
        if (params.length == 0)
            return new MapDialog().showAndWait().orElse(null);
        else
            return new MapLoader().load(Paths.get(params[0]));
    }

    public static Map getMap() {
        return map;
    }

    private void printMapInfo(Map map) {
        // 'Vypsání vzdušné vzdálenosti střelce a cíle do výstupní konzole.'
        // 'Vypsání nadmořských výšek střelce a cíle do výstupní konzole.'
        System.out.println("Vzdálenost: "
                + Math.hypot(map.targetX - map.playerX, map.targetY - map.playerY) * 10);
        System.out.println("Střelec: " + map.heightMap[map.playerY][map.playerX]);
        System.out.println("Cíl: " + map.heightMap[map.targetY][map.targetX]);
    }

    public static void setTitle(String title) {
        stage.setTitle(title);
    }

    /**
     * Vstupní metoda.
     *
     * @param args argumenty, první argument může být cesta k mapě
     */
    public static void main(String[] args) {
        launch(args);
    }

}