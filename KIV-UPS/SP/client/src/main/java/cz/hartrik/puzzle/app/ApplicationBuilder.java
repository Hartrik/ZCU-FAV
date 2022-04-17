package cz.hartrik.puzzle.app;

import java.net.URL;

/**
 * Stará se o postavení aplikace z modulů.
 *
 * @version 2017-09-28
 * @author Patrik Harag
 */
class ApplicationBuilder {

    private ApplicationBuilder() {}

    static Application build(ApplicationModule... modules) {
        URL fxml = FrameStage.class.getResource("Frame.fxml");
        FrameStage frameStage = new FrameStage(fxml);

        Application app = new Application(frameStage, frameStage.getFrameController());
        for (ApplicationModule module : modules) {
            module.init(app);
        }

        return app;
    }

}