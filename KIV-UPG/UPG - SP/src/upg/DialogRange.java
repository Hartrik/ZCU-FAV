package upg;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

/**
 * Dialog, který zobrazí závislost dostřelu na počáteční rychlosti střely při
 * zadané elevaci. Osa X zobrazuje počáteční rychlost střely, osa Y zobrazuje
 * dostřel za předpokladu bezvětří a střelby na plochém terénu (střelec i cíl
 * jsou ve stejné nadmořské výšce, mezi nimi nejsou žádné překážky).
 *
 * @version 2016-05-03
 * @author Patrik Harag
 */
public class DialogRange extends Dialog<Void> {

    public DialogRange(double elevation) {
        setTitle("Profil terénu");
        setResizable(true);

        getDialogPane().setContent(createContent(elevation));
        getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    // vytvoří obsah dialogu
    private Node createContent(double elevation) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Počáteční rychlost (m/s)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Dostřel (m)");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Závislost dostřelu na počáteční rychlosti střely");
        lineChart.setCreateSymbols(false);  // skryje kroužky

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Dostřel při elevaci " + elevation + "°");

        for (int i = 0; i < 300; i += 10) {
            series.getData().add(new XYChart.Data<>(i, countRange(elevation, i, 0.01)));
        }

        lineChart.getData().add(series);

        return lineChart;
    }

    private double countRange(double elevation, double velocity, double dt) {
        double px = 0;
        double pz = 0;
        double g = 10;
        double c = 0.05;

        double vx = velocity * Math.cos(Math.toRadians(elevation));
        double vz = velocity * Math.sin(Math.toRadians(elevation));

        do {
            // výpočet pozice
            px = px + vx * dt;
            pz = pz + vz * dt;

            // výpočet rychlosti střely
            vx = vx - (vx * c * dt);
            vz = vz - (vz * c * dt) - (g * dt);

        } while (pz > 0);

        return px;
    }

}