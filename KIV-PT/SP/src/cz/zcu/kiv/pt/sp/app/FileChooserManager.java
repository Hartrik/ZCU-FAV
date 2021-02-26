package cz.zcu.kiv.pt.sp.app;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Spravuje file chooser.
 *
 * @version 2016-10-24
 * @author Patrik Harag
 */
public class FileChooserManager {

    private FileChooser fileChooser;
    private final Supplier<Window> windowSupplier;

    public FileChooserManager(Supplier<Window> windowSupplier) {
        this.windowSupplier = windowSupplier;
    }

    private FileChooser getDialog() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("."));
        }
        return fileChooser;
    }

    /**
     * Vytvoří dialog pro uložení souboru.
     *
     * @param title nadpis
     * @return Optional
     */
    public Optional<Path> showSaveDialog(String title) {
        FileChooser dialog = getDialog();
        dialog.setTitle(title);

        File file = dialog.showSaveDialog(windowSupplier.get());
        return Optional.ofNullable(file).map(File::toPath);
    }

    /**
     * Vytvoří dialog pro načítání.
     *
     * @param title nadpis
     * @return Optional
     */
    public Optional<Path> showOpenDialog(String title) {
        FileChooser dialog = getDialog();
        dialog.setTitle(title);

        File file = dialog.showOpenDialog(windowSupplier.get());
        return Optional.ofNullable(file).map(File::toPath);
    }

}