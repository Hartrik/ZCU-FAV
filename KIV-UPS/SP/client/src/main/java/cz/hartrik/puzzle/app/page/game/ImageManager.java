package cz.hartrik.puzzle.app.page.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Manages available images.
 *
 * @author Patrik Harag
 * @version 2017-10-15
 */
public class ImageManager {

    private static final String[] imageNames = {
            "kiwi.jpg",
            "dog.jpg",
            "squirrel.jpg"
    };

    private static final ImageManager instance = new ImageManager();

    public static ImageManager getInstance() {
        return instance;
    }


    private final List<Image> images;

    private ImageManager() {
        this.images = new ArrayList<>(imageNames.length);

        for (String imageName : imageNames) {
            Image image = new Image(getClass().getResourceAsStream(imageName));
            images.add(image);
        }
    }

    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }

    public Image getImageBySize(int w, int h) {
        for (Image image : getImages()) {
            if ((int) image.getWidth() / Piece.SIZE == w
                    && (int) image.getHeight() / Piece.SIZE == h) {

                return image;
            }
        }

        return generate(w * Piece.SIZE, h * Piece.SIZE);
    }

    private Image generate(int width, int height) {
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color = Color.rgb(0,
                        (int) ((i) * (255.f / width)),
                        (int) ((j) * (255.f / height)));
                writer.setColor(i, j, color);
            }
        }
        return image;
    }

}
