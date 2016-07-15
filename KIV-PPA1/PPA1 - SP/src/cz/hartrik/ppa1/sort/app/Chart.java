package cz.hartrik.ppa1.sort.app;

import cz.hartrik.ppa1.sort.algorithm.Data;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * @version 2015-10-23
 * @author Patrik Harag
 */
public class Chart {

    public static final int NUMBER_OF_LINES = 10;

    private final double spacing;
    private final int w, h;

    protected final Data<Integer> data;
    protected final double maxValue;

    protected final Pane pane;
    protected final List<Rectangle> columns = new LinkedList<>();

    public Chart(int w, int h, double spacing, Data<Integer> data, double maxValue) {
        this.w = w;
        this.h = h;
        this.spacing = spacing;
        this.data = data;
        this.maxValue = maxValue;

        this.pane = createChart(w, h);
    }

    public Pane getPane() {
        return pane;
    }

    private Pane createChart(int w, int h) {
        Pane node = new Pane();
        node.setPrefSize(w, h);
        node.getStyleClass().add("chart-panel");

        createLines(node);
        createColumns(node);

        return node;
    }

    private void createLines(Pane pane) {
        int lineSpacing = h / NUMBER_OF_LINES;
        for (int i = 0; i < NUMBER_OF_LINES; i++) {
            Line line = new Line(0, i*lineSpacing, w, i*lineSpacing);
            line.getStyleClass().add("chart-line");

            pane.getChildren().add(line);
        }
    }

    private void createColumns(Pane pane) {
        double colWidth = (double) w / data.size() - spacing - spacing / data.size();
        double colHeightRatio = h / maxValue;

        int i = 0;
        for (int val : data.list()) {
            double columnHeight = colHeightRatio * val;

            Rectangle column = new Rectangle(
                    spacing + i*colWidth + i*spacing, h - columnHeight,
                    colWidth, columnHeight);

            column.getStyleClass().add("chart-column");

            columns.add(column);
            i++;
        }

        pane.getChildren().addAll(columns);
    }

}