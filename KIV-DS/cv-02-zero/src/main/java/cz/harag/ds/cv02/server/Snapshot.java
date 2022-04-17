package cz.harag.ds.cv02.server;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Patrik Harag
 * @version 2020-12-05
 */
public class Snapshot {

    private final int marker;

    private final int status;
    private final List<MessageOperation> operations;
    private final Set<BankConnection> markersGot;  // set of connections we got the marker from
    private boolean finished;

    public Snapshot(int marker, int initialBalance) {
        this.marker = marker;
        this.status = initialBalance;
        this.operations = new ArrayList<>();
        this.markersGot = new LinkedHashSet<>();
    }

    public int getStatus() {
        return status;
    }

    public List<MessageOperation> getOperations() {
        return operations;
    }

    public Set<BankConnection> getMarkersGot() {
        return markersGot;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished() {
        this.finished = true;
    }

    @Override
    public String toString() {
        int opSum = 0;
        for (MessageOperation operation : operations) {
            if (operation.getOperation() == OperationType.CREDIT) {
                opSum += operation.getValue();
            } else {
                // not decremented yet
            }
        }

        return "Snapshot{" +
                "marker=" + marker +
                ", status=" + status +
                ", operations=" + operations +
                ", operationsSum=" + opSum +
                ", finished=" + finished +
                '}';
    }
}