package cz.harag.ds.cv01.server.model;

/**
 * @author Patrik Harag
 * @version 2020-10-15
 */
public class ServerRequest implements Comparable<ServerRequest> {

    private Integer value;
    private Long id;

    public ServerRequest() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ServerRequest{" +
                "value=" + value +
                ", id=" + id +
                '}';
    }

    @Override
    public int compareTo(ServerRequest o) {
        return Long.compare(id, o.id);
    }
}
