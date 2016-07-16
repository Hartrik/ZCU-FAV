package upg;

import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

/**
 * Kontroler hlavního okna.
 *
 * @version 2016-05-06
 * @author Patrik Harag
 */
public class FrameController implements Initializable {

    private static final double MAX_WIND_STRENGTH = 15;  // m/s

    @FXML private Pane pane;
    @FXML private ImageView imageView;

    @FXML private ImageView windIndicator;
    @FXML private Label windLabel;

    @FXML private Label rendererGrayscaleLabel;
    @FXML private Label rendererDesertLabel;

    @FXML private Spinner<Double> angleSpinner;
    @FXML private Spinner<Double> elevationSpinner;
    @FXML private Spinner<Double> velocitySpinner;

    @FXML private ComboBox<NamedWeapon> weaponComboBox;

    private Game game;
    private MapRenderer renderer = new GrayscaleMapRenderer();

    private final double windAngle = Math.random() * 360;
    private final double windStrength = Math.random() * MAX_WIND_STRENGTH;

    private final Vec wind = new Vec(
            windStrength * Math.cos(Math.toRadians(windAngle)),
            windStrength * Math.sin(Math.toRadians(windAngle)),
            0);

    private static enum GameState { WIN, LOSE, NONE }
    private GameState gameState = GameState.NONE;
    private int shots = 0;

    @Override
    public void initialize(java.net.URL url, java.util.ResourceBundle rb) {
        Map map = Main.getMap();

        game = new Game(imageView, pane);
        game.show(map, renderer);
        game.showAngle(0);
        game.setOnTargetDestroyed(() -> Platform.runLater(this::onWin));
        game.setOnPlayerDestroyed(() -> Platform.runLater(this::onGameOver));
        game.setOnCollision((x, y) -> System.out.printf("Pozice dopadu: [%f, %f]%n", x, y));
        game.setOnOutOfMap(() -> System.out.println("Střela vylétla z mapy"));

        initSpinners();
        initWindIndicator();
        initWeaponComboBox();
    }

    private void initWindIndicator() {
        int imageSize = 64;
        windIndicator.setImage(new Image("/upg/icon - arrow.png"));
        windIndicator.setMouseTransparent(true);

        // směr
        windIndicator.setRotate(windAngle);

        // síla
        double relativeStrength = windStrength / MAX_WIND_STRENGTH;
        windIndicator.setScaleX(relativeStrength);
        windIndicator.setScaleY(relativeStrength);

        // posunutí do pravého horního rohu - podle velikosti
        // pokud by byl obrázek malý, zůstal by kus od okraje
        double diff = imageSize * (1 - relativeStrength);
        windIndicator.setTranslateX(+ diff / 2);
        windIndicator.setTranslateY(- diff / 2);

        // nastavení hodnoty štítku
        windLabel.setText(String.format("Síla větru: %.2f m/s", windStrength));
    }

    /**
     * Inicializuje spinnery - výchozí, minimální, maximální hodnoty...
     * Také zajistí, že není nutné po zadání hodnoty mačkat ENTER.
     */
    private void initSpinners() {
        // spinner - azimut
        DoubleSpinnerValueFactory f1 = new DoubleSpinnerValueFactory(
                Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 10);
        f1.setConverter(new StringConverterImpl());
        angleSpinner.setValueFactory(f1);
        angleSpinner.valueProperty().addListener((ov, o, n) -> game.showAngle(n));
        angleSpinner.focusedProperty().addListener((ov, o, n) -> {
            if (!n) commitSpinner(angleSpinner);
        });

        // spinner - elevace
        DoubleSpinnerValueFactory f2 = new DoubleSpinnerValueFactory(0, 90, 45, 10);
        f2.setConverter(new StringConverterImpl());
        elevationSpinner.setValueFactory(f2);
        elevationSpinner.focusedProperty().addListener((ov, o, n) -> {
            if (!n) commitSpinner(elevationSpinner);
        });

        // spinner - rychlost
        DoubleSpinnerValueFactory f3 = new DoubleSpinnerValueFactory(0, 1000, 150, 10);
        f3.setConverter(new StringConverterImpl());
        velocitySpinner.setValueFactory(f3);
        velocitySpinner.focusedProperty().addListener((ov, o, n) -> {
            if (!n) commitSpinner(velocitySpinner);
        });
    }

    private void initWeaponComboBox() {
        weaponComboBox.getItems().setAll(
            new NamedWeapon() {{
                name = "houfnice";
                weapon = new Howitzer(Projectiles.NORMAL);
            }},
            new NamedWeapon() {{
                name = "malá houfnice";
                weapon = new Howitzer(Projectiles.SMALL);
            }},
            new NamedWeapon() {{  // 10 projektilů v jedné sekundě
                name = "MLRS";
                weapon = new MLRS(10, 10, () -> (Math.random() > .8)
                        ? Projectiles.NORMAL
                        : Projectiles.SMALL);
            }}
        );

        weaponComboBox.getSelectionModel().selectFirst();
    }

    @FXML protected void rendererDefault() {
        game.setRenderer(renderer = new GrayscaleMapRenderer());
        game.repaint();
    }

    @FXML protected void rendererDesert() {
        game.setRenderer(renderer = new GradientMapRenderer(
                new Image("/upg/gradient - desert 2.png")));
        game.repaint();
    }

    @FXML protected void fire() {
        FireSetup fs = new FireSetup(
                angleSpinner.getValue(), elevationSpinner.getValue(),
                velocitySpinner.getValue(), wind);

        Weapon weapon = weaponComboBox.getSelectionModel().getSelectedItem().weapon;
        weapon.fire(fs, (fireSetup, projectile) -> {
            shots++;
            game.fire(fireSetup, projectile);
        });
    }

    @FXML protected void showTerrainProfile() {
        TrajectoryCounter bc = game.createBallisticComputer(
                angleSpinner.getValue(), elevationSpinner.getValue(),
                velocitySpinner.getValue(), wind);

        DialogTerrainProfile dialog = new DialogTerrainProfile(bc, game.getMap());
        dialog.showAndWait();
    }

    @FXML protected void showRange() {
        DialogRange dialog = new DialogRange(elevationSpinner.getValue());
        dialog.showAndWait();
    }

    @FXML protected void show3dMap() {
        Dialog3D dialog = new Dialog3D(game.getMap(), renderer);
        dialog.initOwner(pane.getScene().getWindow());
        dialog.show();
    }

    private synchronized void onWin() {
        if (gameState != GameState.NONE)
            return;  // když se hráč jenou zabije, už nemůže vyhrát

        gameState = GameState.WIN;
        Main.setTitle(Main.TITLE + " - výhra");
        showGameStateDialog("Vítězství!", "Dosažené score: " + (1000 / shots),
                AlertType.NONE);
    }

    private synchronized void onGameOver() {
        if (gameState != GameState.NONE)
            return;

        gameState = GameState.LOSE;
        Main.setTitle(Main.TITLE + " - prohra");
        showGameStateDialog("Prohra!", "Zabil tě tvůj vlastní projektil.",
                AlertType.ERROR);
    }

    /** Zobrazí dialog - výhra / prohra */
    private void showGameStateDialog(String header, String text, AlertType type) {
        Alert alert = new Alert(type);
        alert.initOwner(pane.getScene().getWindow());
        alert.setTitle(Main.TITLE);
        alert.setHeaderText(header);
        alert.setContentText(text);

        ButtonType bExit = new ButtonType("Ukončit hru", ButtonData.OTHER);
        ButtonType bCancel = new ButtonType("Pokračovat", ButtonData.CANCEL_CLOSE);
            // pro testovací účely je možné pokračovat...

        alert.getButtonTypes().setAll(bCancel, bExit);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == bExit){
            Platform.exit();
        }
    }

    /**
     * Uloží aktuální hodnotu spinneru - stejné, jako když se zmáčkne ENTER.
     *
     * @param <T> typ spinneru
     * @param spinner spinner
     */
    private <T> void commitSpinner(Spinner<T> spinner) {
        if (!spinner.isEditable()) return;
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }

    private static class NamedWeapon {
        Weapon weapon;
        String name;

        @Override
        public String toString() {
            return name;
        }
    }

}