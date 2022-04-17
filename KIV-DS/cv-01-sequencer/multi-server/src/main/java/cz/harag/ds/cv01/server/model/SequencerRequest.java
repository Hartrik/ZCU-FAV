package cz.harag.ds.cv01.server.model;

/**
 * @author Patrik Harag
 * @version 2020-10-15
 */
public class SequencerRequest {

    private Integer value;

    public SequencerRequest() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SequencerRequest{" +
                "value=" + value +
                '}';
    }
}
