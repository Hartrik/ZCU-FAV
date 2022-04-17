package cz.harag.ds.cv01.server;

import cz.harag.ds.cv01.server.rest.seqencer.SequencerEndpoint;
import cz.harag.ds.cv01.server.rest.shuffler.ShufflerEndpoint;
import cz.harag.ds.cv01.server.rest.server.ServerEndpoint;

/**
 * @author Patrik Harag
 * @version 2020-10-15
 */
public enum ServerType {

    SEQUENCER(SequencerEndpoint.class),
    SHUFFLER(ShufflerEndpoint.class),
    SERVER(ServerEndpoint.class);


    private final Class<?> servletClass;

    ServerType(Class<?> servletClass) {
        this.servletClass = servletClass;
    }

    public Class<?> getServletClass() {
        return servletClass;
    }
}
