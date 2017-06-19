package cz.harag.uir.c03;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Immutable structure.
 *
 * @author Patrik Harag
 * @version 2017-03-11
 */
public class HanoiState {

    static final int PEGS = 3;

    private final LinkedList<Integer> peg1;
    private final LinkedList<Integer> peg2;
    private final LinkedList<Integer> peg3;

    private final int level;
    private final String origin;

    private HanoiState(LinkedList<Integer> p1, LinkedList<Integer> p2,
                       LinkedList<Integer> p3, int level, String origin) {

        this.peg1 = p1;
        this.peg2 = p2;
        this.peg3 = p3;
        this.level = level;
        this.origin = origin;
    }

    public int getLevel() {
        return level;
    }

    public boolean canMove(int from, int to) {
        if (from == to) return false;  // same peg

        LinkedList<Integer> fromPeg = getPeg(from);
        if (fromPeg.isEmpty()) return false;  // nothing to move

        LinkedList<Integer> toPeg = getPeg(to);
        if (toPeg.isEmpty()) return true;

        return fromPeg.getFirst() < toPeg.getFirst();
    }

    public HanoiState move(int from, int to) {
        if (!canMove(from, to)) throw new IllegalArgumentException();

        HanoiState copy = new HanoiState(
                new LinkedList<>(peg1),
                new LinkedList<>(peg2),
                new LinkedList<>(peg3),
                level + 1,
                String.format("move from %d to %d (parent=%h)", from, to, hashCode())
        );

        LinkedList<Integer> fromPeg = copy.getPeg(from);
        Integer fromVal = fromPeg.removeFirst();

        LinkedList<Integer> toPeg = copy.getPeg(to);
        toPeg.addFirst(fromVal);

        return copy;
    }

    public boolean isFinalState() {
        return peg1.isEmpty() && peg2.isEmpty();
    }

    private LinkedList<Integer> getPeg(int i) {
        switch (i) {
            case 0: return peg1;
            case 1: return peg2;
            case 2: return peg3;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public static HanoiState createInitialState(int n) {
        LinkedList<Integer> all = IntStream.range(0, n)
                .boxed()
                .collect(Collectors.toCollection(LinkedList::new));

        LinkedList<Integer> empty = new LinkedList<>();

        return new HanoiState(all, empty, empty, 0, "initial state");
    }

    @Override
    public String toString() {
        return String.format("state: %-30.50s | id: %-10.10h | level:%-5d | origin: %s",
                 Arrays.asList(peg1, peg2, peg3), hashCode(), level, origin);
    }
}
