package cz.zcu.kiv.pt.sp.app;

import cz.zcu.kiv.pt.sp.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Window;

/**
 *
 * @version 2016-10-23
 * @author Patrik Harag
 * @author Michal Fiala
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private MenuBar bar;

    /**
     * TextArea pro vstupní řetězec
     */
    @FXML
    private TextArea textInPut;

    /**
     * TextArea pro výstup nalezeného slova
     */
    @FXML
    private TextArea textOutPut;

    /**
     * TextField pro vyhledávání slova
     */
    @FXML
    private TextField textFind;

    /**
     * Stávající používaný slovník
     */
    private Dictionary dict = new HashSetDictionary();

    private final FileChooserManager fileChooser = new FileChooserManager(this::getWindow);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMenu();
    }

    /**
     * Vytvoření lišty pro menu a jeho itemy s jejich funkcemi.
     */
    private void initMenu() {
        //Inicializace
        Menu fileMenu = new Menu("_File");
        MenuItem exit = new MenuItem("E_xit");
        MenuItem save = new MenuItem("S_ave dictionary");
        MenuItem saveOut = new MenuItem("S_ave output");
        MenuItem open = new MenuItem("O_pen");
        MenuItem openDict = new MenuItem("O_pen dictionary");

        //Nastaveni funkci
        exit.setOnAction((event) -> Platform.exit());
        open.setOnAction((event) -> openInputText());
        openDict.setOnAction((event) -> openDictionary());
        save.setOnAction((event) -> saveDictionary());
        saveOut.setOnAction((event) -> saveOutput());

        //Vložení všech komponent do Menu
        fileMenu.getItems().setAll(open, openDict, save, saveOut, exit);
        bar.getMenus().setAll(fileMenu);
    }

    /**
     * Vytvoří slovník z obsahu vstupního pole.
     */
    @FXML
    private void createDictionary() {
        if (!textInPut.getText().isEmpty()) {
            // Vytvoři slovník z obsahu TextArea
            DictionaryCreator creator = new DictionaryCreator();
            dict = creator.createDictionary(textInPut.getText());
            // Alert
            showInformationAlert("Dictionary creator was created succesfully");
        }
    }

    @FXML
    private void findWord() {
        // Pokud neni vstup prazdny
        if (textFind.getText().isEmpty()) {
            return;
        }

        final String findingWord = textFind.getText();
        // Premazani predchoziho vystupu
        textOutPut.setText("");
        // Vytvoreni vyhledavani
        SearchProvider searchClass = new TrieSearch(textInPut.getText());
        List<SearchResult> result = searchClass.search(findingWord);
        // Pokud nebylo nalezeno slovo
        if (result.isEmpty()) {
            // Not Found
            textOutPut.setText("Similar words in dictionary:" + "\n");
            // Vypsani nejbližších 10 slov pomocí Levensteinovi vzdálenosti
            dict.asSet().stream()
                    .sorted(Comparator.comparingInt(s -> LevenshteinDistance.compute(findingWord, s)))
                    .limit(10)
                    .forEach((x) -> textOutPut.appendText(x + "\n"));
        } else {
            // Vypis nalezenych indexu
            // + Vypis poctu nalezenych slov
            for (SearchResult each : result) {
                textOutPut.appendText(each.toString() + "\n");
            }
            // Vypsani poctu nalezenych slov
            textOutPut.appendText("Number of words found: " + result.size() + "\n");
        }
    }

    @FXML
    private void addWordToDictionary() {
        //jeli pole vyplnene
        if (!textFind.getText().isEmpty()) {
            String word = textFind.getText();
            String alert;
            //Pokud už slovnik obsahuje toto slovo
            if (dict.contains(word)) {
                alert = "Dictionary already contains word: " + word;
            } //pokud slovnik neobsahuje toto slovo
            else {
                dict.add(word);
                alert = "Word: " + word + " was added to Dictiaonary:";
            }
            showInformationAlert(alert);
        }
    }

    /**
     * Otevře windows fileChooser a po vybrání souboru vypíše obsah souboru do
     * vstupní TextArea.
     */
    @FXML
    private void openInputText() {
        fileChooser.showOpenDialog("Open Resource File").ifPresent((file) -> {
            //Vypsání při kodování UTF-8
            try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                String text = lines.collect(Collectors.joining("\n"));
                textInPut.appendText(text);
            } catch (Exception ex) {
                showErrorAlert("Cannot load text file");
            }
        });
    }

    /**
     * Otevře widnows fileChooser a po vybrání souboru načte slovník z tohoto
     * souboru.
     */
    @FXML
    private void openDictionary() {
        fileChooser.showOpenDialog("Open Resource Dictionary").ifPresent((file) -> {
            try {
                //Načtení slovníku
                dict = DictionaryIO.loadDictionary(file);
                //Alert
                showInformationAlert("Dictionary was loaded");
            } catch (Exception ex) {
                showErrorAlert("Cannot load dictionary");
            }
        });
    }

    /**
     * Uloží výstup pomocí file chooseru.
     */
    @FXML
    private void saveOutput() {
        fileChooser.showSaveDialog("Save Output").ifPresent((file) -> {
            try {
                // Vepsání do souboru
                byte[] data = textOutPut.getText().getBytes(StandardCharsets.UTF_8);
                Files.write(file, data);
                // Alert
                showInformationAlert("Output was saved");
            } catch (Exception ex) {
                showErrorAlert("Error occurred while writing a file");
            }
        });
    }

    /**
     * Uloží slovník pomocí file chooseru.
     */
    @FXML
    private void saveDictionary() {
        fileChooser.showSaveDialog("Save Dictionary").ifPresent((file) -> {
            try {
                //Vepsání do souboru
                DictionaryIO.writeDictionary(file, dict);
                //Alert
                showInformationAlert("Dictionary was saved");
            } catch (Exception ex) {
                showErrorAlert("Error occurred while writing a file");
            }
        });
    }

    /**
     * Vypise alert s vstupnim textem
     *
     * @param text Text-vypsany v Alert
     */
    private void showInformationAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(text);
        alert.initOwner(getWindow());
        alert.showAndWait();
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
        return textInPut.getScene().getWindow();
    }

}
