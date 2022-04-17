package cz.harag.ds.cv01.server.model;

/**
 * @author Patrik Harag
 * @version 2020-10-15
 */
public class ShufflerRequest {

    private Integer value;
    private Long id;

    public ShufflerRequest() {
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
        return "ShufflerRequest{" +
                "value=" + value +
                ", id=" + id +
                '}';
    }
}
