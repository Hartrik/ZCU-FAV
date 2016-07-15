package cz.hartrik.ppa1.sort.app;

import cz.hartrik.ppa1.sort.Rnd;
import cz.hartrik.ppa1.sort.algorithm.Data;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Vstupní třída aplikace.
 *
 * @version 2015-12-05
 * @author Patrik Harag
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        getParameters().getRaw().stream()
                .mapToInt(Integer::parseInt)
                .mapToObj(Rnd::new)
                .map(rnd -> rnd.intStream().boxed().collect(Collectors.toList()))
                .map(Data::new)
                .forEach(this::show);
    }

    public void show(Data<Integer> data) {
        Stage stage = new Stage();
        stage.setTitle("PPA1 SP - Patrik Harag");
        stage.getIcons().add(Resources.getFrameIcon());
        stage.setScene(new MainScene(data).getScene());
        stage.setMinHeight(520);
        stage.setMinWidth(660);
        stage.show();
    }

    /**
     * Vstupní metoda aplikace.
     *
     * @param args argumenty příkazové řádky
     */
    public static void main(String[] args) {
        launch(args);
    }

}
