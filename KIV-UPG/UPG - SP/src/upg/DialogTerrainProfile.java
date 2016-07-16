package upg;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

/**
 * Dialog, který zobrazí graf s profilem terénu.
 *
 * Provádí vkreslení „bokorysu“ skutečné dráhy střely a terénu pod ní
 * (se započtením vlivu větru, tj. „nad křivým půdorysem“).
 *
 * @version 2016-05-06
 * @author Patrik Harag
 */
public class DialogTerrainProfile extends Dialog<Void> {

    public DialogTerrainProfile(TrajectoryCounter bc, Map map) {
        setTitle("Profil terénu");
        setResizable(true);

        getDialogPane().setContent(createContent(bc, map));
        getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    // vytvoří obsah dialogu
    private Node createContent(TrajectoryCounter bc, Map map) {

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Vzdálenost (m)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nadmořská výška (m)");

        LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Profil terénu");
        lineChart.setCreateSymbols(false);  // skryje kroužky

        XYChart.Series<Number, Number> terrain = new XYChart.Series<>();
        terrain.setName("Terén");

        XYChart.Series<Number, Number> trajectory = new XYChart.Series<>();
        trajectory.setName("Dráha střely");

        double distance = 0;
        double lastX = bc.getPosition().x;
        double lastY = bc.getPosition().y;

        while (true) {
            Vec pos = bc.nextPosition();
            int x = (int) (pos.x * Game.SCALE + .5);
            int y = (int) (pos.y * Game.SCALE + .5);

            if (x < 0 || y < 0 || x >= map.width || y >= map.height)
                break;  // mimo mapu

            int height = map.heightMap[y][x];

            distance += Math.hypot(lastX - pos.x, lastY - pos.y);
            lastX = pos.x;
            lastY = pos.y;

            terrain.getData().add(new XYChart.Data<>(distance, height));
            trajectory.getData().add(new XYChart.Data<>(distance, pos.z));

            if (height > pos.z)
                break;  // zásah
        }

        lineChart.getData().add(terrain);
        lineChart.getData().add(trajectory);

        return lineChart;
    }

}