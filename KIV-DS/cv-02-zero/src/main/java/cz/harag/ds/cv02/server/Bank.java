package cz.harag.ds.cv02.server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @author Patrik Harag
 * @version 2020-12-04
 */
public class Bank {

    private static final int TIMEOUT = -1;  // ms

    private static final int DEFAULT_BALANCE = 5_000_000;
    private static final int MAX_RETARDATION = 500;  // ms
    private static final int OPERATION_GEN_DELAY = 200;  // ms

    private final List<BankConnection> bankConnections;
    private final ZContext context;
    private final Map<Integer, Snapshot> snapshots;
    private int balance;

    // TODO: restrict capacity & correct addition
    private final BlockingQueue<FutureTask<?>> queue = new LinkedBlockingQueue<>();

    public Bank(List<BankConnection> bankConnections) {
        this.bankConnections = bankConnections;
        this.context = new ZContext();
        this.balance = DEFAULT_BALANCE;
        this.snapshots = new LinkedHashMap<>();
    }

    public synchronized int getBalance() {
        return balance;
    }

    public void bindAll() {
        for (final BankConnection connection : bankConnections) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bind(connection);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void connectAll() {
        for (final BankConnection connection : bankConnections) {
            connect(connection);
        }
    }

    /**
     * Starts operation generation.
     */
    public void startGenerator() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                while (true) {
                    try {
                        Thread.sleep(OPERATION_GEN_DELAY);
                    } catch (InterruptedException e) {
                        return;
                    }
                    try {
                        generateOp(random);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void startWorker() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    FutureTask<?> futureTask;
                    try {
                        futureTask = queue.take();
                    } catch (InterruptedException e) {
                        continue;
                    }
                    synchronized (this) {
                        futureTask.run();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void bind(final BankConnection address) throws IOException {
        ZMQ.Socket socket = context.createSocket(SocketType.PAIR);
        socket.bind("tcp://" + address.getBindAddress());
        log("Bind complete: %s%n", address.getBindAddress());

        while (!Thread.currentThread().isInterrupted()) {
            // Block until a message is received
            byte[] rawMsg = socket.recv(0); retardation();
            String stringMsg = new String(rawMsg, ZMQ.CHARSET);
            ObjectMapper objectMapper = new ObjectMapper();
            final MessageOperation op = objectMapper.readValue(stringMsg, MessageOperation.class);

            retardation();

            FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
                @Override
                public Boolean call() throws IOException {
                    return processOperation(address, op);
                }
            });
            queue.add(task);

            socket.send("accepted".getBytes(ZMQ.CHARSET), 0);
        }
    }

    private boolean processOperation(BankConnection address, MessageOperation op) throws IOException {
        if (op.getOperation() == OperationType.MARKER) {
            int marker = op.getValue();
            log("%s OP: %s%n", address.getConnectAddress(), op);

            Snapshot snapshot = snapshots.get(marker);
            if (snapshot == null) {
                // Received the marker first time
                snapshot = new Snapshot(marker, balance);
                snapshots.put(marker, snapshot);

                // Propagate...
                for (BankConnection bank : bankConnections) {
                    send(bank, op);
                }
            }

            if (snapshot.getMarkersGot().add(address)) {
                if (testIsSnapshotFinished(snapshot)) {
                    snapshot.setFinished();
                    log("SNAPSHOT FINISHED: %s%n", snapshot);
                }
            } else {
                // Already received the marker from this bank...
            }
        } else {
            // process snapshots
            for (Snapshot snapshot : snapshots.values()) {
                if (!snapshot.isFinished() && !snapshot.getMarkersGot().contains(address)) {
                    snapshot.getOperations().add(op);
                }
            }

            // process operation itself
            if (op.getOperation() == OperationType.CREDIT) {
                balance += op.getValue();
                log("%s OP: %s, status: %d%n", address.getConnectAddress(), op, balance);
            } else if (op.getOperation() == OperationType.DEBIT) {
                if (balance >= op.getValue()) {
                    balance -= op.getValue();
                    log("%s OP: %s, status: %d%n", address.getConnectAddress(), op, balance);
                    send(address, new MessageOperation(op.getValue(), OperationType.CREDIT));
                    return true;
                } else {
                    log("%s OP: %s FAILED, status: %d%n", address.getConnectAddress(), op, balance);
                    send(address, new MessageOperation(op.getValue(), OperationType.DEBIT_REFUSED));
                    return false;
                }
            } else if (op.getOperation() == OperationType.DEBIT_REFUSED) {
                log("%s OP: %s, status: %d%n", address.getConnectAddress(), op, balance);
            }
        }
        return true;
    }

    private void connect(BankConnection address) {
        ZMQ.Socket socket = context.createSocket(SocketType.PAIR);
        socket.connect("tcp://" + address.getConnectAddress());
        socket.setSendTimeOut(TIMEOUT);
        socket.setReceiveTimeOut(TIMEOUT);
        address.setConnectSocket(socket);
    }

    private void generateOp(Random random) throws ExecutionException, InterruptedException {
        final BankConnection rndAddress = bankConnections.get(random.nextInt(bankConnections.size()));
        final int rndValue = 10_000 + random.nextInt(40_000);

        if (random.nextBoolean()) {
            // send money to rnd bank
            FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    try {
                        int rndValueMin = Math.min(balance, rndValue);
                        balance -= rndValueMin;
                        MessageOperation operation = new MessageOperation(rndValueMin, OperationType.CREDIT);
                        log("%s GEN: %s, status: %d%n", rndAddress.getConnectAddress(), operation, balance);
                        return send(rndAddress, operation);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            queue.add(task);
            if (!task.get()) {
                throw new IllegalStateException();
            }
        } else {
            // get money from rnd bank
            final MessageOperation operation = new MessageOperation(rndValue, OperationType.DEBIT);
            FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    try {
                        log("%s GEN: %s%n", rndAddress.getConnectAddress(), operation);
                        return send(rndAddress, operation);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            queue.add(task);
            if (!task.get()) {
                // never mind - valid state
            }
        }
    }

    private boolean send(BankConnection address, MessageOperation operation) throws IOException {
        ZMQ.Socket socket = address.getConnectSocket();
        ObjectMapper objectMapper = new ObjectMapper();

        String data = objectMapper.writeValueAsString(operation);
        socket.send(data.getBytes(ZMQ.CHARSET), 0);

        // Block until a message is received
        byte[] rawMsg = socket.recv(0);
        String stringMsg = new String(rawMsg, ZMQ.CHARSET);
        return stringMsg.equalsIgnoreCase("accepted");
    }

    private void retardation() {
        try {
            Thread.sleep((long) (Math.random() * MAX_RETARDATION));
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public boolean testIsSnapshotFinished(Snapshot snapshot) {
        if (snapshot != null) {
            return snapshot.getMarkersGot().containsAll(bankConnections);
        } else {
            return false;
        }
    }

    public Snapshot getSnapshot(int marker) {
        return snapshots.get(marker);
    }

    public Set<Integer> getSnapshots() {
        return snapshots.keySet();
    }

    public int scheduleSnapshot(final int marker) {
        FutureTask<Void> task = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws IOException {
                MessageOperation operation = new MessageOperation(marker, OperationType.MARKER);
                log("OP: %s (REST API)%n", operation);
                Snapshot snapshot = new Snapshot(marker, balance);
                snapshots.put(marker, snapshot);
                for (BankConnection bank : bankConnections) {
                    send(bank, operation);
                }
                return null;
            }
        });
        queue.add(task);
        return marker;
    }

    private void log(String format, Object ... args) {
        synchronized (this) {
            System.out.print(System.currentTimeMillis() + " ");
            System.out.printf(format, args);
            System.out.flush();
        }
    }

}
