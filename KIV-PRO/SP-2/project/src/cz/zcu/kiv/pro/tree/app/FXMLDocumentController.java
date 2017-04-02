package cz.zcu.kiv.pro.tree.app;

import cz.harag.utils.Exceptions;
import cz.harag.utils.Resources;
import cz.zcu.kiv.pro.tree.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javax.script.ScriptException;


/**
 *
 * @version 2016-11-21
 * @author Patrik Harag
 */
public class FXMLDocumentController implements Initializable {

    public static FXMLDocumentController instance;

    @FXML
    private TextArea input;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ComboBox<LayoutManager.LayoutProvider> comboBox;

    @FXML
    private ListView<ExampleManager.Example> examples;

    int NODE_WIDTH = 28;
    int NODE_HEIGHT = 22;

    private final ViewSettings settings = new ViewSettings(20, 20);
    private Tree active;
    private Pane pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        initExamles();
        initLayouts();
        initCentering();

        // výchozí graf
        input.setText(Resources.text("example.js", getClass(), StandardCharsets.UTF_8));
        Exceptions.unchecked(this::createTree);
    }

    private void initExamles() {
        examples.getItems().setAll(ExampleManager.loadAllExamples());
        examples.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            try {
                input.setText(n.loadCode());
            } catch (IOException ex) {
                showErrorAlert("Cannot load the example");
            }
        });
    }

    private void initLayouts() {
        comboBox.getItems().setAll(LayoutManager.getLayoutProviders());
        comboBox.setValue(comboBox.getItems().get(0));
        comboBox.valueProperty().addListener((ov, o, n) -> {
            if (n != null) layoutTree(active, n);
        });
    }

    private void initCentering() {
        // automatické vycentrování
        scrollPane.widthProperty().addListener((ov, o, n) -> {
            if (active != null) center(pane, active);
        });
        scrollPane.heightProperty().addListener((ov, o, n) -> {
            if (active != null) center(pane, active);
        });
    }

    @FXML
    private void createTree() throws ScriptException {
        Tree tree;

        try {
            JSTreeBuilder builder = new JSTreeBuilder(settings);
            tree = builder.createTree(input.getText());
        } catch (Exception ex) {
            showErrorAlert("Wrong input\n" + ex.getLocalizedMessage());
            return;
        }

        if (tree == null || tree.isEmpty()) {
            showTree(null);
            return;
        }

        layoutTree(tree, comboBox.getValue());
    }

    public void layoutTree(Tree tree, LayoutManager.LayoutProvider provider) {
        double w = settings.getHorizontalGap() + NODE_WIDTH;
        double h = settings.getVerticalGap() + NODE_HEIGHT;

        Layout layout = provider.createInstance(w, h);
        layout.make(tree);

        showTree(tree);
    }

    public void showTree(Tree tree) {
        this.active = tree;
        if (tree == null || tree.isEmpty()) {
            scrollPane.setContent(new Pane());
            return;
        }

        List<Node> labels = new ArrayList<>();
        List<Node> lines = new ArrayList<>();

        tree.getNodes().forEach(n -> {
            n.getSuccessors().stream()
                    .map((s) -> new Line(n.getX(), n.getY(), s.getX(), s.getY()))
                    .forEach(lines::add);

            Label label = new Label("" + n.getId());
            label.setStyle("-fx-background-radius: 10px;"
                    + "-fx-padding: 0px 5px 0px 5px;"
                    + "-fx-background-color: white;"
                    + "-fx-border-color: black;"
                    + "-fx-border-radius: 10px;");

            label.setFont(Font.font("System", 14));
            Bounds size = countSize(label);

            label.setTranslateX(n.getX() - (size.getWidth() + 10) / 2);
            label.setTranslateY(n.getY() - size.getHeight() / 2);

            Tooltip tooltip = new Tooltip(n.toString());
            Tooltip.install(label, tooltip);

            labels.add(label);
        });

        this.pane = new Pane();
        pane.getChildren().addAll(lines);
        pane.getChildren().addAll(labels);

        pane.setScaleX(settings.getScale());
        pane.setScaleY(settings.getScale());

        center(pane, tree);

        scrollPane.setContent(pane);
    }

    private Bounds countSize(Label label) {
        Text dummy = new Text(label.getText());
        dummy.setFont(label.getFont());

        new Scene(new Group(dummy));
        dummy.applyCss();

        return dummy.getLayoutBounds();
    }

    private void center(Pane pane, Tree tree) {
        DoubleSummaryStatistics xStats = tree.getNodes().stream()
                .mapToDouble(TreeNode::getX).summaryStatistics();

        DoubleSummaryStatistics yStats = tree.getNodes().stream()
                .mapToDouble(TreeNode::getY).summaryStatistics();

        double xSize = xStats.getMax() - xStats.getMin();
        double ySize = yStats.getMax() - yStats.getMin();

        double dx = (scrollPane.getWidth() - xSize) / 2 - xStats.getMin();
        double dy = (scrollPane.getHeight() - ySize) / 2 - yStats.getMin();

        if (dx > 0) pane.setTranslateX(dx);
        if (dy > 0) pane.setTranslateY(dy);
    }

    /**
     * Vypise alert s vstupnim textem
     *
     * @param text Text-vypsany v Alert
     */
    private void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(text);
        alert.initOwner(getWindow());
        alert.showAndWait();
    }

    private Window getWindow() {
        //return bar.getScene().getWindow();
        return null;
    }

}
