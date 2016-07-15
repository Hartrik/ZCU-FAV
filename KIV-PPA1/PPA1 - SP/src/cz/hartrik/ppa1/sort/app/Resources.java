package cz.hartrik.ppa1.sort.app;

import java.net.URL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @version 2015-10-28
 * @author Patrik Harag
 */
public final class Resources {

    private Resources() {}

    private static final String IMG_FRAME_ICON = "icon - chart.png";

    private static final String IMG_REPLAY = "icon - restart.png";
    private static final String IMG_PLAY = "icon - play.png";
    private static final String IMG_PAUSE = "icon - pause.png";
    private static final String IMG_NEXT = "icon - next.png";
    private static final String IMG_SKIP = "icon - skip.png";

    private static Image image(String filename) {
        return new Image(Resources.class.getResourceAsStream(filename));
    }

    public static Image getFrameIcon() {
        return image(IMG_FRAME_ICON);
    }

    private static ImageView imageView(String filename) {
        return new ImageView(image(filename));
    }

    public static ImageView getIconPlay() {
        return imageView(IMG_PLAY);
    }

    public static ImageView getIconPause() {
        return imageView(IMG_PAUSE);
    }

    public static ImageView getIconReplay() {
        return imageView(IMG_REPLAY);
    }

    public static ImageView getIconNext() {
        return imageView(IMG_NEXT);
    }

    public static ImageView getIconSkip() {
        return imageView(IMG_SKIP);
    }

    public static URL getCSS() {
        return Resources.class.getResource("styles.css");
    }

}