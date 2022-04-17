package cz.harag.ds.cv02.server;

/**
 * @author Patrik Harag
 * @version 2020-12-04
 */
public class MessageOperation {

    private int value;
    private OperationType operation;

    public MessageOperation() {
    }

    public MessageOperation(int value, OperationType operation) {
        this.value = value;
        this.operation = operation;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return operation.name().toLowerCase() + " " + value;
    }
}
