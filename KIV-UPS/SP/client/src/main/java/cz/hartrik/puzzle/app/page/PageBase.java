package cz.hartrik.puzzle.app.page;

import cz.hartrik.puzzle.app.Application;

/**
 * An abstract page.
 *
 * @author Patrik Harag
 * @version 2017-10-17
 */
public abstract class PageBase implements Page {

    protected final Application application;

    public PageBase(Application application) {
        this.application = application;
    }

}
